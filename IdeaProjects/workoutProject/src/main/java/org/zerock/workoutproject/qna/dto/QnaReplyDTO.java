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

public class QnaReplyDTO {
    private Long qno; // qna (참조하는 테이블의 Qno값)
    private Long rno;   // qnaReply의 rno값
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyDate; // 답변 날짜
    private boolean secret; // 비밀 답변 여부
    private boolean admin; // 관리자 여부
    private String writer; // 이름
    private String replyText; // 댓글 내용
}
