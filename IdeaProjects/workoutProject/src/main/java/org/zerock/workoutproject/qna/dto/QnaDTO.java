package org.zerock.workoutproject.qna.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class QnaDTO {
    private Long qno; // PK
    private String title; // 제목
    private String questionText;    // 질문 내용
    private String writer;  // 작성자
    private boolean completed; // 답변 완료 여부
    private List<QnaReplyDTO> replies;  // 댓글 리스트
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;  // 등록일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modDate;  // 수정일
    private boolean hidden; // 비공개 여부


//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public  class QnaImageDTO {
//        private Long ino;
//        private String imageName;
//    }
    private List<QnaImageDTO> images;
}