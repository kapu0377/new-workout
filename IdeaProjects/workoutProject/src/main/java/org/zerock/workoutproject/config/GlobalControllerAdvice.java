package org.zerock.workoutproject.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;

@Log4j2
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addCsrfToken(Model model, HttpServletRequest request) {
        try {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            if (token != null) {
                model.addAttribute("_csrf", token);
                log.debug("CSRF 토큰이 모델에 추가되었습니다.");
            } else {
                log.debug("CSRF 토큰이 null입니다.");
            }
        } catch (Exception e) {
            log.error("CSRF 토큰 처리 중 오류 발생: " + e.getMessage());
        }
    }

}
