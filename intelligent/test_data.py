import pandas as pd
from scipy.sparse import csr_matrix
from sklearn.neighbors import NearestNeighbors
import pickle
import pyodbc  # <- Thư viện mới

## 1. Tải và Xử lý Dữ liệu (TỪ DỮ LIỆU THẬT)

# THAY ĐỔI CÁC THÔNG SỐ NÀY CHO PHÙ HỢP
SERVER_NAME = 'DESKTOP-BQB1VDP'  # <<<!!! THAY ĐỔI TÊN SERVER CỦA BẠN VÀO ĐÂY !!!
DATABASE_NAME = 'YourEyes'
DRIVER = '{ODBC Driver 17 for SQL Server}'  # Hoặc '{SQL Server}' nếu driver 17 không hoạt động

# Tạo chuỗi kết nối (Connection String)
# (Trusted_Connection=yes nghĩa là dùng Đăng nhập Windows)
connection_string = f"""
    DRIVER={DRIVER};
    SERVER={SERVER_NAME};
    DATABASE={DATABASE_NAME};
    Trusted_Connection=yes;
"""

# Câu lệnh SQL để lấy dữ liệu
# Chúng ta lấy chính xác các cột đã dùng trong dữ liệu mẫu
sql_query = """
    SELECT 
        user_id, 
        product_id, 
        action_type, 
        action_time 
    FROM 
        User_Product_Activity
    WHERE 
        user_id IS NOT NULL 
        AND product_id IS NOT NULL
"""

print("Đang kết nối đến SQL Server...")
try:
    # Kết nối đến CSDL
    conn = pyodbc.connect(connection_string)
    
    # Thực thi câu lệnh và tải dữ liệu vào DataFrame
    df = pd.read_sql(sql_query, conn)
    
    # Đóng kết nối
    conn.close()
    
    # Đảm bảo cột 'action_time' là kiểu datetime (quan trọng cho việc sắp xếp)
    df['action_time'] = pd.to_datetime(df['action_time'])

    # Kiểm tra nếu DataFrame rỗng
    if df.empty:
        print("LỖI: Không có dữ liệu nào được tải từ CSDL. Vui lòng kiểm tra lại câu query hoặc CSDL.")
        exit()

    print(f"--- Đã tải thành công {len(df)} tương tác từ CSDL thật ---")
    print(df.head())  # In 5 dòng đầu tiên để kiểm tra
    print("-" * 30)

except Exception as e:
    print(f"LỖI: Không thể kết nối hoặc tải dữ liệu từ SQL Server.")
    print(f"Chi tiết lỗi: {e}")
    # Thoát script nếu không có dữ liệu
    exit()

## 2. Áp dụng Trọng số (Theo logic đã bàn)
# (Phần này giữ nguyên)
action_weights = {
    'VIEW_DETAIL': 1,
    'ADD_TO_CART': 3,
    'ORDER': 5
}
# Bỏ qua các hành động không có trong map (fillna(0))
df['score'] = df['action_type'].map(action_weights).fillna(0)

# Xóa các hàng không có điểm (ví dụ: action_type không mong muốn)
df = df[df['score'] > 0]
if df.empty:
    print("LỖI: Sau khi lọc trọng số, không còn dữ liệu nào. "
          "Hãy kiểm tra lại các giá trị 'action_type' trong CSDL.")
    exit()

print("Đã áp dụng trọng số cho các hành động.")
print(df.head())
print("-" * 30)


## 3. Tạo Tập Train và Test (Chiến lược: "Giữ lại 1 món cuối cùng")
# (Đây là phần logic được thêm vào từ script mẫu)
print("Đang tách dữ liệu Train/Test...")
# Sắp xếp dữ liệu theo thời gian để đảm bảo "món cuối cùng" là mới nhất
df = df.sort_values(by='action_time')

# Gán rank (thứ tự) cho các hành động của mỗi người dùng
df['rank_in_user'] = df.groupby('user_id')['action_time'].rank(method='first', ascending=True)

