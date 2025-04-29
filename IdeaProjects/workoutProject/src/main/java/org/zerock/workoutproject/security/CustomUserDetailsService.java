package org.zerock.workoutproject.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.workoutproject.member.domain.Member;
import org.zerock.workoutproject.member.repository.MemberRepository;
import org.zerock.workoutproject.security.dto.MemberSecurityDTO;

import java.util.Optional;
import java.util.stream.Collectors;


@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        log.info("loadUserByUsername : " + username);
        Optional<Member> result = memberRepository.getWithRoles(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        Member member = result.get();
        MemberSecurityDTO memberSecurityDTO =
                new MemberSecurityDTO(member.getMid(),
                        member.getMpw(),
                        member.getEmail(), member.getAge(),
                        member.getHeight(), member.getWeight(),
                        member.getPhone(),
                        member.isDel(),
                        member.getRoleSet().stream()
                                .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name()))
                                .collect(Collectors.toList())
                );

        log.info("memberSecurityDTO");
        log.info(memberSecurityDTO);

        return memberSecurityDTO;

    }
}







