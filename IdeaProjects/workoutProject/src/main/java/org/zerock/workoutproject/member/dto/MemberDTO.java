package org.zerock.workoutproject.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    @NotBlank(message = "아이디는 필수 입력값 입니다.")
    private String mid;
    @NotBlank(message = "비밀번호는 필수 입력값 입니다.")
    private String mpw;
    @NotBlank(message = "이메일은 필수 입력값 입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다. (예: example@email.com)")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", 
            message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    private int age;
    private double height;
    private double weight;
    @NotBlank(message = "전화번호는 필수 입력값 입니다.")
    private String phone;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String profileImageName;   
    private String profileImageUuid;    
}
