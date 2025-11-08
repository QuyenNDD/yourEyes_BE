import pandas as pd
from scipy.sparse import csr_matrix
from sklearn.neighbors import NearestNeighbors
import pyodbc
import numpy as np
from datetime import datetime
import pickle

# -------------------------------------------------------------------
# BƯỚC 1: KHAI BÁO CẤU HÌNH
# -------------------------------------------------------------------
SERVER_NAME = 'DESKTOP-BQB1VDP' # <<<!!! THAY ĐỔI TÊN SERVER CỦA BẠN !!!>>>
DATABASE_NAME = 'YourEyes'
DRIVER = '{ODBC Driver 17 for SQL Server}'

ACTION_WEIGHTS = {'VIEW_DETAIL': 1, 'ADD_TO_CART': 3, 'ORDER': 5}
K_NEIGHBORS = 10 
N_RECOMMENDATIONS = 20 

def get_db_connection():
    """Tạo và trả về một kết nối CSDL."""
    connection_string = f"""
        DRIVER={DRIVER};
        SERVER={SERVER_NAME};
        DATABASE={DATABASE_NAME};
        Trusted_Connection=yes;
    """
    try:
        conn = pyodbc.connect(connection_string)
        return conn
    except Exception as e:
        print(f"LỖI NGHIÊM TRỌNG: Không thể kết nối CSDL.")
        print(f"Chi tiết: {e}")
        raise

# -------------------------------------------------------------------
# BƯỚC 2: CÁC HÀM TẢI DỮ LIỆU VÀ HUẤN LUYỆN
# -------------------------------------------------------------------

def load_interaction_data(conn):
    """Tải 100% dữ liệu tương tác và áp dụng trọng số."""
    print("Đang tải dữ liệu tương tác (User_Product_Activity)...")
    sql_query = """
        SELECT user_id, product_id, action_type 
        FROM User_Product_Activity
        WHERE user_id IS NOT NULL AND product_id IS NOT NULL
    """
    df = pd.read_sql(sql_query, conn)
    if df.empty:
        raise ValueError("Không có dữ liệu tương tác.")
    df['score'] = df['action_type'].map(ACTION_WEIGHTS).fillna(0)
    df = df[df['score'] > 0]
    print(f"Đã tải {len(df)} tương tác hợp lệ.")
    return df

def load_filter_data(conn):
    """Tải dữ liệu hồ sơ (profile) và sản phẩm (product) để LỌC."""
    print("Đang tải dữ liệu lọc (Profiles & Products)...")
    
    df_profiles = pd.read_sql("SELECT user_id, gender, height, weight FROM user_profile", conn)
    # Đặt user_id làm index để tra cứu nhanh
    df_profiles = df_profiles.set_index('user_id')
    
    df_products = pd.read_sql("SELECT id, gender_target, size FROM products", conn)
    # Đặt product_id làm index để tra cứu nhanh
    df_products = df_products.set_index('id')
    
    print(f"Đã tải {len(df_profiles)} hồ sơ và {len(df_products)} sản phẩm.")
    return df_profiles, df_products

def load_cluster_models():
    """Tải mô hình K-Means và Bộ tiền xử lý."""
    print("Đang tải mô hình K-Means (kmeans_model.pkl)...")
    with open('kmeans_model.pkl', 'rb') as f:
        kmeans_model = pickle.load(f)
    
    print("Đang tải Bộ tiền xử lý (preprocessor.pkl)...")
    with open('preprocessor.pkl', 'rb') as f:
        preprocessor = pickle.load(f)
        
    return kmeans_model, preprocessor

def train_knn_model(df):
    """Huấn luyện mô hình K-NN (Item-based) trên toàn bộ dữ liệu."""
    print("Đang xây dựng Ma trận Tương tác (K-NN)...")
    utility_df = df.groupby(['user_id', 'product_id'])['score'].sum().unstack().fillna(0)
    
    item_utility_matrix = csr_matrix(utility_df.transpose().values)
    n_items = item_utility_matrix.shape[0]
    k_fit = min(K_NEIGHBORS, n_items - 1)
    
    if k_fit <= 0:
        raise ValueError(f"Chỉ có {n_items} sản phẩm. Không đủ để huấn luyện K-NN.")

    print(f"Đang huấn luyện K-NN với K={k_fit}...")
    model_knn = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=k_fit)
    model_knn.fit(item_utility_matrix)
    
    print("Huấn luyện K-NN thành công!")
    return model_knn, utility_df, item_utility_matrix

