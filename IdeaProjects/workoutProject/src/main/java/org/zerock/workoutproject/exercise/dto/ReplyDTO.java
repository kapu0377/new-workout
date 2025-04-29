package org.zerock.workoutproject.exercise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {
  // 댓글의 PK
  private Long rno;
  // board의 PK를 FK로
  @NotNull
  private int eno;
  // 댓글 내용
  @NotEmpty
  private String replyText;
  // 댓글 작성자
  @NotEmpty
  private String replyer;
  // 등록일
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime regDate;
  //수정일
  @JsonIgnore
  private LocalDateTime modDate;
}









