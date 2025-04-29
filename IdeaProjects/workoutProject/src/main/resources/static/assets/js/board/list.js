let isFirstLoad = true;

document.addEventListener('DOMContentLoaded', function () {
    const tableContainer = document.querySelector('.table-container');
    if (tableContainer && isFirstLoad) {
        tableContainer.style.opacity = '0.8';
        
        setTimeout(() => {
            tableContainer.style.opacity = '1';
            isFirstLoad = false;
            
            document.querySelectorAll('.board-table tbody tr').forEach((row, index) => {
                row.classList.add('fade-in');
                row.style.animationDelay = (index * 0.05) + 's';
            });
        }, 100);
    }

    initializeBoardFunctions();
});


function initializeBoardFunctions() {
    setupBoardClickEvents();
    setupPaginationEvents();
    updateViewCounts();
}


function setupPaginationEvents() {
    const paginationButtons = document.querySelectorAll('.pagination-btn');
    console.log(`페이지네이션 버튼 개수: ${paginationButtons.length}`);

    const paginationContainer = document.querySelector('.pagination-container');
    if (paginationContainer) {
        paginationContainer.style.opacity = '0.7';
        setTimeout(() => {
            paginationContainer.style.opacity = '1';
        }, 200);
    }

    paginationButtons.forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();

            const pageNum = this.getAttribute('data-num');
            console.log(`클릭한 버튼의 페이지 번호: ${pageNum}`);

            if (pageNum) {
                loadBoardPage(pageNum);
            } else {
                console.warn('data-num 속성이 없습니다.');
            }
        });
    });
}

/**
 * 페이지 이동 함수
 * @param {string} page - 페이지 번호
 */
function loadBoardPage(page) {
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('page', page);
    window.location.href = currentUrl.toString();
}

/**
 * 조회수 증가 처리
 */
function setupBoardClickEvents() {
    const boardLinks = document.querySelectorAll('.post-title');
    boardLinks.forEach(link => {
        link.addEventListener('click', async function (e) {
            e.preventDefault();

            const url = new URL(this.href);
            const bno = url.searchParams.get('bno');
            console.log(`게시물 링크 클릭: bno=${bno}`);

            try {
                // CSRF 토큰 정보 가져오기
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
                
                // 조회수 증가 API 호출
                const response = await fetch(`/board/api/view/${bno}`, { 
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        ...(csrfToken && csrfHeader ? { [csrfHeader]: csrfToken } : {})
                    },
                    credentials: 'same-origin'
                });
                
                console.log(`조회수 증가 응답 상태: ${response.status}`);

                if (!response.ok) {
                    throw new Error(`조회수 증가 실패: ${response.status}`);
                }

                // 성공적으로 조회수 증가 후 페이지 이동
                window.location.href = this.href;
            } catch (error) {
                console.error('조회수 증가 중 오류:', error);
                // 오류가 발생해도 페이지 이동
                window.location.href = this.href;
            }
        });
    });
}

/**
 * 조회수 갱신 함수
 */
async function updateViewCounts() {
    try {
        console.log("조회수 갱신 요청 시작");
        const response = await fetch('/board/api/view-counts', {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
            }
        });
        
        if (!response.ok) {
            throw new Error(`조회수 요청 실패: 상태 코드 ${response.status}`);
        }

        const viewCounts = await response.json();
        console.log('조회수 데이터:', viewCounts);

        // 각 게시물의 조회수 업데이트
        viewCounts.forEach(({ bno, viewCount }) => {
            const viewCountElement = document.getElementById(`view-count-${bno}`);
            if (viewCountElement) {
                console.log(`업데이트 성공: view-count-${bno} -> ${viewCount}`);
                viewCountElement.textContent = viewCount;
            } else {
                console.warn(`view-count-${bno} 요소를 찾을 수 없음`);
            }
        });
    } catch (error) {
        console.error('조회수 갱신 중 오류:', error);
    }
} 