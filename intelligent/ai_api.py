import pandas as pd
import pickle
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn # Server để chạy FastAPI
import numpy as np

# --- 1. Tải Mô hình KHI KHỞI ĐỘNG API ---
# Tải các "bộ não" một lần duy nhất khi API khởi động
# Điều này đảm bảo tốc độ dự đoán cực nhanh (miligiây)
try:
    print("Đang tải mô hình K-Means (kmeans_model.pkl)...")
    with open('kmeans_model.pkl', 'rb') as f:
        kmeans_model = pickle.load(f)
    
    print("Đang tải Bộ tiền xử lý (preprocessor.pkl)...")
    with open('preprocessor.pkl', 'rb') as f:
        preprocessor = pickle.load(f)
    print("--- API Sẵn sàng ---")
except FileNotFoundError:
    print("LỖI: Không tìm thấy file .pkl. API không thể khởi động.")
    kmeans_model = None
    preprocessor = None

# --- 2. Định nghĩa Cấu trúc Dữ liệu (Validation) ---
# Pydantic sẽ tự động kiểm tra xem JSON từ Java gửi qua có đúng định dạng không
class UserProfile(BaseModel):
    gender: str
    age: int
    height: float
    weight: float
    style_preference: str

# --- 3. Khởi tạo FastAPI App ---
app = FastAPI(
    title="YourEyes AI API",
    description="API thời gian thực để phân cụm người dùng."
)

# --- 4. Định nghĩa Endpoint (Cổng API) ---
@app.post("/predict_cluster")
async def predict_cluster(profile: UserProfile):
    """
    Nhận hồ sơ người dùng mới và dự đoán họ thuộc cụm (cluster) nào.
    """
    if not kmeans_model or not preprocessor:
        raise HTTPException(status_code=503, detail="Lỗi: Mô hình chưa được tải. API đang không sẵn sàng.")

    try:
        # 1. Chuyển dữ liệu Pydantic (JSON) thành DataFrame
        # (Vì preprocessor của chúng ta được huấn luyện trên DataFrame)
        new_user_df = pd.DataFrame([profile.dict()])

        # 2. Tiền xử lý (Mã hóa + Chuẩn hóa)
        # Dùng .transform() - KHÔNG BAO GIỜ .fit() ở production
        new_user_processed = preprocessor.transform(new_user_df)

        # 3. Dự đoán (Predict)
        predicted_cluster = kmeans_model.predict(new_user_processed)
        
        # 4. Trả về kết quả (dưới dạng JSON)
        # Chuyển đổi np.int64 thành int chuẩn của Python
        cluster_id = int(predicted_cluster[0]) 
        
        print(f"Dự đoán thành công: Hồ sơ {profile.age} tuổi -> Cụm {cluster_id}")
        
        return {"cluster_id": cluster_id}

    except Exception as e:
        print(f"LỖI DỰ ĐOÁN: {e}")
        # Lỗi 500 - Internal Server Error
        raise HTTPException(status_code=500, detail=f"Lỗi máy chủ nội bộ khi dự đoán: {str(e)}")

# --- 5. Lệnh để Chạy API ---
if __name__ == "__main__":
    # Chạy API trên cổng 5001
    print("Khởi chạy máy chủ API tại http://localhost:5001")
    uvicorn.run(app, host="127.0.0.1", port=5001)