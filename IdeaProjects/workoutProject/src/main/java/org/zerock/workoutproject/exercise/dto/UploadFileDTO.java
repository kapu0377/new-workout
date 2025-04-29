package org.zerock.workoutproject.exercise.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDTO {
  // form태그로 전달받는 파일 형식은 MultipartFile 타입이며
  // input태그의 type인 file의 경우 여러개의 파일을 한번에 설정할 수 있기 때문에 list 형식으로 받는다
  private List<MultipartFile> files;
}
