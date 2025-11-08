import pandas as pd
import pyodbc
import pickle
import re # Thư viện để làm sạch văn bản (Regular Expressions)
from datetime import datetime
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.neighbors import NearestNeighbors

# -------------------------------------------------------------------
# BƯỚC 1: KHAI BÁO CẤU HÌNH
# -------------------------------------------------------------------
SERVER_NAME = 'DESKTOP-BQB1VDP' # <<<!!! THAY ĐỔI TÊN SERVER CỦA BẠN !!!>>>
DATABASE_NAME = 'YourEyes'
DRIVER = '{ODBC Driver 17 for SQL Server}'

# -------------------------------------------------------------------
# BƯỚC 2: CÁC HÀM TẢI VÀ XỬ LÝ DỮ LIỆU
# -------------------------------------------------------------------

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

def load_product_data(conn):
    """Tải 100% dữ liệu sản phẩm (name, description)."""
    print("Đang tải dữ liệu từ [products]...")
    sql_query = "SELECT id, name, description FROM products"
    df = pd.read_sql(sql_query, conn)
    
    if df.empty:
        raise ValueError("Không có sản phẩm nào trong CSDL để huấn luyện.")
        
    print(f"Đã tải thành công {len(df)} sản phẩm.")
    return df

def clean_text(text):
    """Một hàm làm sạch văn bản đơn giản."""
    if text is None or pd.isna(text):
        return ""
    text = str(text).lower() # Chuyển về chữ thường
    text = re.sub(r'[^a-z0-9à-ỹ\s]', '', text) # Loại bỏ ký tự đặc biệt, giữ lại Tiếng Việt
    text = re.sub(r'\s+', ' ', text).strip() # Xóa khoảng trắng thừa
    return text

def create_corpus(df):
    """Kết hợp name và description, làm sạch chúng."""
    print("Đang tạo kho văn bản (corpus)...")
    
    # Tạo một "từ điển" map từ index (0, 1, 2...) sang product_id (1022, 1023...)
    # Điều này CỰC KỲ QUAN TRỌNG để tra cứu sau này
    product_index_to_id = pd.Series(df.id.values).to_dict()

    # Xử lý NULL và kết hợp name + description
    df['name'] = df['name'].fillna('')
    df['description'] = df['description'].fillna('')
    
    # Kết hợp: "Áo Sơ mi" + "Áo sơ mi nam chất lụa" -> "áo sơ mi áo sơ mi nam chất lụa"
    df['corpus_text'] = df['name'].apply(clean_text) + " " + df['description'].apply(clean_text)
    
    # Xử lý trường hợp 2 cột đều rỗng
    df['corpus_text'] = df['corpus_text'].replace(r'^\s*$', np.nan, regex=True)
    df = df.dropna(subset=['corpus_text']) # Xóa sản phẩm không có text
    
    print(f"Đã tạo xong corpus cho {len(df)} sản phẩm có văn bản.")
    return df['corpus_text'].tolist(), product_index_to_id

# -------------------------------------------------------------------
# BƯỚC 3: HÀM MAIN ĐỂ CHẠY
# -------------------------------------------------------------------
def main():
    """Hàm chính điều phối toàn bộ quá trình."""
    print(f"--- BẮT ĐẦU HUẤN LUYỆN NLP (Sản phẩm Tương tự) ---")
    print(f"Thời gian: {datetime.now()}")
    print("-" * 40)
    
    conn = None
    try:
        # 1. Kết nối CSDL và Tải dữ liệu
        conn = get_db_connection()
        products_df = load_product_data(conn)
        
        # 2. Tạo kho văn bản (Corpus) và Bản đồ (Map)
        corpus, product_index_to_id = create_corpus(products_df)
        
        if not corpus:
            raise ValueError("Kho văn bản (corpus) rỗng. Không thể huấn luyện.")

        # 3. Huấn luyện Bộ Vector hóa TF-IDF
        print("Đang huấn luyện TF-IDF Vectorizer...")
        # max_features=5000: Chỉ lấy 5000 từ quan trọng nhất
        # min_df=2: Bỏ qua các từ chỉ xuất hiện 1 lần (quá hiếm)
        vectorizer = TfidfVectorizer(max_features=5000, min_df=2)
        tfidf_matrix = vectorizer.fit_transform(corpus)
        
        print(f"Đã vector hóa văn bản thành ma trận: {tfidf_matrix.shape}")

        # 4. Huấn luyện Mô hình K-NN (Cosine Similarity)
        print("Đang huấn luyện K-NN (Cosine) cho NLP...")
        # Tìm 10 "hàng xóm" (sản phẩm tương tự) gần nhất
        k_nlp = min(10, tfidf_matrix.shape[0] - 1)
        if k_nlp <= 0:
            raise ValueError("Dữ liệu quá ít (<= 1 sản phẩm). Không thể huấn luyện K-NN.")
            
        model_nlp_knn = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=k_nlp)
        model_nlp_knn.fit(tfidf_matrix)
        
        print("Huấn luyện NLP K-NN hoàn tất!")
        
        # 5. Lưu 3 "Bộ não" NLP
        print("Đang lưu các mô hình NLP...")
        
        # Bộ não 1: Dùng để biến đổi văn bản (kể cả search query)
        with open('nlp_vectorizer.pkl', 'wb') as f:
            pickle.dump(vectorizer, f)
            
        # Bộ não 2: Dùng để tìm hàng xóm (sản phẩm tương tự)
        with open('nlp_knn_model.pkl', 'wb') as f:
            pickle.dump(model_nlp_knn, f)
            
        # Bộ não 3: Dùng để tra cứu (index -> product_id)
        with open('nlp_product_map.pkl', 'wb') as f:
            pickle.dump(product_index_to_id, f)

        print("-" * 40)
        print("--- TÁC VỤ HOÀN THÀNH ---")
        print("Bạn đã có 3 file NLP: 'nlp_vectorizer.pkl', 'nlp_knn_model.pkl', 'nlp_product_map.pkl'.")

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
    # Import numpy ở đây, chỉ cần cho hàm create_corpus
    import numpy as np 
    main()