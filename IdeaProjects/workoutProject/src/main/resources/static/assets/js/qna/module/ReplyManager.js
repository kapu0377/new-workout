export default class ReplyManager {
    constructor(qno, httpClient, uiManager) {
        this.qno = qno;
        this.httpClient = httpClient;
        this.uiManager = uiManager;
        this.lastRno = null;
        this.hasNextPage = false;
        this.isLoading = false;
        this.isRepliesVisible = false;
        this.currentUser = null;
        this.isCompleted = false;
        this.initializeElements();
        this.init();
    }

    initializeElements() {
        this.replyContainer = document.getElementById('replyContainer');
        this.replyList = document.getElementById('replyList');
        this.loadMoreButton = document.getElementById('loadMoreButton');
        this.viewRepliesButton = document.getElementById('viewRepliesButton');
        this.replyForm = document.getElementById('replyFormSection');
        this.completedMessageContainer = document.getElementById('completedMessageContainer');
        this.completeButton = document.getElementById('completeButton');
    }
    async init() {
        try {

            const postData = await this.loadPostStatus();
            await this.loadCurrentUser();


            if (this.isCompleted) {
                this.showCompletedMessage();
                this.hideReplyForm();
                await this.loadReplies();
                this.replyContainer.style.display = 'block';
                this.hideViewRepliesButton();
            } else {
                this.setupEventListeners();
            }
        } catch (error) {
            this.uiManager.showError('초기화 중 오류가 발생했습니다.');
        }
    }

    handleStatusChange(isCompleted) {
        this.isCompleted = isCompleted;

        if (this.isCompleted) {
            this.showCompletedMessage();
            this.hideReplyForm();
            this.replyContainer.style.display = "block";
            this.loadReplies(false, true);
            this.hideViewRepliesButton();
            this.isRepliesVisible = true;
        } else {
            this.hideCompletedMessage();
            this.showReplyForm();

            // 답변보기 버튼과 댓글 컨테이너 상태 초기화
            if (this.viewRepliesButton) {
                this.viewRepliesButton.style.display = "block";
                this.viewRepliesButton.textContent = "답변 보기";

                // 이벤트 리스너 재설정
                const newButton = this.viewRepliesButton.cloneNode(true);
                this.viewRepliesButton.parentNode.replaceChild(newButton, this.viewRepliesButton);
                this.viewRepliesButton = newButton;

                this.viewRepliesButton.addEventListener('click', () => {
                    if (!this.isRepliesVisible) {
                        this.replyContainer.style.display = "block";
                        if (!this.lastRno) {
                            this.loadReplies();
                        }
                        this.viewRepliesButton.textContent = "답변 숨기기";
                    } else {
                        this.replyContainer.style.display = "none";
                        this.viewRepliesButton.textContent = "답변 보기";
                    }
                    this.isRepliesVisible = !this.isRepliesVisible;
                });
            }

            this.replyContainer.style.display = "none";
            this.isRepliesVisible = false;  // 상태 동기화

            // 상태 변경 후 댓글 목록 갱신
            this.loadReplies(false, true);
        }
    }

    // 완료 처리 메시지를 숨깁니다
    hideCompletedMessage() {
        const completedMessageContainer = document.getElementById("completedMessageContainer");
        if (completedMessageContainer) {
            completedMessageContainer.style.display = "none";
        }
    }
    // 답변 폼을 보여줍니다
    showReplyForm() {
        if (this.replyForm) {
            this.replyForm.style.display = "block";
        }
    }

    setupEventListeners() {
        if (this.viewRepliesButton) {
            this.viewRepliesButton.addEventListener('click', () => {
                if (!this.isRepliesVisible) {
                    if (!this.lastRno) {
                        this.loadReplies();
                    }
                    this.toggleReplies(true);
                } else {
                    this.toggleReplies(false);
                }
                this.isRepliesVisible = !this.isRepliesVisible;
            });
        }

        if (this.loadMoreButton) {
            this.loadMoreButton.addEventListener('click', () => {
                this.loadReplies(true);
            });
        }

        if (this.completeButton) {
            this.completeButton.addEventListener('click', async () => {
                await this.toggleCompleted(); // 변경된 toggleCompleted 호출
            });
        }
    }
    // 완료 처리 상태를 토글합니다
    async toggleCompleted() {
        try {
            const response = await this.httpClient.patch(
                `/qna/api/${this.qno}/completed?completed=${!this.isCompleted}`
            );

            if (response && typeof response.completed !== 'undefined') {
                this.isCompleted = response.completed;

                this.handleStatusChange(this.isCompleted);

                this.uiManager.showSuccess(
                    this.isCompleted ? "완료 처리되었습니다." : "진행중으로 변경되었습니다."
                );
            } else {
                throw new Error('유효하지 않은 서버 응답');
            }
        } catch (error) {
            this.uiManager.showError("상태 업데이트에 실패했습니다.");
        }
    }
    // 버튼 초기화를 위한 새로운 메서드
    resetViewRepliesButton() {
        if (this.viewRepliesButton) {
            this.viewRepliesButton.replaceWith(this.viewRepliesButton.cloneNode(true));

            this.viewRepliesButton = document.getElementById('viewRepliesButton');

            this.viewRepliesButton.style.display = "block";
            const toggleText = this.viewRepliesButton.querySelector('.toggle-text');
            if (toggleText) {
                toggleText.textContent = "보기";
            }

            this.viewRepliesButton.addEventListener('click', () => {
                if (!this.isRepliesVisible) {
                    if (!this.lastRno) {
                        this.loadReplies();
                    }
                    this.toggleReplies(true);
                } else {
                    this.toggleReplies(false);
                }
                this.isRepliesVisible = !this.isRepliesVisible;
            });
        }
    }

    // 답변 보기/숨기기 토글
    toggleReplies(show) {
        if (this.replyContainer) {
            this.replyContainer.style.display = show ? 'block' : 'none';
            if (this.viewRepliesButton) {
                const toggleText = this.viewRepliesButton.querySelector('.toggle-text');
                if (toggleText) {
                    toggleText.textContent = show ? '숨기기' : '보기';
                }
            }
        }
    }

    // 게시글 상태를 불러옵니다
    async loadPostStatus() {
        const postData = await this.httpClient.get(`/qna/api/${this.qno}`);
        this.isCompleted = postData.completed;
        return postData;
    }

    // 완료 처리 메시지를 화면에 표시합니다
    showCompletedMessage() {
        if (this.completedMessageContainer) {
            this.completedMessageContainer.innerHTML = `
            <div class="completed-message">
                완료 처리된 질문입니다.
            </div>
        `;
            this.completedMessageContainer.style.display = 'block';
        }
    }

    hideReplyForm() {
        if (this.replyForm) {
            this.replyForm.style.display = 'none';
        }
    }

    hideViewRepliesButton() {
        if (this.viewRepliesButton) {
            this.viewRepliesButton.style.display = 'none';
        }
    }


    async loadCurrentUser() {
        try {
            this.currentUser = await this.httpClient.get('/qna/api/user/current');
            return this.currentUser;
        } catch (error) {
            console.error('Failed to load current user:', error);
            return null;
        }
    }


    // 댓글을 화면에 표시합니다
    displayReplies(replies, append = false) {
        if (!Array.isArray(replies) || replies.length === 0) {
            if (!append) {
                this.replyList.innerHTML = '<p style="text-align: center; color: #6c757d;">등록된 답변이 없습니다.</p>';
            }
            this.loadMoreButton.style.display = 'none';
            return;
        }

        const replyHTML = replies.map(reply => `
            <div class="reply-item">
                <div class="reply-header">
                    <div class="reply-writer-container">
                        <span class="reply-writer">${reply.writer}</span>
                        ${reply.admin ? '<span class="admin-badge">관리자</span>' : ''}
                    </div>
                    <span class="reply-date">${this.uiManager.formatDate(reply.replyDate)}</span>
                </div>
                <div class="reply-content">${reply.replyText}</div>
            </div>
        `).join('');

        if (append) {
            this.replyList.innerHTML += replyHTML;
        } else {
            this.replyList.innerHTML = replyHTML;
        }

        this.loadMoreButton.style.display = this.hasNextPage ? 'inline-block' : 'none';
    }

    // 댓글 목록
    async loadReplies(append = false, resetLastRno = false) {
        if (this.isLoading) return;

        try {
            this.isLoading = true;
            this.loadMoreButton?.classList.add('loading');

            if (resetLastRno) {
                this.lastRno = null;
            }

            let url = `/qna/api/${this.qno}/replies`;
            if (append && this.lastRno) {
                url += `?lastRno=${this.lastRno}`;
            }

            const data = await this.httpClient.get(url);
            this.hasNextPage = data.hasNext;

            if (data.replies && data.replies.length > 0) {
                this.lastRno = data.replies[data.replies.length - 1].rno;
                this.displayReplies(data.replies, append);
            } else {
                this.displayReplies([], append);
            }
        } catch (error) {
            this.uiManager.showError('답변을 불러올 수 없습니다.');
        } finally {
            this.isLoading = false;
            this.loadMoreButton?.classList.remove('loading');
        }
    }
    // 새 댓글을 등록합니다
    async submitReply(replyText) {
        if (!replyText.trim()) {
            this.uiManager.showError('답변 내용을 입력해주세요.');
            return;
        }

        if (!this.currentUser) {
            this.currentUser = await this.loadCurrentUser();
        }

        if (!this.currentUser) {
            this.uiManager.showError('로그인이 필요합니다.');
            return;
        }

        try {
            await this.httpClient.post(`/qna/api/${this.qno}/reply`, {
                replyText,
                qno: this.qno,
                writer: this.currentUser.username
            });

            document.getElementById('replyText').value = '';
            this.uiManager.showSuccess('답변이 등록되었습니다.');
            await this.loadReplies(false, true);  // 댓글 목록 새로고침
        } catch (error) {
            this.uiManager.showError('답변 등록 중 오류가 발생했습니다.');
        }
    }
}