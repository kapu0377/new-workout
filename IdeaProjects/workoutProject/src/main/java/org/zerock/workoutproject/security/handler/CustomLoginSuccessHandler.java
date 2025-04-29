package org.zerock.workoutproject.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

@Log4j2
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public CustomLoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);  // 전달받은 URL 사용
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (session != null) {
            String redirectUrl = (String) session.getAttribute("url");
            log.info("Redirect URL from session: {}", redirectUrl);
            
            if (redirectUrl != null) {
                if (redirectUrl.contains("/member/login") || 
                    redirectUrl.startsWith("http://") || 
                    redirectUrl.startsWith("https://")) {
                    log.info("Invalid redirect URL detected. Using default target URL instead");
                    session.removeAttribute("url");
                    super.onAuthenticationSuccess(request, response, authentication);
                    return;
                }
                
                // 안전한 URL인 경우에만 리디렉션 수행
                session.removeAttribute("url");
                log.info("Redirecting to: {}", redirectUrl);
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                return;
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}