package org.zerock.workoutproject.qna.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class QnaReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qno")
    private Qna qna; // 질문 번호
    private String writer; // 이름
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime replyDate; // 답변 날짜
    private String replyText; // 댓글 내용
    private boolean admin; // 관리자 여부
    private boolean isAdmin; // 관리자 여부
}
