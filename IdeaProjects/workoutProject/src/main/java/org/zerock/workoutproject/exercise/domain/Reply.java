package org.zerock.workoutproject.exercise.domain;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.workoutproject.common.BaseEntity;

@Entity
// 테이블 설정 및 인덱스 설정
@Table(name="Reply", indexes = {@Index(name="idx_reply_exercise_eno", columnList = "exercise_eno")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// StackOverflow에러를 방지하기 위해
@ToString(exclude="exercise")
public class Reply extends BaseEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long rno;
  @ManyToOne(fetch = FetchType.LAZY)
  private Exercise exercise;
  private String replyText;
  private String replyer;
  public void changeText(String text){
    this.replyText = text;
  }
}
