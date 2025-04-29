package org.zerock.workoutproject.board.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.workoutproject.board.domain.Board;
import org.zerock.workoutproject.board.domain.QBoard;
import org.zerock.workoutproject.board.domain.QBoardReply;
import org.zerock.workoutproject.board.dto.BoardImageDTO;
import org.zerock.workoutproject.board.dto.BoardListAllDTO;
import org.zerock.workoutproject.board.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {
    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> searchAll(String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);
        if (keyword != null) {
            query.where(board.title.contains(keyword));
        }
        this.getQuerydsl().applyPagination(pageable, query);
        List<Board> results = query.fetch();
        Long count = query.fetchCount();
        return new PageImpl<Board>(results, pageable, count);
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QBoardReply reply = QBoardReply.boardReply;
        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));
        query.groupBy(board);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null) {
            query.where(board.title.contains(keyword));
        }
        query.where(booleanBuilder);
        query.where(board.bno.gt(0L));

        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.bean(BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.regDate,
                board.writer,
                reply.count().as("replyCount")
        ));

        this.getQuerydsl().applyPagination(pageable, dtoQuery);
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();
        Long count = dtoQuery.fetchCount();
        return new PageImpl<>(dtoList, pageable, count);
    }

    @Override
    public Page<BoardListAllDTO> searchWithAll(String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QBoardReply reply = QBoardReply.boardReply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));
        query.groupBy(board);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null) {
            query.where(board.title.contains(keyword));
        }
        query.where(booleanBuilder);
        query.where(board.bno.gt(0L));
        getQuerydsl().applyPagination(pageable, query);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(board, reply.countDistinct());
        // tuple 실행
        List<Tuple> tupleList = tupleJPQLQuery.fetch();
        // Tuple의 결과를 DTO로 변경하는 부분
        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {
            // Tuple의 실행결과 중 notice 데이터를 변수에 설정
            Board board1 = (Board) tuple.get(board);
            // reply.countDistinct()의 실행 결과를 변수에 저장
            long replyCount = tuple.get(1, Long.class);

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();
            List<BoardImageDTO> imgDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());
            dto.setBoardImages(imgDTOS);
            return dto;
        }).collect(Collectors.toList());
        long totalCount = query.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

}
