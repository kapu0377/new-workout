package org.zerock.workoutproject.exercise.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExerciseListReplyCountDTO {
  private int tno;
  private String title;
  private String writer;
  private LocalDateTime regDate;
  private Long replyCount;
}
