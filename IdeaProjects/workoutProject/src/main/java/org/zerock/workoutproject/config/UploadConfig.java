package org.zerock.workoutproject.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class UploadConfig {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;
    
    @PostConstruct
    public void init() {
        try {
            log.info("Initializing upload directory: {}", uploadPath);
            File uploadDir = new File(uploadPath);
            
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (created) {
                    log.info("Created upload directory: {}", uploadPath);
                } else {
                    log.error("Failed to create upload directory: {}", uploadPath);
                }
            }
            
            // 디렉토리 권한 확인
            if (!uploadDir.canWrite()) {
                log.error("Upload directory is not writable: {}", uploadPath);
            } else {
                log.info("Upload directory is writable: {}", uploadPath);
            }
            
            // 임시 파일 생성 및 삭제로 권한 테스트
            try {
                File testFile = new File(uploadPath, "test_write_permission.tmp");
                Files.write(testFile.toPath(), "test".getBytes());
                log.info("Successfully wrote test file to upload directory");
                Files.delete(Paths.get(uploadPath, "test_write_permission.tmp"));
                log.info("Successfully deleted test file from upload directory");
            } catch (Exception e) {
                log.error("Error testing write permissions: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error initializing upload directory: {}", e.getMessage(), e);
        }
    }
} 