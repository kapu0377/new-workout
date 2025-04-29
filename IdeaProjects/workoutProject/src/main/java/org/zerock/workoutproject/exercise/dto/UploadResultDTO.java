package org.zerock.workoutproject.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
  private String uuid;
  private String fileName;
  private boolean img;
  // 썸네일 출력시 사용하는 메서드
  public String getLink(){
    if(img){
      return "s_"+uuid+"_"+fileName;
    }else{
      return uuid+"_"+fileName;
    }
  }
}
