package org.zerock.workoutproject.exercise.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "exercise")
public class ExerciseImage implements Comparable<ExerciseImage> {
  @Id
  private String uuid;
  private String fileName;
  // 파일의 순서를 결정하는 값 ord : 오름차순, 내림차순 설정에 사용
  private int ord;
  @ManyToOne
  private Exercise exercise;

  @Override
  public int compareTo(ExerciseImage other) {
    return this.ord - other.ord; // 오름차순 설정
    // return other.ord - this.ord; // 내림차순 설정
  }
  public void changeBoard(Exercise exercise){
    this.exercise = exercise;
  }
}
