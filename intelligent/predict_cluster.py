import pandas as pd
import pickle
import numpy as np

# -------------------------------------------------------------------
# BƯỚC 1: TẢI CÁC MÔ HÌNH ĐÃ HUẤN LUYỆN
# -------------------------------------------------------------------
print("Đang tải mô hình K-Means (kmeans_model.pkl)...")
try:
    with open('kmeans_model.pkl', 'rb') as f:
        kmeans_model = pickle.load(f)
except FileNotFoundError:
    print("LỖI: Không tìm thấy file 'kmeans_model.pkl'.")
    exit()

print("Đang tải Pipeline Tiền xử lý (preprocessor.pkl)...")
try:
    with open('preprocessor.pkl', 'rb') as f:
        preprocessor = pickle.load(f)
except FileNotFoundError:
    print("LỖI: Không tìm thấy file 'preprocessor.pkl'.")
    exit()

print("Tải mô hình thành công!")
print("-" * 40)

# -------------------------------------------------------------------
# BƯỚC 2: GIẢ LẬP DỮ LIỆU NGƯỜI DÙNG MỚI
# -------------------------------------------------------------------
# Hãy tưởng tượng một người dùng mới đăng ký
# Bạn lấy dữ liệu này từ form đăng ký của họ

new_user_data = {
    'gender': 'Nữ',
    'age': 25,
    'height': 162,
    'weight': 53,
    'style_preference': 'Công sở'
}
print(f"Dữ liệu người dùng mới: {new_user_data}")

# -------------------------------------------------------------------
# BƯỚC 3: CHUẨN BỊ DỮ LIỆU VÀ DỰ ĐOÁN
# -------------------------------------------------------------------

# Biến đổi dữ liệu mới này thành DataFrame
# (Pipeline của sklearn yêu cầu đầu vào là DataFrame)
new_user_df = pd.DataFrame([new_user_data])

try:
    # 1. Tiền xử lý (Mã hóa + Chuẩn hóa)
    # Dùng 'transform' (KHÔNG DÙNG 'fit_transform' nữa, vì đã 'fit' lúc train)
    new_user_processed = preprocessor.transform(new_user_df)
    
    print(f"Đã mã hóa dữ liệu người dùng mới thành vector.")

    # 2. Dự đoán (Predict)
    # Dùng mô hình K-Means để dự đoán cụm
    predicted_cluster = kmeans_model.predict(new_user_processed)
    
    print("-" * 40)
    print(f"--- KẾT QUẢ DỰ ĐOÁN ---")
    print(f"Người dùng mới này thuộc về: Cụm {predicted_cluster[0]}")
    print("-" * 40)
    
    # Dựa trên kết quả huấn luyện của bạn, Cụm 1 là (Nữ, Công sở / Nữ, Thể thao)
    # Rất có thể người này sẽ được gán vào Cụm 1.
    
except Exception as e:
    print(f"LỖI: Không thể dự đoán.")
    print(f"Chi tiết: {e}")
    print("Lỗi này có thể xảy ra nếu style_preference (ví dụ: 'Công sở') "
          "chưa từng xuất hiện trong CSDL lúc huấn luyện.")