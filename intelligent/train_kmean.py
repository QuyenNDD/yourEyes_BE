import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
import pyodbc
import pickle
import matplotlib.pyplot as plt
from datetime import datetime

# -------------------------------------------------------------------
# BƯỚC 1: KHAI BÁO CẤU HÌNH
# -------------------------------------------------------------------
# <<<!!! HÃY ĐIỀN THÔNG SỐ CỦA BẠN VÀO ĐÂY !!!>>>
SERVER_NAME = 'DESKTOP-BQB1VDP' 
DATABASE_NAME = 'YourEyes'
DRIVER = '{ODBC Driver 17 for SQL Server}'
# <<<!!! HẾT PHẦN CẤU HÌNH !!!>>>

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

def load_user_profiles(conn):
    """Tải dữ liệu hồ sơ người dùng (user_profile)."""
    print("Đang tải dữ liệu từ [user_profile]...")
    # Lấy các cột chúng ta cần cho việc phân khúc
    sql_query = """
        SELECT 
            user_id, 
            gender, 
            age, 
            height, 
            weight, 
            style_preference 
        FROM 
            user_profile
    """
    df = pd.read_sql(sql_query, conn)
    
    if df.empty:
        raise ValueError("Không có dữ liệu nào trong [user_profile] để huấn luyện.")

    print(f"Đã tải thành công hồ sơ của {len(df)} người dùng.")
    return df

def create_preprocessing_pipeline(df):
    """Tạo pipeline để mã hóa và chuẩn hóa dữ liệu."""
    print("Đang tạo pipeline tiền xử lý...")
    
    # Xác định các cột số và cột chữ
    numeric_features = ['age', 'height', 'weight']
    categorical_features = ['gender', 'style_preference']

    # Pipeline cho DỮ LIỆU SỐ:
    # 1. Imputer: Điền các giá trị NULL (nếu có) bằng giá trị TRUNG BÌNH.
    # 2. Scaler: Chuẩn hóa (scale) tất cả về thang 0-1.
    numeric_transformer = Pipeline(steps=[
        ('imputer', SimpleImputer(strategy='mean')),
        ('scaler', StandardScaler())
    ])

    # Pipeline cho DỮ LIỆU CHỮ (Categorical):
    # 1. Imputer: Điền các giá trị NULL (nếu có) bằng một giá trị cố định (ví dụ: 'missing').
    # 2. Encoder: Chuyển đổi thành các cột 0/1 (One-Hot Encoding).
    categorical_transformer = Pipeline(steps=[
        ('imputer', SimpleImputer(strategy='constant', fill_value='missing')),
        ('onehot', OneHotEncoder(handle_unknown='ignore'))
    ])

    # Kết hợp hai pipeline trên
    preprocessor = ColumnTransformer(
        transformers=[
            ('num', numeric_transformer, numeric_features),
            ('cat', categorical_transformer, categorical_features)
        ])
    
    return preprocessor

