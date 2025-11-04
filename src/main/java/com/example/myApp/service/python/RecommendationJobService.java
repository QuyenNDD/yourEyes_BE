package com.example.myApp.service.python;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
public class RecommendationJobService {
    private final Logger logger = Logger.getLogger(RecommendationJobService.class.getName());

    // Chạy vào 2:00 AM mỗi ngày
    @Scheduled(fixedRate = 60000)
    public void runDailyHybridModel() {
        logger.info("--- [BẮT ĐẦU] Tác vụ Hybrid Job hàng đêm ---");
        try {
            // Đường dẫn TUYỆT ĐỐI đến Python và script của bạn
            String pythonExecutable = "C:/Users/ASUS/AppData/Local/Programs/Python/Python312/python.exe";
            String scriptPath = "E:/project/youreyes/BE/run_production_hybrid.py";

            ProcessBuilder pb = new ProcessBuilder(pythonExecutable, scriptPath);
            pb.environment().put("PYTHONIOENCODING", "UTF-8");
            pb.redirectErrorStream(true); // Gộp output và error stream

            Process process = pb.start();

            // Đọc log output từ script Python (Rất quan trọng để debug)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("[Python Log] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("--- [THÀNH CÔNG] Tác vụ Hybrid Job đã chạy xong ---");
            } else {
                logger.severe("--- [THẤT BẠI] Tác vụ Hybrid Job. Exit code: " + exitCode);
            }

        } catch (Exception e) {
            logger.severe("Lỗi nghiêm trọng khi thực thi ProcessBuilder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
