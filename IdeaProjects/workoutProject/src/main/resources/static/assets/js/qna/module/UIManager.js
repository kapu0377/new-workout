export default class UIManager {
    constructor() {
        this.actionsContainer = document.getElementById('postActions');
        this.getQnaList = document.getElementById('getQnaList');
        this.paginationContainer = document.getElementById('pagination'); // 페이징 컨테이너 초기화
    }

    triggerReflow(element) {
        element.style.display = 'none';
        element.offsetHeight;
        element.style.display = '';
    }

    showError(message) {
        const errorDiv = document.getElementById('errorMessage');
        if (!errorDiv) return;
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 3000);
    }

    showSuccess(message) {
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.textContent = message;
        document.body.appendChild(successDiv);

        setTimeout(() => {
            successDiv.style.opacity = '0';
            setTimeout(() => document.body.removeChild(successDiv), 500);
        }, 2000);
    }

    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return new Intl.DateTimeFormat('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        }).format(date);
    }

    createButton(text, className, onClick) {
        const button = document.createElement('button');
        button.textContent = text;
        button.className = className;
        button.onclick = onClick;
        return button;
    }

    updateActionButtons(postData, isAdmin, currentUser, postWriter, toggleCallback, deleteCallback) {
        if (!this.actionsContainer) {
            console.error('actionsContainer not found');
            return;
        }

        this.actionsContainer.innerHTML = '';

        if (isAdmin || (currentUser === postWriter && !postData.completed)) {
            const toggleButton = this.createButton(
                postData.completed ? '진행중으로 변경' : '완료 처리',
                `button primary-button ${postData.completed ? 'status-pending' : 'status-completed'}`,
                () => toggleCallback()
            );

            this.actionsContainer.appendChild(toggleButton);
        }

        if (isAdmin || (currentUser === postWriter && !postData.completed)) {
            const deleteButton = this.createButton('삭제', 'button delete-button', () => {
                if (confirm('정말 삭제하시겠습니까?')) {
                    deleteCallback();
                }
            });

            this.actionsContainer.appendChild(deleteButton);
        }

        this.triggerReflow(this.actionsContainer);
    }

    /**
     * @param {number} totalPages - 전체 페이지 수
     * @param {number} currentPage - 현재 페이지 번호
     * @param {Function} pageCallback - 페이지 변경 시 호출될 콜백 함수
     */
    renderPagination(totalPages, currentPage, pageCallback) {
        const paginationElement = document.getElementById('pagination');
        if (!paginationElement) {
            console.error('pagination 요소를 찾을 수 없습니다.');
            return;
        }

        paginationElement.style.opacity = '0.7';

        let paginationHTML = '';

        // '처음' 버튼
        if (currentPage > 1) {
            paginationHTML += `<button data-page="1" class="pagination-btn first-page">처음</button>`;
        }

        // 페이지 번호 버튼 (최대 5개)
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, startPage + 4);

        for (let i = startPage; i <= endPage; i++) {
            const activeClass = i === currentPage ? 'active' : '';
            paginationHTML += `<button data-page="${i}" class="pagination-btn ${activeClass}">${i}</button>`;
        }

        // '마지막' 버튼
        if (currentPage < totalPages) {
            paginationHTML += `<button data-page="${totalPages}" class="pagination-btn last-page">마지막</button>`;
        }

        paginationElement.innerHTML = paginationHTML;

        // 페이드인 효과
        setTimeout(() => {
            paginationElement.style.opacity = '1';
        }, 100);

        // 페이지 버튼 클릭 이벤트 등록
        document.querySelectorAll('.pagination-btn').forEach(button => {
            button.addEventListener('click', () => {
                const page = parseInt(button.getAttribute('data-page'));
                if (pageCallback && typeof pageCallback === 'function') {
                    pageCallback(page);
                }
            });
        });
    }
}
