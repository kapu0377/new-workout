package org.zerock.workoutproject.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@ToString
public class PageResponseDTO<E> {
    private int page;
    private int size;
    private int total;

    private int start;
    private int end;
    private int last;
    private boolean prev;
    private boolean next;
    private List<Integer> pageList;
    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {
        if (total <= 0) {
            this.pageList = List.of(); // 빈 리스트 반환
            return;
        }

        this.page = Math.min(pageRequestDTO.getPage(), (int) Math.ceil((double) total / pageRequestDTO.getSize())); // 페이지 제한
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        // 전체 마지막 페이지 계산
        this.last = (int) Math.ceil((double) total / size);

        // 현재 페이지 리스트의 끝 번호 계산
        this.end = (int) (Math.ceil((double) this.page / 10)) * 10;

        // 현재 페이지 리스트의 시작 번호 계산
        this.start = this.end - 9;

        // end가 전체 마지막 페이지를 넘지 않도록 제한
        if (this.end > this.last) {
            this.end = this.last;
            this.start = Math.max(this.end - 9, 1); // end가 last일 때 start 조정
        }

        // 이전/다음 버튼 활성화 여부
        this.prev = this.start > 1;
        this.next = this.page < this.last;

        // 페이지 번호 리스트 생성
        this.pageList = IntStream.rangeClosed(this.start, this.end)
                .boxed()
                .collect(Collectors.toList());

        // 디버깅 출력
        System.out.println("=====================================");
        System.out.println("Page: " + this.page);
        System.out.println("Start: " + this.start);
        System.out.println("End: " + this.end);
        System.out.println("Last: " + this.last);
        System.out.println("Page List: " + this.pageList);
    }

}