# Lấy tổng số hành động của mỗi người dùng
df['total_actions'] = df.groupby('user_id')['rank_in_user'].transform('max')

# - Tập Train: Gồm tất cả hành động KHÔNG phải là hành động cuối cùng
# - Tập Test: CHỈ gồm hành động cuối cùng của mỗi người dùng
train_data = df[df['rank_in_user'] < df['total_actions']]
test_data = df[df['rank_in_user'] == df['total_actions']]

# Kiểm tra xem có dữ liệu huấn luyện không
if train_data.empty:
    print("LỖI: Tập huấn luyện (Train) bị rỗng. "
          "Điều này có thể xảy ra nếu mỗi người dùng chỉ có 1 hành động.")
    exit()

print(f"Tổng số tương tác: {len(df)}")
print(f"Tương tác huấn luyện (Train): {len(train_data)}")
print(f"Tương tác kiểm thử (Test): {len(test_data)}")
print("-" * 30)


## 4. Xây dựng Ma trận Tương tác (Utility Matrix) từ Tập Train
print("Đang xây dựng Ma trận Tương tác...")
# Đây là bước "luyện" dữ liệu cho K-NN
# Chúng ta nhóm theo user và product, và TÍNH TỔNG điểm (sum)
utility_df = train_data.groupby(['user_id', 'product_id'])['score'].sum().unstack().fillna(0)

if utility_df.empty:
    print("LỖI: Ma trận Tương tác (Utility Matrix) bị rỗng. Không thể huấn luyện.")
    exit()

print("--- Ma trận Tương tác (Utility Matrix) ---")
print(utility_df.head()) # In 5 dòng đầu của ma trận
print("-" * 30)

# Chuyển đổi sang định dạng ma trận thưa (Sparse Matrix) để tiết kiệm bộ nhớ
utility_matrix_sparse = csr_matrix(utility_df.values)


## 5. Huấn luyện Mô hình K-NN (Item-based)
print("Bắt đầu huấn luyện mô hình K-NN (Item-based)...")

# Vì là Item-based, chúng ta cần tìm "hàng xóm" của SẢN PHẨM.
# Chúng ta phải CHUYỂN VỊ (Transpose) ma trận: (user x item) -> (item x user)
item_utility_matrix = utility_matrix_sparse.transpose()

# Xác định số lượng hàng xóm (k)
# k không thể lớn hơn tổng số sản phẩm
n_items = item_utility_matrix.shape[0]
k_neighbors = min(10, n_items - 1) # Đặt k=10, hoặc ít hơn nếu có quá ít sản phẩm

if k_neighbors <= 0:
    print(f"LỖI: Chỉ có {n_items} sản phẩm trong tập huấn luyện. "
          "Không đủ để tìm hàng xóm.")
    exit()

print(f"Sẽ tìm {k_neighbors} hàng xóm cho mỗi sản phẩm (trong tổng số {n_items} sản phẩm).")
# Khởi tạo mô hình K-NN
model_knn = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=k_neighbors)

# Huấn luyện mô hình
model_knn.fit(item_utility_matrix)

print("Đã huấn luyện xong!")
print("-" * 30)


## 6. Lưu Mô hình đã Huấn luyện
# Chúng ta cần lưu 2 thứ:
# 1. Mô hình K-NN đã huấn luyện
# 2. DataFrame 'utility_df' (để biết index "5" là product_id nào)
print("Đang lưu mô hình...")
# 1. Lưu mô hình K-NN
with open('knn_model.pkl', 'wb') as f:
    pickle.dump(model_knn, f)

# 2. Lưu các cột của ma trận (chính là Product IDs)
utility_df.to_pickle('utility_df_mappings.pkl')

print("Đã lưu mô hình (knn_model.pkl) và bản đồ map (utility_df_mappings.pkl).")
print("--- HOÀN THÀNH HUẤN LUYỆN ---")