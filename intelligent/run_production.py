import pandas as pd
from scipy.sparse import csr_matrix
from sklearn.neighbors import NearestNeighbors
import pyodbc
import numpy as np
from datetime import datetime

# -------------------------------------------------------------------
# BƯỚC 1: KHAI BÁO CẤU HÌNH
# -------------------------------------------------------------------
# <<<!!! HÃY ĐIỀN THÔNG SỐ CỦA BẠN VÀO ĐÂY !!!>>>
SERVER_NAME = 'DESKTOP-BQB1VDP' 
DATABASE_NAME = 'YourEyes'
DRIVER = '{ODBC Driver 17 for SQL Server}'
# <<<!!! HẾT PHẦN CẤU HÌNH !!!>>>

# Trọng số cho các hành động
ACTION_WEIGHTS = {
    'VIEW_DETAIL': 1,
    'ADD_TO_CART': 3,
    'ORDER': 5
}
# Số lượng hàng xóm (sản phẩm tương tự) để tìm
K_NEIGHBORS = 10 
# Số lượng gợi ý cuối cùng cho mỗi user
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

def load_data(conn):
    """Tải 100% dữ liệu tương tác và áp dụng trọng số."""
    print("Đang tải 100% dữ liệu tương tác từ CSDL...")
    sql_query = """
        SELECT user_id, product_id, action_type 
        FROM User_Product_Activity
        WHERE user_id IS NOT NULL AND product_id IS NOT NULL
    """
    df = pd.read_sql(sql_query, conn)
    
    if df.empty:
        raise ValueError("Không có dữ liệu tương tác nào trong CSDL để huấn luyện.")

    df['score'] = df['action_type'].map(ACTION_WEIGHTS).fillna(0)
    df = df[df['score'] > 0]
    
    print(f"Đã tải thành công {len(df)} tương tác hợp lệ.")
    return df

def train_model(df):
    """Huấn luyện mô hình K-NN (Item-based) trên toàn bộ dữ liệu."""
    print("Đang xây dựng Ma trận Tương tác...")
    utility_df = df.groupby(['user_id', 'product_id'])['score'].sum().unstack().fillna(0)
    
    print("Đang chuyển vị ma trận (Item-based)...")
    item_utility_matrix = csr_matrix(utility_df.transpose().values)
    
    n_items = item_utility_matrix.shape[0]
    # Điều chỉnh K nếu số sản phẩm quá ít
    k_fit = min(K_NEIGHBORS, n_items - 1)
    
    if k_fit <= 0:
        raise ValueError(f"Chỉ có {n_items} sản phẩm. Không đủ để huấn luyện.")

    print(f"Đang huấn luyện K-NN với K={k_fit} (trên tổng số {n_items} sản phẩm)...")
    model_knn = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=k_fit)
    model_knn.fit(item_utility_matrix)
    
    print("Huấn luyện thành công!")
    return model_knn, utility_df, item_utility_matrix

def generate_recommendations(model_knn, utility_df, item_utility_matrix):
    """Tạo danh sách gợi ý cho TẤT CẢ người dùng."""
    print("Bắt đầu tạo gợi ý cho tất cả người dùng...")
    
    # Tạo các "từ điển" tra cứu
    item_id_to_index = {pid: i for i, pid in enumerate(utility_df.columns)}
    item_index_to_id = {i: pid for i, pid in enumerate(utility_df.columns)}
    
    n_items_fit = model_knn.n_samples_fit_
    k_neighbors_fit = min(K_NEIGHBORS + 1, n_items_fit) # k + 1 (vì bao gồm cả chính nó)
    
    # 1. TÍNH TOÁN TRƯỚC BẢN ĐỒ TƯƠNG TỰ (ITEM-ITEM SIMILARITY MAP)
    # Bước này tìm hàng xóm cho MỌI item
    print(f"  Đang tính toán bản đồ tương tự (Item-Item map)...")
    all_distances, all_indices = model_knn.kneighbors(item_utility_matrix, n_neighbors=k_neighbors_fit)
    
    item_similarity_map = {}
    for i in range(n_items_fit):
        original_item_id = item_index_to_id[i]
        neighbors = []
        for j in range(1, len(all_indices[i])): # Bắt đầu từ 1 để bỏ qua chính nó
            neighbor_index = all_indices[i][j]
            neighbor_id = item_index_to_id[neighbor_index]
            distance = all_distances[i][j]
            similarity_score = 1 - distance # Cosine distance -> similarity
            neighbors.append((neighbor_id, similarity_score))
        item_similarity_map[original_item_id] = neighbors
    print(f"  Đã tính xong bản đồ tương tự cho {len(item_similarity_map)} sản phẩm.")

    # 2. TẠO GỢI Ý CHO TỪNG USER
    print(f"  Đang tạo gợi ý cá nhân hóa cho {len(utility_df.index)} người dùng...")
    all_recommendations = []
    generation_time = datetime.now()
    
    for user_id, user_vector in utility_df.iterrows():
        # Lấy các item user đã tương tác và điểm số
        items_user_has_interacted = user_vector[user_vector > 0]
        items_to_exclude = set(items_user_has_interacted.index)
        
        recommendation_scores = {}

        # Lặp qua lịch sử của user
        for interacted_item_id, interaction_score in items_user_has_interacted.items():
            # Lấy các sản phẩm tương tự (từ bản đồ đã tính toán)
            if interacted_item_id in item_similarity_map:
                similar_items = item_similarity_map[interacted_item_id]
                
                for neighbor_id, similarity_score in similar_items:
                    # Nếu user CHƯA tương tác với item này
                    if neighbor_id not in items_to_exclude:
                        # Tính điểm gợi ý cuối cùng
                        final_score = similarity_score * interaction_score
                        
                        # Cộng dồn điểm nếu item được gợi ý từ nhiều nguồn
                        recommendation_scores[neighbor_id] = recommendation_scores.get(neighbor_id, 0) + final_score
        
        # Sắp xếp và lấy Top N
        sorted_recommendations = sorted(recommendation_scores.items(), key=lambda item: item[1], reverse=True)
        top_n = sorted_recommendations[:N_RECOMMENDATIONS]
        
        # Thêm vào danh sách cuối cùng để ghi vào CSDL
        for product_id, score in top_n:
            # (user_id, product_id, score, generated_at)
            all_recommendations.append((user_id, product_id, score, generation_time))

    print(f"  Đã tạo xong {len(all_recommendations)} bản ghi gợi ý.")
    return all_recommendations

