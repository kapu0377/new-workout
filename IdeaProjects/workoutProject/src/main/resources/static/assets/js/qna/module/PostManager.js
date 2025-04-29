export default class PostManager {
    constructor(qno, httpClient, uiManager, replyManager) {
        this.qno = qno;
        this.httpClient = httpClient;
        this.uiManager = uiManager;
        this.replyManager = replyManager;
        this.postData = null;
        this.currentUser = null;
        this.imageContainer = document.getElementById('postImages');
        this.setupImageModal();
        this.actionsContainer = document.getElementById('postActions');
        this.getQnaList = document.getElementById('getQnaList');
        this.paginationContainer = document.getElementById('pagination');

        this.currentPage = this.loadCurrentPage();
    }

    saveCurrentPage(pageNumber) {
        localStorage.setItem('qnaCurrentPage', pageNumber.toString());
    }

    loadCurrentPage() {
        const savedPage = localStorage.getItem('qnaCurrentPage');
        return savedPage ? parseInt(savedPage) : 1; 
    }

    renderPagination(totalPages, currentPage, getQnaListCallback) {
        if (!this.paginationContainer) {
            console.error('Pagination 요소를 찾을 수 없습니다.');
            return;
        }

        this.paginationContainer.innerHTML = '';

        for (let page = 1; page <= totalPages; page++) {
            const button = document.createElement('button');
            button.textContent = page;
            button.className = page === currentPage ? 'pagination-btn active' : 'pagination-btn';
            button.addEventListener('click', () => {
                this.saveCurrentPage(page);

                if (typeof getQnaListCallback === 'function') {
                    getQnaListCallback(page);
                } else {
                    console.error('getQnaListCallback is not a function');
                }
            });

            this.paginationContainer.appendChild(button);
        }
    }


    checkAdminStatus() {
        if (!this.currentUser?.authorities) {
            return false;
        }
        return this.currentUser.authorities.some(auth => auth === 'ROLE_ADMIN');
    }

    setupImageModal() {
        const modal = document.getElementById('imageModal');
        const modalImg = document.getElementById('modalImage');

        this.imageContainer.addEventListener('click', (e) => {
            const clickedImage = e.target.closest('img');
            if (clickedImage) {
                modal.style.display = "block";
                modalImg.src = clickedImage.src;
                setTimeout(() => modal.classList.add('show'), 10);
            }
        });

        modal.addEventListener('click', () => {
            modal.classList.remove('show');
            setTimeout(() => modal.style.display = "none", 300);
        });


        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.style.display === "block") {
                modal.style.display = "none";
            }
        });
    }


    async loadPostData() {
        try {
            this.currentUser = await this.httpClient.get('/qna/api/user/current');
            this.postData = await this.httpClient.get(`/qna/api/${this.qno}`);
            this.updatePostUI();
        } catch (error) {
            this.uiManager.showError('게시글을 불러오는 중 오류가 발생했습니다.');
        }
    }

    updatePostUI() {
        document.getElementById('postTitle').textContent = this.postData.title;
        document.getElementById('postWriter').textContent = this.postData.writer;
        document.getElementById('postDate').textContent = this.uiManager.formatDate(this.postData.regDate);
        document.getElementById('postContent').textContent = this.postData.questionText;

        const statusBadge = document.getElementById('postStatus');
        if (statusBadge) {
            if (this.postData.completed) {
                statusBadge.textContent = '완료';
                statusBadge.className = 'status-badge status-completed';
            } else {
                statusBadge.textContent = '진행중';
                statusBadge.className = 'status-badge status-pending';
            }
        }


        this.displayImages();

        this.uiManager.updateActionButtons(this.postData.completed);

        const isAdmin = this.checkAdminStatus();
        const currentUsername = this.currentUser?.username;  


        this.uiManager.updateActionButtons(
            this.postData,
            isAdmin,
            currentUsername,
            this.postData.writer,
            () => this.toggleCompleted(),
            () => this.deletePost()
        );
    }

    displayImages() {
        if (!this.imageContainer) {
            return;
        }


        this.imageContainer.innerHTML = '';

        if (!this.postData?.images?.length) {
            console.log('표시할 이미지가 없습니다');
            return;
        }

        const imageGrid = document.createElement('div');
        imageGrid.className = 'image-grid';

        this.postData.images.forEach((image) => {

            const imageWrapper = document.createElement('div');
            imageWrapper.className = 'image-wrapper';

            const img = document.createElement('img');
            img.src = `/qna/api/images/${image.ino}`;
            img.alt = '게시글 이미지';
            img.className = 'post-image';

            img.onerror = (e) => console.error(`이미지 로드 실패: ${image.ino}`, e);

            imageWrapper.appendChild(img);
            imageGrid.appendChild(imageWrapper);
        });

        this.imageContainer.appendChild(imageGrid);
    }


    toggleCompleted() {
        console.log('toggleCompleted 함수 호출');
        const previousState = this.postData.completed;
        const newState = !previousState;

        this.postData.completed = newState;
        this.updatePostUI();
        
        const url = `/qna/api/${this.qno}/completed`;
        console.log('요청 URL:', url);
        
        this.httpClient.patch(url, { completed: newState })
            .then(data => {
                console.log('서버 응답 데이터:', data);
                if (data && typeof data.completed !== 'undefined') {
                    this.postData.completed = data.completed;
                    this.updatePostUI();
                    
                    if (this.replyManager) {
                        this.replyManager.handleStatusChange(data.completed);
                    }
                } else {
                    throw new Error('서버 응답 데이터 오류');
                }
            })
            .catch(error => {
                console.error('상태 업데이트 중 오류 발생:', error);
                // 오류 발생 시 이전 상태로 되돌림
                this.postData.completed = previousState;
                this.updatePostUI();
                this.uiManager.showError('상태 변경에 실패했습니다.');
            });
    }

    async deletePost() {
        try {
            console.log('Attempting to delete post:', this.qno);
            await this.httpClient.delete(`/qna/api/${this.qno}`);
            window.location.href = '../list';
        } catch (error) {
            console.error('Error deleting post:', error);
            this.uiManager.showError('게시글 삭제 중 오류가 발생했습니다.');
        }
    }
}