# -------------------------------------------------------------------
# BƯỚC 3: HÀM LỌC VÀ GHI CSDL
# -------------------------------------------------------------------

def get_size_match(user_height, user_weight, product_size):
    """
    *** HÀM LỌC SIZE NÂNG CAO (VÍ DỤ) ***
    Đây là logic nghiệp vụ của bạn. Bạn cần định nghĩa nó.
    Ví dụ: 1m91, 87kg (như User 1002) thì nên mặc size 'XL' hoặc 'XXL'.
    """
    # Xử lý logic cơ bản
    if not product_size or pd.isna(product_size):
        return True # Nếu sản phẩm không có size (ví dụ: phụ kiện), luôn khớp
    if not user_height or not user_weight:
        return True # Nếu user không có hồ sơ, tạm thời bỏ qua lọc size

    # Logic ví dụ rất đơn giản (BẠN CẦN THAY THẾ BẰNG LOGIC TỐT HƠN)
    product_size = str(product_size).upper()
    if user_height > 180 or user_weight > 80:
        return product_size in ['XL', 'XXL']
    elif user_height > 170 or user_weight > 65:
        return product_size in ['L', 'XL']
    elif user_height > 160 or user_weight > 50:
         return product_size in ['M', 'L']
    else:
        return product_size in ['S', 'M']

def generate_hybrid_recommendations(model_knn, utility_df, item_utility_matrix, df_profiles, df_products):
    """Tạo gợi ý K-NN và lọc bằng dữ liệu Profile/Product."""
    print("Bắt đầu tạo gợi ý HYBRID cho tất cả người dùng...")
    
    # 1. Tạo bản đồ tra cứu Item-Item (như script cũ)
    item_id_to_index = {pid: i for i, pid in enumerate(utility_df.columns)}
    item_index_to_id = {i: pid for i, pid in enumerate(utility_df.columns)}
    n_items_fit = model_knn.n_samples_fit_
    k_neighbors_fit = min(K_NEIGHBORS + 1, n_items_fit)
    
    print(f"  Đang tính toán bản đồ tương tự (Item-Item map)...")
    all_distances, all_indices = model_knn.kneighbors(item_utility_matrix, n_neighbors=k_neighbors_fit)
    
    item_similarity_map = {}
    for i in range(n_items_fit):
        original_item_id = item_index_to_id[i]
        neighbors = []
        for j in range(1, len(all_indices[i])): 
            neighbor_id = item_index_to_id[all_indices[i][j]]
            similarity_score = 1 - all_distances[i][j]
            neighbors.append((neighbor_id, similarity_score))
        item_similarity_map[original_item_id] = neighbors

    # 2. Tạo gợi ý VÀ LỌC cho từng user
    print(f"  Đang tạo và lọc gợi ý cho {len(utility_df.index)} người dùng...")
    all_final_recommendations = []
    generation_time = datetime.now()
    
    for user_id, user_vector in utility_df.iterrows():
        # Lấy hồ sơ (profile) của user này để lọc
        try:
            user_profile = df_profiles.loc[user_id]
            user_gender = user_profile['gender']
            user_height = user_profile['height']
            user_weight = user_profile['weight']
        except KeyError:
            # User này có tương tác nhưng không có trong bảng profile
            user_gender, user_height, user_weight = None, None, None

        items_user_has_interacted = user_vector[user_vector > 0]
        items_to_exclude = set(items_user_has_interacted.index)
        
        recommendation_scores = {}

        # Tạo gợi ý thô từ K-NN
        for interacted_item_id, interaction_score in items_user_has_interacted.items():
            if interacted_item_id in item_similarity_map:
                similar_items = item_similarity_map[interacted_item_id]
                for neighbor_id, similarity_score in similar_items:
                    if neighbor_id not in items_to_exclude:
                        final_score = similarity_score * interaction_score
                        recommendation_scores[neighbor_id] = recommendation_scores.get(neighbor_id, 0) + final_score
        
        # 3. ÁP DỤNG BỘ LỌC
        filtered_recs = []
        for product_id, score in recommendation_scores.items():
            try:
                product_details = df_products.loc[product_id]
                
                # --- BỘ LỌC 1: GIỚI TÍNH ---
                prod_gender = product_details['gender_target']
                if user_gender and prod_gender and prod_gender.lower() != 'unisex' and user_gender.lower() != prod_gender.lower():
                    continue # Bỏ qua vì không hợp giới tính

                # --- BỘ LỌC 2: SIZE (DÙNG HÀM NÂNG CAO) ---
                prod_size = product_details['size']
                if not get_size_match(user_height, user_weight, prod_size):
                    continue # Bỏ qua vì không hợp size

                # Nếu vượt qua tất cả bộ lọc:
                filtered_recs.append((product_id, score))

            except KeyError:
                continue # Bỏ qua nếu sản phẩm không có trong bảng products
        
        # Sắp xếp và lấy Top N
        sorted_recommendations = sorted(filtered_recs, key=lambda item: item[1], reverse=True)
        top_n = sorted_recommendations[:N_RECOMMENDATIONS]
        
        for product_id, score in top_n:
            all_final_recommendations.append((user_id, product_id, score, generation_time))

    print(f"  Đã tạo xong {len(all_final_recommendations)} bản ghi gợi ý (ĐÃ LỌC).")
    return all_final_recommendations

