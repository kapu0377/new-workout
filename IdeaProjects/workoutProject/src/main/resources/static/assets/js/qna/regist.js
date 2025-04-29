import HttpClient from './module/HttpClient.js';
import UIManager from './module/UIManager.js';
import RegisterManager from './module/RegisterManager.js';

console.log('QnA regist.js 모듈이 로드되었습니다.');

// 인스턴스 생성
const httpClient = new HttpClient();
const uiManager = new UIManager();
const registerManager = new RegisterManager(httpClient, uiManager);

// DOM이 로드된 후 초기화
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOMContentLoaded 이벤트 발생');
    
    if (registerManager) {
        registerManager.loadCurrentUser();
        
        // 이미지 업로드 핸들러 설정
        const dropZone = document.getElementById('dropZone');
        const fileInput = document.getElementById('imageFiles');
        
        if (fileInput) {
            fileInput.addEventListener('change', (event) => {
                const files = Array.from(event.target.files);
                if (files.length > 0) {
                    console.log(`${files.length}개의 파일이 선택됨`);
                    registerManager.addNewFiles(files);
                }
            });
        }
        
        if (dropZone) {
            ['dragover', 'dragleave', 'drop'].forEach(eventName => {
                dropZone.addEventListener(eventName, (e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    
                    if (eventName === 'dragover') {
                        dropZone.style.borderColor = '#4a90e2';
                        dropZone.style.backgroundColor = '#f8fafc';
                    } else if (eventName === 'dragleave') {
                        dropZone.style.borderColor = '#e5e7eb';
                        dropZone.style.backgroundColor = 'transparent';
                    } else if (eventName === 'drop') {
                        dropZone.style.borderColor = '#e5e7eb';
                        dropZone.style.backgroundColor = 'transparent';
                        
                        const files = Array.from(e.dataTransfer.files);
                        if (files.length > 0) {
                            console.log(`${files.length}개의 파일이 드롭됨`);
                            registerManager.addNewFiles(files);
                        }
                    }
                });
            });
        }
        
        const submitButton = document.getElementById('submitButton');
        
        if (submitButton) {
            submitButton.addEventListener('click', () => {
                console.log('등록 버튼 클릭됨');
                
                const titleElement = document.getElementById('title');
                const contentElement = document.getElementById('content');
                
                if (!titleElement || !contentElement) {
                    console.error('필수 입력 필드를 찾을 수 없습니다');
                    alert('페이지 로딩에 문제가 있습니다. 페이지를 새로고침해 주세요.');
                    return;
                }
                
                const title = titleElement.value.trim();
                const content = contentElement.value.trim();
                
                console.log('버튼 클릭 시 값 확인:', {
                    title: title,
                    content: content,
                    titleLength: title.length,
                    contentLength: content.length
                });
                
                if (!title) {
                    console.error('제목이 비어 있습니다');
                    alert('제목을 입력해주세요.');
                    titleElement.focus();
                    return;
                }
                
                if (!content) {
                    console.error('내용이 비어 있습니다');
                    alert('내용을 입력해주세요.');
                    contentElement.focus();
                    return;
                }
                
                // 유효성 검사를 통과하면 등록 진행
                registerManager.submitQna();
            });
        }
        
        const listButton = document.getElementById('listButton');
        if (listButton) {
            listButton.addEventListener('click', () => {
                console.log('목록 버튼 클릭됨');
                location.href = '/qna/list';
            });
        }
    } else {
        console.error('registerManager가 초기화되지 않았습니다.');
    }
});