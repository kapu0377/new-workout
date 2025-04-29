package org.zerock.workoutproject.qna.dto;

import lombok.Data;

@Data
public class FaqDTO {
    private Long id;
    private String question;
    private String answer;
    private boolean visible;
    private Integer displayOrder;
}