def save_recommendations_to_db(recommendations_data, conn):
    """Xóa các gợi ý cũ và lưu các gợi ý mới vào CSDL."""
    if not recommendations_data:
        print("Không có gợi ý nào được tạo ra. Bỏ qua việc ghi CSDL.")
        return
    
    print("Đang ghi gợi ý vào CSDL [recommendations]...")
    cursor = conn.cursor()
    try:
        cursor.execute("TRUNCATE TABLE [recommendations]")
        sql_insert = "INSERT INTO [recommendations] (user_id, product_id, score, generated_at) VALUES (?, ?, ?, ?)"
        cursor.fast_executemany = True 
        cursor.executemany(sql_insert, recommendations_data)
        conn.commit()
        print(f"  Ghi thành công {len(recommendations_data)} gợi ý vào CSDL!")
    except Exception as e:
        print(f"LỖI: Không thể ghi vào CSDL. Đang rollback...")
        conn.rollback()
        print(f"Chi tiết: {e}")
    finally:
        cursor.close()

# -------------------------------------------------------------------
# BƯỚC 4: HÀM MAIN ĐỂ CHẠY
# -------------------------------------------------------------------
def main():
    """Hàm chính điều phối toàn bộ quá trình."""
    print(f"--- BẮT ĐẦU CHẠY TÁC VỤ HYBRID (Production) ---")
    print(f"Thời gian: {datetime.now()}")
    print("-" * 40)
    
    conn = None
    try:
        conn = get_db_connection()
        
        # 1. Tải dữ liệu tương tác (cho K-NN)
        interaction_df = load_interaction_data(conn)
        
        # 2. Tải dữ liệu lọc (cho Bộ lọc)
        df_profiles, df_products = load_filter_data(conn)
        
        # 3. Tải các mô hình K-Means (lúc này chưa dùng, nhưng sẽ dùng ở bước sau)
        # model_kmeans, preprocessor = load_cluster_models() 
        # (Chúng ta chưa dùng K-Means trong script này, 
        #  chỉ dùng dữ liệu profile trực tiếp để lọc. 
        #  K-Means dùng để giải quyết Cold Start, là một logic riêng.)

        # 4. Huấn luyện K-NN
        model_knn, utility_df, item_matrix = train_knn_model(interaction_df)
        
        # 5. Tạo gợi ý HYBRID (K-NN + Lọc Giới tính/Size)
        recommendations = generate_hybrid_recommendations(
            model_knn, utility_df, item_matrix, 
            df_profiles, df_products
        )
        
        # 6. Lưu kết quả vào CSDL
        save_recommendations_to_db(recommendations, conn)
        
        print("-" * 40)
        print("--- TÁC VỤ HYBRID HOÀN THÀNH ---")

    except ValueError as ve:
        print(f"\n--- TÁC VỤ DỪNG LẠI ---")
        print(f"Lỗi dữ liệu: {ve}")
    except Exception as e:
        print(f"\n--- TÁC VỤ THẤT BẠI ---")
        print(f"Lỗi không xác định: {e}")
    finally:
        if conn:
            conn.close()
            print("Đã đóng kết nối CSDL.")

if __name__ == "__main__":
    main()