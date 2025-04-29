import HttpClient from './module/HttpClient.js';
import UIManager from './module/UIManager.js';

const httpClient = new HttpClient();
const uiManager = new UIManager();

let isFirstLoad = true;

function saveCurrentPage(page) {
    sessionStorage.setItem('qnaCurrentPage', page.toString());
}

function loadCurrentPage() {
    const savedPage = sessionStorage.getItem('qnaCurrentPage');
    return savedPage ? parseInt(savedPage) : 1;
}

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);

    const year = date.getFullYear(); // 연도
    const month = date.getMonth() + 1; // 월
    const day = date.getDate(); // 일

    return `${year}-${month}-${day}`;
}

export async function getQnaList(page = loadCurrentPage(), size = 10) {
    const tbody = document.getElementById('qnaListBody');
    if (!tbody) {
        console.error('qnaListBody 요소를 찾을 수 없습니다.');
        return;
    }

    // 로딩 시작: 로딩 행 추가
    const tableContainer = document.querySelector('.table-container');
    if (tableContainer) {
        // 첫 로딩이면 페이드인을 위해 낮은 투명도 설정
        if (isFirstLoad) {
            tableContainer.style.opacity = '0.8';
        }
    }
    tbody.innerHTML = '<tr class="loading-row"><td colspan="5" class="loading-cell">데이터 로딩 중...</td></tr>';

    try {
        saveCurrentPage(page);

        const response = await httpClient.get(`/qna/api/list?page=${page}&size=${size}`);
        console.log('Fetched QnA List:', response);

        const qnaList = response.content;
        const totalPages = response.totalPages;
        const currentPage = response.number + 1;

        // QnA 목록을 테이블에 렌더링 (데이터가 없을 경우 빈 메시지 표시)
        if (qnaList && qnaList.length > 0) {
            tbody.innerHTML = qnaList.map(qna => `
                <tr class="${isFirstLoad ? 'fade-in' : ''}">
                    <td>${qna.qno}</td>
                    <td><a href="/qna/view/${qna.qno}">${qna.title}</a></td>
                    <td>${qna.writer}</td>
                    <td>${formatDate(qna.regDate)}</td>
                    <td>
                        <span class="status-badge ${qna.completed ? 'status-completed' : 'status-pending'}">
                            ${qna.completed ? '완료' : '진행중'}
                        </span>
                    </td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="5">등록된 질문이 없습니다.</td></tr>';
        }

        // 페이지네이션 렌더링 시에도 저장된 페이지 정보 활용
        uiManager.renderPagination(totalPages, currentPage, getQnaList);

        // 첫 로딩 후 부드러운 페이드인 효과
        if (isFirstLoad && tableContainer) {
            setTimeout(() => {
                tableContainer.style.opacity = '1';
                isFirstLoad = false;
            }, 100);
        }

    } catch (error) {
        console.error('QnA 목록을 가져오는 중 오류 발생:', error);
        tbody.innerHTML = '<tr class="empty-row"><td colspan="5">목록을 불러오는 중 오류가 발생했습니다.</td></tr>'; // 오류 메시지 표시
        return null;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    // 저장된 페이지 번호로 시작
    const savedPage = loadCurrentPage();
    getQnaList(savedPage);
});