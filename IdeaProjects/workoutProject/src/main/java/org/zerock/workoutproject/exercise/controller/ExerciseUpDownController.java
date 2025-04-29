package org.zerock.workoutproject.exercise.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.exercise.dto.UploadFileDTO;
import org.zerock.workoutproject.exercise.dto.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
@RequestMapping("/exercise")
public class ExerciseUpDownController {
  // application.properites에 있는 문자열 데이터 취득
  @Value("${org.zerock.upload.path}")
  private String uploadPath; // C:\\upload가 저장됨

  //MediaType.MULTIPART_FORM_DATA_VALUE 파일을 받기위해 사용하는 타입
  @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
    log.info(uploadFileDTO);
    // dto에 파일이 있는지 확인
    if(uploadFileDTO.getFiles() != null){
      final List<UploadResultDTO> list = new ArrayList<>();
      // 받은 파일 이름을 출력하는 반복문
      uploadFileDTO.getFiles().forEach(multipartFile -> {
        // 원본 파일 이름 저장
        String originalName = multipartFile.getOriginalFilename();
        log.info(originalName);
        // uuid 생성
        String uuid = UUID.randomUUID().toString();
        // 파일 경로 설정 : C:\\upload\\uuid_원본파일이름.확장자
        Path savePath = Paths.get(uploadPath, uuid+"_"+originalName);
        // 현재 저장하는 파일이 이미지인지 확인하는 값
        boolean image = false;
        try{
          // 파일을 savePath에 있는 경로 대로 저장
          multipartFile.transferTo(savePath);
//           저장한 파일이 이미지 파일인 경우 썸네일을 생성
          if(Files.probeContentType(savePath).startsWith("image")){
            image = true;
            // 생성될 썸네일 파일의 설정 : C:\\upload\\s_uuid_원본파일이름.확장자
            File thumbFile = new File(uploadPath,"s_"+uuid+"_"+originalName);
            // 썸네일 저장
            Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
          }
        }catch(IOException e){
          e.printStackTrace();
        }
        list.add(UploadResultDTO.builder()
                .uuid(uuid)
                .fileName(originalName)
                .img(image)
            .build());
      });
      return list;
    }
    return null;
  }
  @GetMapping("/view/{fileName}")
  public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) throws IOException {
    // 파일명이 없거나 리소스가 존재하지 않으면 404 반환
    if (fileName == null || fileName.trim().isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
    if (!resource.exists()) {
      return ResponseEntity.notFound().build();
    }
    String resourceName = resource.getFilename();
    HttpHeaders headers = new HttpHeaders();
    try{
      headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
    }catch(Exception e){
      return ResponseEntity.internalServerError().build();
    }
    return ResponseEntity.ok().headers(headers).body(resource);
  }
  @DeleteMapping("/remove/{fileName}")
  public Map<String, Boolean> removeFile(@PathVariable String fileName){
    Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);
    String resourceName = resource.getFilename();
    Map<String, Boolean> resultMap = new HashMap<>();
    boolean removed = false;
    try{
      String contentType = Files.probeContentType(resource.getFile().toPath());
      removed = resource.getFile().delete();
      if(contentType.startsWith("image")){
        File thumbFile = new File(uploadPath+File.separator+"s_"+fileName);
        thumbFile.delete();
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    resultMap.put("result", removed);
    return resultMap;
  }
}
