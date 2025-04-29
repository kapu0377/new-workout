document.addEventListener('DOMContentLoaded', function () {
    const pwCheckBtn = document.getElementById('pwCheckBtn');
    const submitBtn = document.getElementById('submitBtn');
    const pwMatchMessage = document.getElementById('pwMatchMessage');
    let passwordVerified = false;
    let correctColor = "#0000FF";
    let worngColor = "#FF0000";
    
    pwCheckBtn.addEventListener('click', function () {
        const password = document.getElementById('mpw').value;
        const passwordCheck = document.getElementById('mpwCheck').value;

        if (password === '' || passwordCheck === '') {
            alert('비밀번호를 입력해주세요.');
            return;
        }

        if (password === passwordCheck) {
            passwordVerified = true;
            pwMatchMessage.style.display = 'block';
            pwMatchMessage.textContent = '비밀번호가 일치합니다.';
            pwMatchMessage.style.color = correctColor;
            submitBtn.disabled = false;

            document.getElementById('mpw').readOnly = true;
            document.getElementById('mpwCheck').readOnly = true;
            pwCheckBtn.disabled = true;
        } else {
            passwordVerified = false;
            pwMatchMessage.style.display = 'block';
            pwMatchMessage.textContent = '비밀번호가 일치하지 않습니다.';
            pwMatchMessage.style.color = worngColor;
            submitBtn.disabled = true;
        }
    });

    document.getElementById('mpw').addEventListener('input', function () {
        if (passwordVerified) {
            passwordVerified = false;
            submitBtn.disabled = true;
            pwMatchMessage.style.display = 'none';
            
            document.getElementById('mpw').readOnly = false;
            document.getElementById('mpwCheck').readOnly = false;
            pwCheckBtn.disabled = false;
        }
    });

    document.getElementById('mpwCheck').addEventListener('input', function () {
        if (passwordVerified) {
            passwordVerified = false;
            submitBtn.disabled = true;
            pwMatchMessage.style.display = 'none';
            
            // 비밀번호 입력 필드 잠금 해제
            document.getElementById('mpw').readOnly = false;
            document.getElementById('mpwCheck').readOnly = false;
            pwCheckBtn.disabled = false;
        }
    });

    // 폼 제출 전 최종 확인
    document.getElementById('submitBtn').addEventListener('click', function (e) {
        if (!passwordVerified) {
            e.preventDefault();
            alert('비밀번호 확인이 필요합니다.');
            return false;
        }
    });
    
    // 탈퇴 전 확인 절차 추가
    document.querySelector(".deleteBtn").addEventListener("click", function (e) {
        if (!confirm("정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.")) {
            e.preventDefault();
            return;
        }
        let form = document.getElementById("accesspanel");
        form.action = "/member/remove";
        form.submit();
    });

    // 프로필 이미지 업로드 기능
    const profileImageInput = document.getElementById('profileImageInput');
    if (profileImageInput) {
        profileImageInput.addEventListener('change', async function(e) {
            const file = e.target.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append('profileImage', file);

            try {
                const csrfToken = document.querySelector('input[name="_csrf"]').value;
                
                const response = await fetch('/member/profile/upload', {
                    method: 'POST',
                    body: formData,
                    headers: {
                        'X-CSRF-TOKEN': csrfToken
                    }
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    alert(errorData.error || '이미지 업로드에 실패했습니다.');
                    console.error('업로드 실패:', errorData);
                    return;
                }

                const result = await response.json();
                console.log('업로드 성공:', result);
                window.location.reload();

            } catch (error) {
                console.error('프로필 이미지 업로드 중 오류 발생:', error);
                alert('이미지 업로드에 실패했습니다.');
            }
        });
    }
}); 