def find_optimal_k(data_processed):
    """Sử dụng Phương pháp Khuỷu tay (Elbow Method) để tìm K tối ưu."""
    print("Đang tìm K tối ưu (Elbow Method)...")
    
    inertias = [] # Lưu trữ độ 'méo' của cụm
    max_k = min(10, len(data_processed) - 1) # Tìm tối đa 10 cụm, hoặc ít hơn
    
    if max_k <= 1:
        print("Dữ liệu quá ít, chỉ định K=1.")
        return 1

    K_range = range(1, max_k + 1)
    
    for k in K_range:
        kmeans = KMeans(n_clusters=k, init='k-means++', n_init=10, random_state=42)
        kmeans.fit(data_processed)
        inertias.append(kmeans.inertia_)

    # Vẽ biểu đồ (Tùy chọn, nhưng rất hữu ích)
    plt.figure(figsize=(8, 4))
    plt.plot(K_range, inertias, 'bo-')
    plt.xlabel('Số lượng cụm (K)')
    plt.ylabel('Inertia (Độ méo)')
    plt.title('Phương pháp Khuỷu tay (Elbow Method)')
    plt.savefig('kmeans_elbow_plot.png')
    print("Đã lưu biểu đồ 'kmeans_elbow_plot.png'. Hãy xem nó để xác nhận K.")

    # Tự động tìm "khuỷu tay" (cách đơn giản)
    # Chúng ta sẽ tìm K mà tại đó Inertia giảm đi không còn nhiều
    # (Đây là một cách heuristic đơn giản)
    # Trong ví dụ này, chúng ta sẽ chọn K=4 (hoặc bạn có thể tự nhìn biểu đồ)
    # Tạm thời, chúng ta sẽ chọn K=4 làm ví dụ
    # Bạn nên nhìn vào biểu đồ và chọn K tốt hơn
    
    optimal_k = 4 # <<<!!! BẠN CÓ THỂ THAY ĐỔI K NÀY SAU KHI XEM BIỂU ĐỒ !!!>>>
    if max_k < optimal_k:
        optimal_k = max_k
        
    print(f"Đã tự động chọn K = {optimal_k}. (Bạn có thể tinh chỉnh K này).")
    return optimal_k

def main():
    """Hàm chính điều phối toàn bộ quá trình."""
    print(f"--- BẮT ĐẦU HUẤN LUYỆN K-MEANS (Phân khúc) ---")
    print(f"Thời gian: {datetime.now()}")
    print("-" * 40)
    
    conn = None
    try:
        # 1. Kết nối CSDL và Tải dữ liệu
        conn = get_db_connection()
        user_profiles_df = load_user_profiles(conn)
        
        # Lưu lại user_ids để tham chiếu sau này
        user_ids = user_profiles_df['user_id']
        
        # 2. Tiền xử lý dữ liệu
        preprocessor_pipeline = create_preprocessing_pipeline(user_profiles_df)
        
        # 'fit_transform' dữ liệu
        # Dữ liệu X là các hồ sơ đã được số hóa
        X_processed = preprocessor_pipeline.fit_transform(user_profiles_df)
        
        print(f"Đã tiền xử lý xong. Dữ liệu có {X_processed.shape[0]} mẫu và {X_processed.shape[1]} đặc trưng (features).")
        
        # 3. Tìm K tối ưu
        optimal_k = find_optimal_k(X_processed)
        
        # 4. Huấn luyện mô hình K-Means cuối cùng
        print(f"Đang huấn luyện K-Means với K = {optimal_k}...")
        kmeans_model = KMeans(n_clusters=optimal_k, init='k-means++', n_init=10, random_state=42)
        kmeans_model.fit(X_processed)
        
        # Gán nhãn cụm cho dữ liệu gốc
        cluster_labels = kmeans_model.labels_
        user_profiles_df['cluster'] = cluster_labels
        
        print("Huấn luyện K-Means hoàn tất!")
        print("--- Kết quả phân cụm (5 mẫu đầu) ---")
        print(user_profiles_df.head())
        print("-" * 40)
        
        # 5. Lưu mô hình và pipeline
        print("Đang lưu mô hình K-Means (kmeans_model.pkl)...")
        with open('kmeans_model.pkl', 'wb') as f:
            pickle.dump(kmeans_model, f)
            
        print("Đang lưu Pipeline tiền xử lý (preprocessor.pkl)...")
        with open('preprocessor.pkl', 'wb') as f:
            pickle.dump(preprocessor_pipeline, f)
            
        print("-" * 40)
        print("--- TÁC VỤ HOÀN THÀNH ---")
        print("Bạn đã có 'kmeans_model.pkl' và 'preprocessor.pkl'.")
        print("Hãy kiểm tra 'kmeans_elbow_plot.png' để chọn K tốt hơn nếu cần.")

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