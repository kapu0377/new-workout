package org.zerock.workoutproject.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseImageDTO {
  private String uuid;
  private String fileName;
  private int ord;
}













