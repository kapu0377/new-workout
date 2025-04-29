package org.zerock.workoutproject.qna.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaListDTO {
    private Long qno; // PK
    private Long view; // 조회수
    private String title; // 제목
    private String questionText;    // 질문 내용
    private String writer;  // 작성자
    private boolean completed; // 답변 완료 여부
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;  // 등록일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modDate;  // 수정일
    private boolean hidden; // 비공개 여부

}