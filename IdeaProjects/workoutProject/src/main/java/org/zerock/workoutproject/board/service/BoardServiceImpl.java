package org.zerock.workoutproject.board.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.workoutproject.board.domain.Board;
import org.zerock.workoutproject.board.dto.*;
import org.zerock.workoutproject.board.repository.BoardRepository;
import org.zerock.workoutproject.board.repository.ReplyRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final ReplyRepository replyRepository;

    @Override
    public Long register(BoardDTO dto) {
        Board board = dtoToEntity(dto);
        Long bno = boardRepository.save(board).getBno();
        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {
        Optional<Board> result = boardRepository.findByIdWithImages(bno);
        Board board = result.orElseThrow();
//        BoardDTO dto = modelMapper.map(board, BoardDTO.class);
        BoardDTO dto = entityToDto(board);
        return dto;
    }

    @Override
    public Long modify(BoardDTO dto) {
        Optional<Board> result = boardRepository.findById(dto.getBno());
        Board board = result.orElseThrow();
        board.change(dto.getTitle(), dto.getContent());
        board.clearImages();
        if (dto.getFileNames() != null) {
            for (String fileName : dto.getFileNames()) {
                String[] arr = fileName.split("_",2);
                board.addImage(arr[0], arr[1]);
            }
        }
        boardRepository.save(board);
        return board.getBno();
    }

    @Override
    public void remove(Long bno) {
        replyRepository.deleteByBoard_bno(bno);
        boardRepository.deleteById(bno);
    }


    @Override
    public PageResponseDTO<BoardDTO> searchList(PageRequestDTO pageRequestDTO) {
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        Page<Board> result = boardRepository.searchAll(keyword, pageable);
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(keyword, pageable);

        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO req) {
        String keyword = req.getKeyword();
        Pageable pageable = req.getPageable("bno");
        Page<BoardListAllDTO> result = boardRepository.searchWithAll(keyword, pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(req)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    public List<BoardDTO> getRecentPosts(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("regDate").descending());
        List<Board> recentBoards = boardRepository.findAll(pageable).getContent();

        return recentBoards.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BoardDTO getPopularPost() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("view").descending());
        List<Board> popularBoards = boardRepository.findAll(pageable).getContent();

        if (!popularBoards.isEmpty()) {
            return modelMapper.map(popularBoards.get(0), BoardDTO.class);
        }
        return null;
    }

    @Override
    public List<ViewCountDTO> getAllViewCounts() {
        return boardRepository.findAll().stream()
                .map(board -> new ViewCountDTO(
                        board.getBno(),
                        Math.toIntExact(board.getView())  // Long -> int
                ))
                .collect(Collectors.toList());
    }

    @Override
    public int increaseViewCount(Long bno) {
        boardRepository.increaseViewCount(bno);
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        return board.getView().intValue();  // Long을 int로 변환
    }

}
