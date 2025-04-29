package org.zerock.workoutproject.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.METHOD_NOT_ALLOWED, 
                "지원하지 않는 HTTP 메서드입니다: " + ex.getMessage()
        );
        problemDetail.setTitle("메서드 지원 오류");
        problemDetail.setType(URI.create("https://capu.blog/errors/method-not-supported"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("supportedMethods", ex.getSupportedMethods());
        
        return new ResponseEntity<>(problemDetail, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요."
        );
        problemDetail.setTitle("서버 오류");
        problemDetail.setType(URI.create("https://capu.blog/errors/server-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorMessage", ex.getMessage());
        problemDetail.setProperty("errorClass", ex.getClass().getName());
        
        if (ex.getCause() != null) {
            problemDetail.setProperty("rootCause", ex.getCause().getMessage());
        }
        
        // 디버깅을 위한 로그 기록
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
} 