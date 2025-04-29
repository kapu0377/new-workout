package org.zerock.workoutproject.security.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.zerock.workoutproject.member.dto.MemberDTO;
import org.zerock.workoutproject.member.repository.MemberRepository;

@Component

@RequiredArgsConstructor
public class CheckIdValidaotr extends AbstracValidator<MemberDTO> {
    private final MemberRepository memberRepository;

    @Override
    protected void doValidate(MemberDTO dto, Errors errors) {
        if(memberRepository.existsByMid(dto.getMid())) {
            errors.rejectValue("mid", "아이디 중복 오류", "이미 사용중인 아이디 입니다.");
        }
    }
}
