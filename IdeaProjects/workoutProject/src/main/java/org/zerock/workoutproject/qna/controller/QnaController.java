package org.zerock.workoutproject.qna.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.qna.domain.Qna;

import org.zerock.workoutproject.qna.service.QnaService;

@Slf4j
@Controller
@RequestMapping("/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    @GetMapping("/list")
    public String listPage() {
        return "qna/list";
    }

    @GetMapping("/view/{qno}")
    public String viewPage(@PathVariable Long qno, Model model) {
        Qna qna = qnaService.getQna(qno);
        model.addAttribute("qna", qna);
        model.addAttribute("completed", qna.isCompleted());
        return "qna/view";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "qna/register";
    }
}
