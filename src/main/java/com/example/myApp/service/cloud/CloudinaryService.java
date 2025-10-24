package com.example.myApp.service.cloud;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        Map<?, ?> result = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());

        tempFile.delete();

        return (String) result.get("secure_url");
    }

    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String extractPublicId(String url) {
        try {
            String[] parts = url.split("/");
            String filename = parts[parts.length - 1];
            return filename.substring(0, filename.lastIndexOf('.'));
        } catch (Exception e) {
            throw new IllegalArgumentException("Không thể extract publicId từ URL: " + url);
        }
    }
}
