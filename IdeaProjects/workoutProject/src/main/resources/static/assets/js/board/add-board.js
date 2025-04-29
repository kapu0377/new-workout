import axios from 'axios';

export function showUploadFile(data) {
    const uuid = data.uuid;
    const fileName = data.fileName;
    const link = data.link;
    const str = `
    <div class="uploaded-image">
      <div class="image-wrapper">
        <img src="/view/${link}" data-src="${uuid}_${fileName}" class="preview-image">
        <button type="button" onclick="removeFile(event, '${uuid}', '${fileName}', this)" class="remove-image-btn">×</button>
      </div>
      <p class="file-name">${fileName}</p>
    </div>`;
    document.querySelector(".uploadResult").insertAdjacentHTML('beforeend', str);
}

export function removeFile(event, uuid, fileName, button) {
    event.preventDefault();
    event.stopPropagation();
    if (confirm('이미지를 삭제하시겠습니까?')) {
        removeFileToServer(uuid, fileName)
            .then(() => button.closest('.uploaded-image').remove())
            .catch(err => {
                console.error('파일 삭제 중 오류:', err);
                alert('파일 삭제에 실패했습니다.');
            });
    }
}

export async function uploadToServer(formData) {
    const response = await axios.post('/board/api/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
}

export function initAddPage() {
    const dropZone = document.getElementById('drop-zone');
    const fileInput = document.getElementById('file-upload');
    ['dragenter','dragover','dragleave','drop'].forEach(evt => {
        dropZone.addEventListener(evt, e => {
            e.preventDefault(); e.stopPropagation();
        });
    });
    ['dragenter','dragover'].forEach(evt => {
        dropZone.addEventListener(evt, () => {
            dropZone.style.borderColor = '#4a90e2';
            dropZone.style.backgroundColor = '#f8fafc';
        });
    });
    ['dragleave','drop'].forEach(evt => {
        dropZone.addEventListener(evt, () => {
            dropZone.style.borderColor = '#e5e7eb';
            dropZone.style.backgroundColor = 'transparent';
        });
    });
    dropZone.addEventListener('drop', e => handleFiles(e.dataTransfer.files));
    fileInput.addEventListener('change', e => handleFiles(e.target.files));
}

function handleFiles(files) {
    const formData = new FormData();
    Array.from(files).forEach(file => {
        if (file.type.startsWith('image/')) {
            formData.append('files', file);
        }
    });
    if (formData.has('files')) {
        uploadToServer(formData)
            .then(results => results.forEach(showUploadFile))
            .catch(err => console.error('업로드 오류:', err));
    }
}

export function initFormValidation() {
    const submitBtn = document.querySelector('.submitBtn');
    const form = document.getElementById('addForm');
    if (!submitBtn || !form) return;
    submitBtn.addEventListener('click', e => {
        e.preventDefault(); e.stopPropagation();
        const title = form.querySelector('input[name="title"]').value.trim();
        const content = form.querySelector('textarea[name="content"]').value.trim();
        const writer = form.querySelector('input[name="writer"]').value.trim();
        if (!title) { alert('제목을 입력해주세요.'); return; }
        if (!content) { alert('내용을 입력해주세요.'); return; }
        if (!writer) { alert('로그인 후 이용해주세요.'); return; }
        const target = document.querySelector('.uploadHidden');
        if (target) {
            let str = '';
            document.querySelectorAll('.uploadResult img').forEach(img => {
                const ds = img.getAttribute('data-src');
                if (ds) str += `<input type='hidden' name='fileNames' value='${ds}'>`;
            });
            target.innerHTML = str;
        }
        form.submit();
    });
}

// 초기화 함수
export function initBoardAddPage() {
    initAddPage();
    initFormValidation();
}

window.removeFile = removeFile;
document.addEventListener('DOMContentLoaded', initBoardAddPage); 