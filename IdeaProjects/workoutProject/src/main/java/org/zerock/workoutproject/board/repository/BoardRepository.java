package org.zerock.workoutproject.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.zerock.workoutproject.board.domain.Board;
import org.zerock.workoutproject.board.repository.search.BoardSearch;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    Page<Board> findByTitleContainingOrderByBnoDesc(String keyword, Pageable pageable);

    // 게시글과 댓글 수를 함께 조회하는 쿼리
    @Query("SELECT b, count(r) " +
            "FROM Board b " +
            "LEFT OUTER JOIN BoardImage r ON r.board = b " +  // BoardImage와의 조인으로 수정
            "GROUP BY b " +
            "ORDER BY b.bno ASC")
    Page<Object[]> searchWithReplyCount(Pageable pageable);



    // 조회수 증가 쿼리
    @Modifying
    @Query("UPDATE Board b SET b.view = b.view + 1 WHERE b.bno = :bno")
    void increaseViewCount(Long bno);

    // 인기 게시글(조회수가 많은 순)을 가져오는 쿼리
    @Query("SELECT b FROM Board b ORDER BY b.view ASC ")
    Page<Board> findMostViewed(Pageable pageable);

    @Query("select b from Board b where b.title like concat('%', :keyword, '%')")
    Page<Board> findKeyword(String keyword, Pageable pageable);
    @Query(value = "select now()", nativeQuery = true)
    String getTime();

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithImages(Long bno);
}
