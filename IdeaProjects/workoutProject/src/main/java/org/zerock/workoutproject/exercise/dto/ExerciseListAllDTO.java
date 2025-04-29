package org.zerock.workoutproject.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseListAllDTO {

  private int eno;
  private String title;
  private String content;
  private String url;
  private int view;
  private Long replyCount;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
  private List<ExerciseImageDTO> noticeImages;
}












