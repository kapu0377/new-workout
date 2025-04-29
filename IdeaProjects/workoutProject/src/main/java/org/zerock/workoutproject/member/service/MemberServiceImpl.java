package org.zerock.workoutproject.member.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.zerock.workoutproject.member.domain.Member;
import org.zerock.workoutproject.member.domain.MemberRole;
import org.zerock.workoutproject.member.dto.MemberDTO;
import org.zerock.workoutproject.member.repository.MemberRepository;
import org.zerock.workoutproject.security.dto.MemberSecurityDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO login(MemberDTO memberDTO) {
        Member member = memberRepository.login(memberDTO.getMid(), memberDTO.getMpw());
        return modelMapper.map(member, MemberDTO.class);

    }

    @Override
    public void join(MemberDTO memberDTO) throws MidExistException {
        String mid = memberDTO.getMid();
        Member member = modelMapper.map(memberDTO, Member.class);

        member.changePassword(passwordEncoder.encode(memberDTO.getMpw()));
        member.addRole(MemberRole.USER);
        memberRepository.save(member);
    }

    @Override
    public void remove(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getMid())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        memberRepository.delete(member);
        SecurityContextHolder.clearContext();
    }

    @Override
    public MemberDTO findById(String id) {
        Member member = memberRepository.findById(id).orElseThrow();
        return modelMapper.map(member, MemberDTO.class);
    }

    @Override
    public int updateInfo(MemberDTO memberDTO) {
        try {

            Member member = memberRepository.findById(memberDTO.getMid()).orElseThrow();
            if (memberDTO.getMpw() != null && !memberDTO.getMpw().isBlank()) {
                member.changePassword(passwordEncoder.encode(memberDTO.getMpw()));
            }

            if (memberDTO.getEmail() != null){
                member.changeEmail(memberDTO.getEmail());
            }
            if (memberDTO.getAge() > 0) {
                member.changeAge(memberDTO.getAge());
            }
            if (memberDTO.getHeight() > 0) {
                member.changeHeight(memberDTO.getHeight());
            }
            if (memberDTO.getWeight() > 0) {
                member.changeWeight(memberDTO.getWeight());
            }
            if (memberDTO.getPhone() != null) {
                member.changePhone(memberDTO.getPhone());
            }
            memberRepository.save(member);
            return 1;
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean midCheck(String mid) {
        return memberRepository.existsByMid(mid);
    }

    @Override
    public void updateProfileImage(String username, String originalName, String uuid) {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        member.changeProfileImage(originalName, uuid);
        memberRepository.save(member);
    }

}
