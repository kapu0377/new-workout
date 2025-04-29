package org.zerock.workoutproject.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.workoutproject.security.dto.MemberSecurityDTO;


import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler  implements AuthenticationSuccessHandler {
  private final PasswordEncoder passwordEncoder;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    log.info("-------------------------------------");
    log.info("CustomLoginSuccessHandler onAuthenticationSuccess...............");
    log.info(authentication.getPrincipal());
    // MemberSecurityDTO의 모든 데이터를 사용하기 위해서 Principal을 MemberSecurityDTO로 변경
    MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();
    // 암호화된 패스워드를 변수에 저장
    String encodedPw = memberSecurityDTO.getMpw();
    // 소셜 계정이면 패스워드가 1111이거나 암호화된 패스워드가 1111인 경우
//    if(memberSecurityDTO.isSocial() &&
//        memberSecurityDTO.getMpw().equals("1111")
//        || passwordEncoder.matches("1111",memberSecurityDTO.getMpw())) {
//      log.info("Should Change Password");
//      log.info("Redirect to Member Modify");
//      // 비밀번호가 1111이면 회원수정 페이지로 리다이렉트
//      response.sendRedirect("/member/modify");
//      return;
//    }else{
//      // 비밀번호가 1111이 아니면 게시글 목록으로 리다이렉트
//      response.sendRedirect("/board/list");
//    }
    response.sendRedirect("/");
  }
}
