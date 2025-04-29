export default class RegisterManager {
    constructor(httpClient, uiManager) {
        this.httpClient = httpClient;
        this.uiManager = uiManager;
        this.currentUser = null;
        this.selectedFiles = new Map();

        this.initializeElements();
        this.loadCurrentUser();
    }

    initializeElements() {
        this.dropZone = document.getElementById('dropZone');
        this.fileInput = document.getElementById('imageFiles');
        this.titleInput = document.getElementById('title');
        this.contentInput = document.querySelector('textarea#content');
        this.writerInput = document.getElementById('writer');
        this.imagePreview = document.getElementById('imagePreview');
        
        if (!this.dropZone || !this.fileInput || !this.titleInput || 
            !this.contentInput || !this.imagePreview) {
            console.error('필요한 DOM 요소를 찾을 수 없습니다:', {
                dropZone: !!this.dropZone,
                fileInput: !!this.fileInput,
                titleInput: !!this.titleInput,
                contentInput: !!this.contentInput,
                imagePreview: !!this.imagePreview
            });
            
            // DOM 요소가 없는 경우 DOMContentLoaded 이벤트 이후에 다시 시도
            document.addEventListener('DOMContentLoaded', () => {
                this.dropZone = document.getElementById('dropZone');
                this.fileInput = document.getElementById('imageFiles');
                this.titleInput = document.getElementById('title');
                this.contentInput = document.querySelector('textarea#content');
                this.writerInput = document.getElementById('writer');
                this.imagePreview = document.getElementById('imagePreview');
                
                console.log('DOMContentLoaded 이후 DOM 요소 재시도:', {
                    dropZone: !!this.dropZone,
                    fileInput: !!this.fileInput,
                    titleInput: !!this.titleInput,
                    contentInput: !!this.contentInput,
                    imagePreview: !!this.imagePreview
                });
            });
        } else {
            console.log('모든 DOM 요소를 찾았습니다:', {
                dropZone: this.dropZone,
                fileInput: this.fileInput,
                titleInput: this.titleInput,
                contentInput: this.contentInput,
                imagePreview: this.imagePreview
            });
        }
    }

    triggerFileInput() {
        if (this.fileInput) {
            this.fileInput.click();
        } else {
            console.error('fileInput 요소를 찾을 수 없습니다.');
        }
    }

    addNewFiles(files) {
        console.log('addNewFiles 호출됨:', files);
        if (!files || files.length === 0) {
            console.warn('선택된 파일이 없습니다.');
            return;
        }

        files.forEach(file => {
            if (file.type.startsWith('image/')) {
                const fileId = Date.now() + '-' + Math.random().toString(36).substr(2, 9);
                console.log('이미지 파일 추가:', file.name, 'ID:', fileId);
                this.selectedFiles.set(fileId, file);
                this.displayImagePreview(file, fileId);
            } else {
                console.warn('이미지 파일이 아닙니다:', file.name, file.type);
            }
        });
    }

    displayImagePreview(file, fileId) {
        if (!this.imagePreview) {
            console.error('imagePreview 요소를 찾을 수 없습니다.');
            return;
        }
        
        const reader = new FileReader();
        reader.onload = (e) => {
            const previewItem = document.createElement('div');
            previewItem.className = 'uploaded-image';
            previewItem.setAttribute('data-file-id', fileId);

            previewItem.innerHTML = `
                <div class="image-wrapper">
                    <img src="${e.target.result}" alt="Preview" class="preview-image">
                    <button type="button" class="remove-image-btn">&times;</button>
                </div>
                <p class="file-name">${file.name}</p>
            `;

            const removeBtn = previewItem.querySelector('.remove-image-btn');
            if (removeBtn) {
                removeBtn.addEventListener('click', () => {
                    this.removeImage(fileId);
                });
            }

            this.imagePreview.appendChild(previewItem);
        };
        reader.readAsDataURL(file);
    }

    removeImage(fileId) {
        console.log('이미지 삭제:', fileId);
        this.selectedFiles.delete(fileId);
        const previewItem = document.querySelector(`[data-file-id="${fileId}"]`);
        if (previewItem) {
            previewItem.remove();
        }
    }

    async loadCurrentUser() {
        try {
            this.currentUser = await this.httpClient.get('/qna/api/user/current');
            if (this.currentUser) {
                console.log('사용자 정보 로드됨:', this.currentUser);
                if (this.writerInput) {
                    this.writerInput.value = this.currentUser.username;
                }
                
                const userInfoElement = document.getElementById('userInfo');
                if (userInfoElement) {
                    userInfoElement.textContent = `사용자: ${this.currentUser.username}`;
                }
            } else {
                console.warn('사용자 정보가 없습니다.');
                const userInfoElement = document.getElementById('userInfo');
                if (userInfoElement) {
                    userInfoElement.textContent = "로그인 필요";
                }
            }
        } catch (error) {
            console.error('사용자 정보 로드 오류:', error);
            this.uiManager.showError('사용자 정보를 불러오는데 실패했습니다.');
        }
    }

    async submitQna() {
        console.log('submitQna 호출됨');
        
        // DOM 요소를 실행 시점에 다시 한번 확인
        if (!this.titleInput) {
            this.titleInput = document.getElementById('title');
        }
        if (!this.contentInput) {
            this.contentInput = document.querySelector('textarea#content');
        }
        
        if (!this.titleInput || !this.contentInput) {
            console.error('필수 입력 필드를 찾을 수 없습니다. 다른 방법으로 시도합니다.');
            
            // 직접 셀렉터로 다시 시도
            this.titleInput = document.querySelector('#title') || document.querySelector('input[name="title"]');
            this.contentInput = document.querySelector('#content') || document.querySelector('textarea[name="content"]');
            
            if (!this.titleInput || !this.contentInput) {
                this.uiManager.showError('필수 입력 필드를 찾을 수 없습니다. 페이지를 새로고침하고 다시 시도해 주세요.');
                return;
            }
        }
        
        console.log('제목 요소:', this.titleInput);
        console.log('내용 요소:', this.contentInput);
        
        const rawTitle = this.titleInput.value;
        const rawContent = this.contentInput.value;
        console.log('원본 제목 값:', rawTitle, '길이:', rawTitle ? rawTitle.length : 0);
        console.log('원본 내용 값:', rawContent, '길이:', rawContent ? rawContent.length : 0);
        
        const title = rawTitle ? rawTitle.trim() : '';
        const content = rawContent ? rawContent.trim() : '';
        console.log('정리된 제목:', title, '길이:', title.length);
        console.log('정리된 내용:', content, '길이:', content.length);
        
        console.log('현재 사용자:', this.currentUser);

        if (!this.currentUser || !this.currentUser.username) {
            this.uiManager.showError('로그인한 사용자 정보를 확인할 수 없습니다.');
            return;
        }

        if (!title) {
            console.error('제목이 비어 있습니다');
            this.uiManager.showError('제목을 입력해주세요.');
            this.titleInput.focus();
            return;
        }

        if (!content) {
            console.error('내용이 비어 있습니다');
            this.uiManager.showError('내용을 입력해주세요.');
            this.contentInput.focus();
            return;
        }

        const formData = new FormData();
        formData.append("writer", this.currentUser.username);
        formData.append("title", title);
        
        formData.append("questionText", content);
        formData.append("content", content); 

        console.log('FormData 내용:');
        console.log('- writer:', this.currentUser.username);
        console.log('- title:', title);
        console.log('- questionText:', content);
        console.log('- content:', content);  

        console.log('선택된 파일 개수:', this.selectedFiles.size);
        
        if (this.selectedFiles.size > 0) {
            this.selectedFiles.forEach((file, fileId) => {
                console.log('파일 추가:', file.name, 'ID:', fileId);
                formData.append("imageFiles", file);
            });
        }

        try {
            console.log('API 호출 시작: /qna/api/register');
            console.log('FormData 전송 내용 확인:');
            for (let [key, value] of formData.entries()) {
                if (value instanceof File) {
                    console.log(`${key}: 파일(${value.name})`);
                } else {
                    console.log(`${key}: ${value}`);
                }
            }
            
            const result = await this.httpClient.post('/qna/api/register', formData, true);
            console.log('등록 성공:', result);
            
            this.uiManager.showSuccess('질문이 성공적으로 등록되었습니다!');
            
            setTimeout(() => {
                location.href = '/qna/list';
            }, 1500);
        } catch (error) {
            console.error('API 오류:', error);
            this.uiManager.showError(error.message || '등록 중 오류가 발생했습니다.');
        }
    }
}