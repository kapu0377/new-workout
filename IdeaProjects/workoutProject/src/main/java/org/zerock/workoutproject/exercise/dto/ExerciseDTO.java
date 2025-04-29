package org.zerock.workoutproject.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
  private int eno;
  private String title;
  private String content;
  private String url;
  private int view;
  private List<String> fileNames;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
