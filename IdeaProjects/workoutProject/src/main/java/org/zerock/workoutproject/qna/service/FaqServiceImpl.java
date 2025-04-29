package org.zerock.workoutproject.qna.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.workoutproject.qna.domain.Faq;
import org.zerock.workoutproject.qna.dto.FaqDTO;
import org.zerock.workoutproject.qna.repository.FaqRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@Transactional
public class FaqServiceImpl implements FaqService {
    private final FaqRepository faqRepository;

    public FaqServiceImpl(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public List<FaqDTO> getAllVisibleFaqs() {
        return faqRepository.findAllVisibleFaqs()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FaqDTO getFaqById(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faq not found with id: " + id));
        return convertToDTO(faq);
    }

    @Override
    @Transactional
    public FaqDTO createFaq(FaqDTO faqDTO) {
        Faq faq = convertToEntity(faqDTO);
        Faq savedFaq = faqRepository.save(faq);
        return convertToDTO(savedFaq);
    }

    @Override
    @Transactional
    public FaqDTO updateFaq(Long id, FaqDTO faqDTO) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faq not found with id: " + id));

        updateFaqFromDTO(faq, faqDTO);
        Faq updatedFaq = faqRepository.save(faq);
        return convertToDTO(updatedFaq);
    }

    @Override
    @Transactional
    public void deleteFaq(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new RuntimeException("Faq not found with id: " + id);
        }
        faqRepository.deleteById(id);
    }

    @Override
    @Transactional
    public FaqDTO toggleFaqVisibility(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faq not found with id: " + id));

        faq.setVisible(!faq.isVisible());
        Faq updatedFaq = faqRepository.save(faq);
        return convertToDTO(updatedFaq);
    }

    @Override
    @Transactional
    public List<FaqDTO> updateFaqOrder(List<Long> faqIds) {
        List<Faq> faqs = faqRepository.findAllById(faqIds);

        IntStream.range(0, faqIds.size()).forEach(i -> {
            Faq faq = faqs.stream()
                    .filter(f -> f.getId().equals(faqIds.get(i)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Faq not found with id: " + faqIds.get(i)));

            faq.setDisplayOrder(i + 1);
        });

        List<Faq> updatedFaqs = faqRepository.saveAll(faqs);
        return updatedFaqs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FaqDTO convertToDTO(Faq faq) {
        FaqDTO dto = new FaqDTO();
        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setVisible(faq.isVisible());
        dto.setDisplayOrder(faq.getDisplayOrder());
        return dto;
    }

    private Faq convertToEntity(FaqDTO dto) {
        Faq faq = new Faq();
        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setVisible(true);
        faq.setDisplayOrder(dto.getDisplayOrder());
        return faq;
    }

    private void updateFaqFromDTO(Faq faq, FaqDTO dto) {
        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        if (dto.getDisplayOrder() != null) {
            faq.setDisplayOrder(dto.getDisplayOrder());
        }
    }
}