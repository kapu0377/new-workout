package org.zerock.workoutproject.exercise.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.workoutproject.exercise.domain.Exercise;
import org.zerock.workoutproject.exercise.domain.QExercise;
import org.zerock.workoutproject.exercise.domain.QReply;
import org.zerock.workoutproject.exercise.dto.ExerciseImageDTO;
import org.zerock.workoutproject.exercise.dto.ExerciseListAllDTO;
import org.zerock.workoutproject.exercise.dto.ExerciseListReplyCountDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ExerciseSearchImpl extends QuerydslRepositorySupport implements ExerciseSearch {

  public ExerciseSearchImpl() {
    super(Exercise.class);
  }

  @Override
  public Page<Exercise> searchAll(String keyword, Pageable pageable) {
    // Querydsl용 entity 선언
    QExercise exercise = QExercise.exercise;
    // JPQL SELECT쿼리문 생성
    JPQLQuery<Exercise> query = from(exercise);
    // keyword가 있을 경우 실행
    if (keyword != null && !keyword.trim().isEmpty()) {
      // title열을 기준으로 like 검색
      query.where(exercise.title.contains(keyword));
    }
    query.where(exercise.eno.gt(0));
    //JPQLQuery에 페이지네이션 설정
    this.getQuerydsl().applyPagination(pageable,query);
    // Query실행 후 결과를 저장
    List<Exercise> list = query.fetch();
    // Query실행 결과의 개수 저장
    Long count = query.fetchCount();
    // Page<Notice> 데이터로 반환
    return new PageImpl<Exercise>(list,pageable,count);
  }

  @Override
  public Page<ExerciseListReplyCountDTO> searchWithReplyCount(String keyword, Pageable pageable) {
    QExercise exercise = QExercise.exercise;
    QReply reply = QReply.reply;
    // SELECT * FROM board
    JPQLQuery<Exercise> query = from(exercise);
    // LEFT JOIN reply ON  reply.board_bno = board.bno
    query.leftJoin(reply).on(reply.exercise.eq(exercise));
    query.groupBy(exercise);

    //동적 쿼리를 만들기 위한 클래스
    BooleanBuilder booleanBuilder = new BooleanBuilder();


    query.where(booleanBuilder);
    // bno>0
    query.where(exercise.eno.gt(0));

    JPQLQuery<ExerciseListReplyCountDTO> dtoQuery = query.select(Projections.
            bean(ExerciseListReplyCountDTO.class
                    ,exercise.eno
                    ,exercise.title
                    ,exercise.content
                    ,exercise.regDate
                    ,exercise.url
                    ,reply.count().as("replyCount")
            ));
    //paging 처리
    this.getQuerydsl().applyPagination(pageable, dtoQuery);

    // fetch를 이용하여 위에서 만든 query문을 실행
    List<ExerciseListReplyCountDTO> list = dtoQuery.fetch();
    // fetch실행 결과의 갯수
    Long count = dtoQuery.fetchCount();

    return new PageImpl<>(list,pageable,count);
  }

  @Override
  public Page<ExerciseListAllDTO> searchWithAll(String keyword, Pageable pageable) {
    QExercise exercise = QExercise.exercise;
    QReply reply = QReply.reply;
    JPQLQuery<Exercise> exerciseJPQLQuery = from(exercise);
    //left join : 댓글을 조회하기 위한 JOIN문 : 댓글은 단방향으로 설정했기 떄문에 board만 검색하면 댓글 데이터는 함께 조회하지 않습니다.
    exerciseJPQLQuery.leftJoin(reply).on(reply.exercise.eq(exercise));
    //동적 쿼리를 만들기 위한 클래스
    BooleanBuilder booleanBuilder = new BooleanBuilder();

    exerciseJPQLQuery.where(booleanBuilder);
    // bno>0
    exerciseJPQLQuery.where(exercise.eno.gt(0));
    if(keyword != null && !keyword.trim().isEmpty()) {
      exerciseJPQLQuery.where(exercise.title.contains(keyword));
    }


    exerciseJPQLQuery.groupBy(exercise);
    //페이징 설정
    getQuerydsl().applyPagination(pageable, exerciseJPQLQuery);
    // JPA에서의 Tuple : 엔티티가 아닌 특정열의 결과를 받기 위한 기능
    //SELECT bno,title,content,writer,moddate,regdate,subject,댓글수
    JPQLQuery<Tuple> tupleJPQLQuery = exerciseJPQLQuery.select(exercise,reply.countDistinct());
    // tuple실행
    List<Tuple> tupleList = tupleJPQLQuery.fetch();
    // Tuple의 결과를 DTO로 변경하는 부분
    List<ExerciseListAllDTO> dtoList = tupleList.stream().map(tuple ->{
      // Tuple의 결과에서 Board 엔티티의 내용을 DTO에 설정
      Exercise exercise1 = (Exercise)tuple.get(exercise);
      long replyCount = tuple.get(1,Long.class);
      ExerciseListAllDTO dto = ExerciseListAllDTO.builder()
              .eno(exercise1.getEno())
              .title(exercise1.getTitle())
              .content(exercise1.getContent())
              .regDate(exercise1.getRegDate())
              .replyCount(replyCount)
              .url(exercise1.getUrl())
              .build();
      //BoardImageDTO 처리 , Board엔티티의 imageSet데이터를 BoardImageDTO로 변환
      List<ExerciseImageDTO> imageDTOS = exercise1.getImageSet().stream().sorted()
              .map(boardImage -> ExerciseImageDTO.builder()
                      .uuid(boardImage.getUuid())
                      .fileName(boardImage.getFileName())
                      .ord(boardImage.getOrd())
                      .build()
              ).collect(Collectors.toList());
      dto.setNoticeImages(imageDTOS);
      return dto;
    }).collect(Collectors.toList());
    long totalCount = exerciseJPQLQuery.fetchCount();

    return new PageImpl<>(dtoList , pageable, totalCount);
  }

}
