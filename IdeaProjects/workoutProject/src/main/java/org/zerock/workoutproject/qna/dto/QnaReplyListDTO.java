package org.zerock.workoutproject.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaReplyListDTO {
    private List<QnaReplyDTO> replies;    // 댓글 목록
    private boolean hasNext;              // 다음 페이지 존재 여부
    private Long lastRno;                 // 마지막 댓글 번호
    private int totalCount;               // 전체 댓글 수
}
