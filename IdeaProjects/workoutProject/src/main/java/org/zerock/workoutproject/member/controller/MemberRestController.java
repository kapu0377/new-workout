package org.zerock.workoutproject.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.workoutproject.member.service.MemberService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Log4j2
@CrossOrigin(origins = "*") 
public class MemberRestController {
    private final MemberService memberService;

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @GetMapping({"/check/{mid}", "/member/check/{mid}"})
    public boolean memberCheck(@PathVariable("mid") String mid) {
        log.info("ID 중복 체크 요청: {}", mid);
        boolean result = memberService.midCheck(mid);
        log.info("ID {} 중복 체크 결과: {}", mid, result);
        return result;
    }

    @PostMapping(value = "/member/profile/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestParam("profileImage") MultipartFile profileImage,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (profileImage.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지 파일을 선택해주세요."));
        }

        try {
            String originalName = profileImage.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            Path savePath = Paths.get(uploadPath, "profiles", uuid + "_" + originalName);
            
            // profiles 디렉토리가 없으면 생성
            Files.createDirectories(savePath.getParent());
            
            // 원본 이미지 저장
            profileImage.transferTo(savePath);

            // 썸네일 생성 (200x200)
            File thumbFile = new File(uploadPath + File.separator + "profiles" + File.separator + "s_" + uuid + "_" + originalName);
            Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);

            // DB에 이미지 정보 업데이트
            memberService.updateProfileImage(userDetails.getUsername(), originalName, uuid);

            Map<String, String> result = new HashMap<>();
            result.put("originalName", originalName);
            result.put("uuid", uuid);
            
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            log.error("프로필 이미지 업로드 실패: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "이미지 업로드에 실패했습니다."));
        }
    }

    @GetMapping("/member/profile/{fileName}")
    public ResponseEntity<org.springframework.core.io.Resource> getProfileImage(@PathVariable String fileName) {
        try {
            Path imagePath = Paths.get(uploadPath, "profiles", fileName);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(imagePath.toFile());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(imagePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            log.error("프로필 이미지 조회 실패: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
