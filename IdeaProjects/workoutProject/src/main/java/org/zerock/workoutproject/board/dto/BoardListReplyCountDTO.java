package org.zerock.workoutproject.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDTO {
    private Long bno;
    private String title;
    private String writer;
    private String url;
    private Long view;
    private LocalDateTime regDate;
    private Long replyCount;
}
