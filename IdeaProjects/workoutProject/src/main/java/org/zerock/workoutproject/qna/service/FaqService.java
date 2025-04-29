package org.zerock.workoutproject.qna.service;


import org.zerock.workoutproject.qna.dto.FaqDTO;

import java.util.List;

public interface FaqService {
    List<FaqDTO> getAllVisibleFaqs();

    FaqDTO getFaqById(Long id);

    FaqDTO createFaq(FaqDTO faqDto);

    FaqDTO updateFaq(Long id, FaqDTO faqDto);

    void deleteFaq(Long id);

    FaqDTO toggleFaqVisibility(Long id);

    List<FaqDTO> updateFaqOrder(List<Long> faqIds);
}