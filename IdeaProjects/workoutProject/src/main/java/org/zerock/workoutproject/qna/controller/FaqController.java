package org.zerock.workoutproject.qna.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.qna.dto.FaqDTO;
import org.zerock.workoutproject.qna.service.FaqService;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
public class FaqController {
    private final FaqService faqService;

    @Autowired
    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public ResponseEntity<List<FaqDTO>> getAllFaqs() {
        return ResponseEntity.ok(faqService.getAllVisibleFaqs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqDTO> getFaqById(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.getFaqById(id));
    }

    @PostMapping
    public ResponseEntity<FaqDTO> createFaq(@RequestBody FaqDTO faqDto) {
        return ResponseEntity.ok(faqService.createFaq(faqDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqDTO> updateFaq(@PathVariable Long id, @RequestBody FaqDTO faqDto) {
        return ResponseEntity.ok(faqService.updateFaq(id, faqDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<FaqDTO> toggleFaqVisibility(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.toggleFaqVisibility(id));
    }

    @PutMapping("/order")
    public ResponseEntity<List<FaqDTO>> updateFaqOrder(@RequestBody List<Long> faqIds) {
        return ResponseEntity.ok(faqService.updateFaqOrder(faqIds));
    }
}
