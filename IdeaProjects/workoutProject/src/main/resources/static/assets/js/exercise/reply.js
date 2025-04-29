
document.addEventListener('DOMContentLoaded', function() {
    async function addReply(replyObj) {
        const response = await axios.post('/exercise/replies/', replyObj);
        return response.data;
    }

    async function getList({eno, page, size, goLast}) {
        const response = await axios.get(`/exercise/replies/read/${eno}`, {
            params: {page, size, goLast}
        });
        return response.data;
    }

    async function removeReply(rno) {
        const response = await axios.delete(`/exercise/replies/${rno}`);
        return response.data;
    }

    async function modifyReply(replyObj) {
        const response = await axios.put(`/exercise/replies/${replyObj.rno}`, replyObj);
        return response.data;
    }

    // DOM 요소 참조
    const replySection = document.getElementById('replySection');
    if (!replySection) return;

    const eno = replySection.dataset.eno;
    const currentUser = replySection.dataset.user;
    const isAdmin = replySection.dataset.admin === 'true';
    const replyList = document.querySelector('.replyList');
    const registerBtn = document.querySelector('.registerBtn');
    const replyText = document.querySelector('.replyText');

    // 날짜 포맷팅 함수
    function formatDate(dateStr) {
        const date = new Date(dateStr);
        const hours = date.getHours();
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const ampm = hours >= 12 ? '오후' : '오전';
        const formattedHours = String(hours % 12 || 12).padStart(2, '0');

        return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일 ${ampm} ${formattedHours}:${minutes}`;
    }

    // 댓글 목록 표시 함수
    function printList(dtoList) {
        let str = '';
        if (dtoList && dtoList.length > 0) {
            for (const dto of dtoList) {
                const formattedDate = formatDate(dto.regDate);
                const canModify = currentUser === dto.replyer;  // 작성자만 수정 가능
                const canDelete = currentUser === dto.replyer || isAdmin;  // 작성자나 관리자 삭제 가능

                str += `
            <div class="comment-item" data-rno="${dto.rno}">
                <div class="comment-header">
                    <div class="comment-info">
                        <span class="comment-author">${dto.replyer}</span>
                        <span class="comment-date">${formattedDate}</span>
                    </div>
                    <div class="comment-actions">
                        ${canModify ? `<button class="modifyBtn button text-button">수정</button>` : ''}
                        ${canDelete ? `<button class="removeBtn button text-button">삭제</button>` : ''}
                    </div>
                </div>
                <div class="comment-content">
                    <div class="comment-text">${dto.replyText}</div>
                    <div class="edit-area" style="display: none;">
                        <textarea class="reply-textarea">${dto.replyText}</textarea>
                        <div class="button-area">
                            <button class="button modify-button saveBtn">저장</button>
                            <button class="button list-button cancelBtn">취소</button>
                        </div>
                    </div>
                </div>
            </div>`;
            }
        } else {
            str = '<div class="no-comments">등록된 댓글이 없습니다.</div>';
        }
        replyList.innerHTML = str;
    }

    // 댓글 목록 로드 함수
    function printReplies(page, size, goLast) {
        getList({eno, page, size, goLast}).then(
            data => {
                printList(data.dtoList);
            }
        ).catch(e => {
            console.error('댓글 로드 실패:', e);
        });
    }

    registerBtn?.addEventListener("click", function() {
        if (replyText.value.trim() === '') {
            alert("댓글 내용을 입력해주세요");
            return;
        }

        addReply({
            eno: eno,
            replyText: replyText.value,
            replyer: currentUser
        }).then(result => {
            replyText.value = '';
            printReplies(1, 10, true);
        }).catch(e => {
            alert("댓글 등록에 실패했습니다");
            console.error(e);
        });
    });

    // 댓글 수정/삭제 이벤트
    replyList?.addEventListener("click", async function(e) {
        const replyItem = e.target.closest('.comment-item');
        if (!replyItem) return;

        const rno = replyItem.dataset.rno;
        const replyContent = replyItem.querySelector('.comment-content');
        const replyText = replyContent.querySelector('.comment-text');
        const editArea = replyContent.querySelector('.edit-area');
        const buttonArea = replyItem.querySelector('.comment-actions');

        // 수정 모드 전환
        if (e.target.classList.contains('modifyBtn')) {
            const textarea = editArea.querySelector('textarea');
            textarea.value = replyText.textContent.trim();
            textarea.focus();

            replyText.style.display = 'none';
            editArea.style.display = 'block';
            if (buttonArea) buttonArea.style.display = 'none';
        }

        // 수정 내용 저장
        if (e.target.classList.contains('saveBtn')) {
            const textarea = editArea.querySelector('textarea');
            const newText = textarea.value.trim();
            if (!newText) return;

            try {
                await modifyReply({
                    rno: rno,
                    replyText: newText
                });
                await printReplies(1, 10, false);
            } catch (error) {
                console.error('댓글 수정 실패:', error);
                alert('댓글 수정에 실패했습니다.');
            }
        }

        // 수정 취소
        if (e.target.classList.contains('cancelBtn')) {
            replyText.style.display = 'block';
            editArea.style.display = 'none';
            if (buttonArea) buttonArea.style.display = 'flex';
        }

        // 댓글 삭제
        if (e.target.classList.contains('removeBtn')) {
            if (!confirm('댓글을 삭제하시겠습니까?')) return;

            try {
                await removeReply(rno);
                alert('댓글이 삭제되었습니다.');
                await printReplies(1, 10, false);
            } catch (error) {
                console.error('댓글 삭제 실패:', error);
                alert('댓글 삭제에 실패했습니다.');
            }
        }
    });

    // 초기 댓글 목록 로드
    printReplies(1, 10, true);
});