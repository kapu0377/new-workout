package org.zerock.workoutproject.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnaImageDTO {
    private Long ino;
    private String imageName;
    private  Long qno;
}