def save_recommendations_to_db(recommendations_data, conn):
    """Xóa các gợi ý cũ và lưu các gợi ý mới vào CSDL."""
    if not recommendations_data:
        print("Không có gợi ý nào được tạo ra. Bỏ qua việc ghi CSDL.")
        return

    print("Đang ghi gợi ý vào CSDL...")
    cursor = conn.cursor()
    
    try:
        # Bước 1: Xóa tất cả các gợi ý cũ
        print("  Đang xóa gợi ý cũ (TRUNCATE TABLE)...")
        cursor.execute("TRUNCATE TABLE [recommendations]")
        
        # Bước 2: Ghi hàng loạt các gợi ý mới
        print(f"  Đang ghi {len(recommendations_data)} gợi ý mới (INSERT)...")
        sql_insert = """
            INSERT INTO [recommendations] (user_id, product_id, score, generated_at) 
            VALUES (?, ?, ?, ?)
        """
        
        # Kích hoạt chế độ executemany nhanh (nếu driver hỗ trợ)
        cursor.fast_executemany = True 
        cursor.executemany(sql_insert, recommendations_data)
        
        # Xác nhận thay đổi
        conn.commit()
        print("  Ghi vào CSDL thành công!")

    except Exception as e:
        print(f"LỖI: Không thể ghi vào CSDL. Đang rollback...")
        conn.rollback()
        print(f"Chi tiết: {e}")
    finally:
        cursor.close()

# -------------------------------------------------------------------
# BƯỚC 2: HÀM MAIN ĐỂ CHẠY
# -------------------------------------------------------------------
def main():
    """Hàm chính điều phối toàn bộ quá trình."""
    print(f"--- BẮT ĐẦU CHẠY TÁC VỤ GỢI Ý (Production) ---")
    print(f"Thời gian: {datetime.now()}")
    print("-" * 40)
    
    conn = None
    try:
        # 1. Kết nối CSDL
        conn = get_db_connection()
        
        # 2. Tải 100% dữ liệu
        df = load_data(conn)
        
        # 3. Huấn luyện mô hình K-NN
        model, utility_df, item_matrix = train_model(df)
        
        # 4. Tạo gợi ý cho tất cả user
        recommendations = generate_recommendations(model, utility_df, item_matrix)
        
        # 5. Lưu kết quả vào CSDL
        save_recommendations_to_db(recommendations, conn)
        
        print("-" * 40)
        print("--- TÁC VỤ HOÀN THÀNH ---")

    except ValueError as ve:
        # Lỗi do không đủ dữ liệu
        print(f"\n--- TÁC VỤ DỪNG LẠI ---")
        print(f"Lỗi dữ liệu: {ve}")
    except Exception as e:
        # Các lỗi khác
        print(f"\n--- TÁC VỤ THẤT BẠI ---")
        print(f"Lỗi không xác định: {e}")
    finally:
        if conn:
            conn.close()
            print("Đã đóng kết nối CSDL.")

if __name__ == "__main__":
    main()