package org.zerock.workoutproject.qna.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.workoutproject.board.domain.Board;
import org.zerock.workoutproject.board.dto.ViewCountDTO;
import org.zerock.workoutproject.qna.domain.Qna;
import org.zerock.workoutproject.qna.domain.QnaImage;
import org.zerock.workoutproject.qna.domain.QnaReply;
import org.zerock.workoutproject.qna.dto.*;
import org.zerock.workoutproject.qna.repository.QnaImageRepository;
import org.zerock.workoutproject.qna.repository.QnaReplyRepository;
import org.zerock.workoutproject.qna.repository.QnaRepository;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class QnaServiceImpl implements QnaService {
    @Value("${org.zerock.upload.path}")
    private String uploadPath;
    private final QnaImageRepository qnaImageRepository;

    @PostConstruct
    public void init() {
        try {
            File directory = new File(uploadPath);
            log.info("업로드 디렉터리 확인: {}", uploadPath);
            
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    log.info("업로드 디렉터리 생성 성공: {}", uploadPath);
                } else {
                    log.error("업로드 디렉터리 생성 실패: {}", uploadPath);
                    throw new RuntimeException("업로드 디렉터리를 생성할 수 없습니다: " + uploadPath);
                }
            } else {
                log.info("업로드 디렉터리가 이미 존재함: {}", uploadPath);
            }
            
            if (!directory.canWrite()) {
                log.error("업로드 디렉터리에 쓰기 권한 없음: {}", uploadPath);
                throw new RuntimeException("업로드 디렉터리에 쓰기 권한이 없습니다: " + uploadPath);
            }
            
            if (!directory.canRead()) {
                log.error("업로드 디렉터리에 읽기 권한 없음: {}", uploadPath);
                throw new RuntimeException("업로드 디렉터리에 읽기 권한이 없습니다: " + uploadPath);
            }
            
            try {
                File testFile = new File(directory, "test_" + System.currentTimeMillis() + ".txt");
                boolean created = testFile.createNewFile();
                if (created) {
                    log.info("테스트 파일 생성 성공: {}", testFile.getAbsolutePath());
                    boolean deleted = testFile.delete();
                    if (deleted) {
                        log.info("테스트 파일 삭제 성공");
                    } else {
                        log.warn("테스트 파일 삭제 실패, 권한 문제가 있을 수 있음");
                    }
                } else {
                    log.warn("테스트 파일 생성 실패, 권한 문제가 있을 수 있음");
                }
            } catch (IOException e) {
                log.error("테스트 파일 I/O 오류: {}", e.getMessage());
                throw new RuntimeException("업로드 디렉터리 테스트 중 오류 발생", e);
            }
            
            log.info("업로드 디렉터리 초기화 완료: {}", uploadPath);
        } catch (Exception e) {
            log.error("업로드 디렉터리 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("업로드 디렉터리 초기화 실패", e);
        }
    }

    private final QnaRepository qnaRepository;
    private final QnaReplyRepository qnaReplyRepository;
    private static final int DEFAULT_PAGE_SIZE = 10;

    // QnA 등록
    @Override
    @Transactional
    public QnaDTO register(QnaDTO dto, MultipartFile[] imageFiles) {
        log.info("QnA 등록 시작 - 제목: '{}', 작성자: '{}'", dto.getTitle(), dto.getWriter());
        
        try {
            Qna qna = Qna.builder()
                    .title(dto.getTitle())
                    .questionText(dto.getQuestionText())
                    .writer(dto.getWriter())
                    .regDate(LocalDateTime.now())
                    .modDate(LocalDateTime.now())
                    .completed(false)
                    .build();
            
            log.info("Qna 엔티티 생성 완료: {}", qna);
            Qna savedQna = qnaRepository.save(qna);
            log.info("Qna 엔티티 저장 완료: ID={}", savedQna.getQno());

            // 이미지 파일 처리
            if (imageFiles != null && imageFiles.length > 0) {
                log.info("이미지 파일 처리 시작: {} 개", imageFiles.length);
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        String ext = "";
                        if (originalFilename != null && originalFilename.contains(".")) {
                            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }

                        String storedFileName = UUID.randomUUID().toString().replaceAll("-", "") + ext;
                        log.info("이미지 저장 - 원본: {}, 저장명: {}", originalFilename, storedFileName);
                        
                        File saveFile = new File(uploadPath, storedFileName);
                        try {
                            file.transferTo(saveFile);
                            log.info("파일 저장 성공: {}", saveFile.getAbsolutePath());
                        } catch (IOException e) {
                            log.error("파일 업로드 실패: {}", originalFilename, e);
                            throw new RuntimeException("파일 업로드 실패: " + originalFilename, e);
                        }

                        QnaImage qnaImage = QnaImage.builder()
                                .imageName(storedFileName)
                                .build();
                        savedQna.addImage(qnaImage);
                        log.info("이미지 엔티티 추가: {}", storedFileName);
                    }
                }
                // 이미지 등록 후 다시 Qna 엔티티 저장 (연관관계 반영)
                log.info("이미지 연관관계 저장 전 Qna: {}, 이미지 수: {}", savedQna.getQno(), savedQna.getImages().size());
                savedQna = qnaRepository.save(savedQna);
                log.info("이미지 연관관계 저장 후 Qna: {}, 이미지 수: {}", savedQna.getQno(), savedQna.getImages().size());
            }
            
            QnaDTO resultDto = convertToDTO(savedQna);
            log.info("QnA 등록 완료 - ID: {}", resultDto.getQno());
            return resultDto;
        } catch (Exception e) {
            log.error("QnA 등록 중 오류 발생", e);
            throw e;
        }
    }

    // QnA 조회
    @Override
    @Transactional(readOnly = true)
    public Qna getQna(Long qno) {
        return qnaRepository.findQnaWithImages(qno)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + qno));
    }


    // QnA 숨김 처리
    @Override
    public Qna hideQna(Long qno, boolean hide) {
        Qna qna = getQna(qno);
//        if (!isOwnerOrAdmin(qno)) {
//            throw new AccessDeniedException("질문 작성자 또는 관리자만 숨김처리할 수 있습니다.");
//        }
//
//        qna.setHide(hide);
        return qnaRepository.save(qna);
    }

    // QnA 완료 처리 (사용자가 직접 처리)
    @Override
    public Qna answeredQna(Long qno, boolean completed) {
        Qna qna = getQna(qno);
        qna.setCompleted(completed);
        qna.setModDate(LocalDateTime.now());
        return qnaRepository.save(qna);
    }

    // QnA 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<Qna> getQnaList(QnaDTO dto) {
        return qnaRepository.findAll();
    }

    // Reply 엔티티를 DTO로 변환
    @Override
    public QnaReplyDTO convertToQnaReply(QnaReply reply) {
        return QnaReplyDTO.builder()
                .rno(reply.getRno())
                .replyText(reply.getReplyText())
                .replyDate(reply.getReplyDate())
                .admin(reply.isAdmin())
                .writer(reply.getWriter())
                .qno(reply.getQna().getQno())
                .build();
    }

    // Reply DTO를 엔티티로 변환
    @Override
    public QnaReply convertToQnaReplyEntity(QnaReplyDTO replyDTO) {
        Qna qna = qnaRepository.findById(replyDTO.getQno())
                .orElseThrow(() -> new RuntimeException("Qna not found"));

        return QnaReply.builder()
                .qna(qna)
                .replyText(replyDTO.getReplyText())
                .writer(replyDTO.getWriter())
                .admin(replyDTO.isAdmin())
                .replyDate(LocalDateTime.now())
                .build();
    }

    // QnA 수정
    @Override
    public Qna modifyQna(Long qno, QnaDTO dto) {
        Qna qna = getQna(qno);
        qna.setTitle(dto.getTitle());
        qna.setQuestionText(dto.getQuestionText());
        qna.setModDate(LocalDateTime.now());
        return qnaRepository.save(qna);
    }

    @Transactional
    @Override
    public QnaReplyDTO createReply(QnaReplyDTO replyDTO) {
        Qna qna = qnaRepository.findById(replyDTO.getQno())
                .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다: " + replyDTO.getQno()));
        QnaReply reply = QnaReply.builder()
                .qna(qna) // pk 키
                .replyText(replyDTO.getReplyText()) // 답변내용
                .writer(replyDTO.getWriter()) // 작성자
                .admin(replyDTO.isAdmin()) // 관리자 여부
                .replyDate(LocalDateTime.now()) // 답변날짜
                .build();
        if (replyDTO.isAdmin()) {
            qnaRepository.save(qna);
            log.info("관리자 답변 시간 업데이트 완료 - 질문번호: {}", qna.getQno());
        }

        // 답변을 저장하고  변환하여 반환
        QnaReply savedReply = qnaReplyRepository.save(reply);
        return convertToQnaReply(savedReply);
    }

    @Override
    @Transactional(readOnly = true)
    public QnaReplyListDTO getRepliesWithPaging(Long qno, Long lastRno) {
        if (!qnaRepository.existsById(qno)) {
            throw new EntityNotFoundException("질문을 찾을 수 없습니다: " + qno);
        }

        // 페이징 처리된 댓글 조회
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE + 1);
        List<QnaReply> replies;

        if (lastRno == null) {
            replies = qnaReplyRepository.findFirstPageByQno(qno, pageable);
            log.info("첫 페이지 댓글 조회 - QNO: {}", qno);
        } else {
            replies = qnaReplyRepository.findRepliesWithPaging(qno, lastRno, pageable);
            log.info("다음 페이지 댓글 조회 - QNO: {}, lastRno: {}", qno, lastRno);
        }

        // 다음 페이지 존재 여부 확인
        boolean hasNext = replies.size() > DEFAULT_PAGE_SIZE;
        if (hasNext) {
            replies = replies.subList(0, DEFAULT_PAGE_SIZE);
        }

        // 전체 댓글 수 조회
        long totalCount = qnaReplyRepository.countByQno(qno);

        // DTO 변환
        List<QnaReplyDTO> replyDTOs = replies.stream()
                .map(this::convertToQnaReply)
                .toList();

        // 응답 DTO 구성
        return QnaReplyListDTO.builder()
                .replies(replyDTOs)
                .hasNext(hasNext)
                .lastRno(replies.isEmpty() ? null : replies.get(replies.size() - 1).getRno())
                .totalCount((int) totalCount)
                .build();
    }

    @Transactional
    @Override
    public void remove(Long qno) {
        log.info("QnA 삭제: {}", qno);

        qnaRepository.findById(qno)
                .orElseThrow(() -> new EntityNotFoundException("QnA를 찾을 수 없습니다. ID: " + qno));

        qnaRepository.deleteById(qno);
    }

    @Override
    public boolean canDelete(Long qno, UserDetails userDetails) {
        Qna qna = qnaRepository.findById(qno)
                .orElseThrow(() -> new EntityNotFoundException("QnA not found"));
        return userDetails != null && (
                userDetails.getUsername().equals(qna.getWriter()) ||
                        userDetails.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
    }


    // Qna 엔티티를 DTO로 변환
    private QnaDTO convertToDTO(Qna qna) {
        // 이미지 정보를 DTO로 변환
        List<QnaImageDTO> imageDTOs = qna.getImages().stream()
                .map(image -> {
                    log.debug("이미지 변환: ino={}, imageName={}", image.getIno(), image.getImageName());
                    return QnaImageDTO.builder()
                            .ino(image.getIno())
                            .imageName(image.getImageName())
                            .build();
                })
                .collect(Collectors.toList());

        // DTO 생성 시 이미지 정보 포함
        QnaDTO dto = QnaDTO.builder()
                .qno(qna.getQno())
                .title(qna.getTitle())
                .writer(qna.getWriter())
                .questionText(qna.getQuestionText())
                .regDate(qna.getRegDate())
                .modDate(qna.getModDate())
                .hidden(qna.isHide())
                .completed(qna.isCompleted())
                .images(imageDTOs)  // 이미지 정보 추가
                .build();

        log.debug("변환된 DTO: {}, 이미지 개수: {}", dto, imageDTOs.size());
        return dto;
    }

    @Override
    public Resource getImageAsResource(Long ino) throws IOException {
        QnaImage qnaImage = qnaImageRepository.findById(ino)
                .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다: " + ino));

        Path imagePath = Paths.get(uploadPath).resolve(qnaImage.getImageName());

        try {
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("이미지 파일을 읽을 수 없습니다: " + qnaImage.getImageName());
            }
        } catch (MalformedURLException e) {
            throw new IOException("이미지 파일 경로가 잘못되었습니다: " + imagePath, e);
        }
    }

    @Override
    public Page<QnaListDTO> getQnaList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("regDate").descending());
        return qnaRepository.findAllByOrderByRegDateDesc(pageable)
                .map(qna -> QnaListDTO.builder()
                        .qno(qna.getQno())
                        .title(qna.getTitle())
                        .questionText(qna.getQuestionText())
                        .writer(qna.getWriter())
                        .completed(qna.isCompleted())
                        .regDate(qna.getRegDate())
                        .modDate(qna.getModDate())
                        .hidden(qna.isHide())
                        .build());

    }

    @Override
    public long getTotalCount() {
        return qnaRepository.count();
    }

    @Override
    public long increaseViewCount(Long qno) {
        qnaRepository.increaseViewCount(qno);
        Qna qna = qnaRepository.findById(qno)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        return qna.getView().intValue();  // Long을 int로 변환
    }

    @Override
    public List<ViewCountDTO> getAllViewCounts() {
        return qnaRepository.findAll().stream()
                .map(board -> new ViewCountDTO(
                        board.getQno(),
                        Math.toIntExact(board.getView())  // Long -> int 안전 변환
                ))
                .collect(Collectors.toList());
    }
}


