let idCheck = false;
const contextPath = window.location.origin + '/';

window.addEventListener('DOMContentLoaded', () => {
  document.querySelector('.id-check-button').addEventListener('click', (e) => {
    e.preventDefault();
    const idInput = document.querySelector("input[name='mid']");
    const id = idInput.value.trim();
    if (!id) {
      showIdStatus('ID를 입력해주세요.', true);
      return;
    }
    checkId(id);
  });

  document.querySelector("input[name='mpw']").addEventListener('change', passwordCheck);
  document.getElementById('mpwCheck').addEventListener('change', passwordCheck);

  document.getElementById('domainToggleBtn').addEventListener('click', toggleDomainSelect);
  document.querySelectorAll('.domain-option').forEach(opt => opt.addEventListener('click', () => {
    const domain = opt.getAttribute('data-domain');
    selectDomain(domain);
  }));

  const registerForm = document.getElementById('registerForm');
  registerForm.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') e.preventDefault();
  });

  document.getElementById('joinBtn').addEventListener('click', handleSubmit);

  document.addEventListener('click', (e) => {
    const container = document.getElementById('domainSelectContainer');
    const toggleBtn = document.getElementById('domainToggleBtn');
    if (container.style.display !== 'none' && !container.contains(e.target) && e.target !== toggleBtn) {
      container.style.display = 'none';
      toggleBtn.textContent = '▼';
    }
  });

  initEmail();

  const idInput = document.querySelector("input[name='mid']");
  idInput.addEventListener('input', () => {
    idCheck = false;
    document.getElementById('idStatusMessage').style.display = 'none';
  });

  document.getElementById('emailId').addEventListener('input', combineEmail);
  document.getElementById('emailDomain').addEventListener('input', combineEmail);
});

function showIdStatus(message, isError) {
  const statusEl = document.getElementById('idStatusMessage');
  statusEl.textContent = message;
  statusEl.style.color = isError ? 'var(--error)' : 'var(--success)';
  statusEl.style.display = 'block';
}

async function checkId(id) {
  const btn = document.querySelector('.id-check-button');
  btn.disabled = true;
  btn.textContent = '확인 중...';
  try {
    const res = await axios.get(`${contextPath}member/check/${id}`);
    if (res.status === 200 && !res.data) {
      showIdStatus('사용가능한 ID 입니다.', false);
      idCheck = true;
    } else {
      showIdStatus('이미 존재하는 ID 입니다.', true);
      idCheck = false;
    }
  } catch (e) {
    showIdStatus('ID 확인 중 오류 발생', true);
    idCheck = false;
  } finally {
    btn.disabled = false;
    btn.textContent = '중복확인';
  }
}

function passwordCheck() {
  const p1 = document.querySelector("input[name='mpw']").value;
  const p2 = document.getElementById('mpwCheck').value;
  const msg = document.getElementById('pwMatchMessage');
  if (p1 !== p2) {
    msg.textContent = '비밀번호가 서로 일치하지 않습니다.';
    msg.style.color = 'var(--error)';
  } else {
    msg.textContent = '비밀번호가 서로 일치합니다.';
    msg.style.color = 'var(--success)';
  }
  msg.style.display = 'block';
}

function toggleDomainSelect() {
  const container = document.getElementById('domainSelectContainer');
  const btn = document.getElementById('domainToggleBtn');
  console.log('토글 버튼 클릭됨');
  if (container.style.display === 'none') {
    console.log('도메인 목록 표시');
    container.style.display = 'block';
    btn.textContent = '▲';
  } else {
    console.log('도메인 목록 숨김');
    container.style.display = 'none';
    btn.textContent = '▼';
  }
}

function selectDomain(domain) {
  console.log('도메인 선택됨:', domain);
  const emailDomain = document.getElementById('emailDomain');
  emailDomain.value = domain;
  emailDomain.readOnly = !!domain;
  combineEmail();
  toggleDomainSelect();
}

function combineEmail() {
  console.log('이메일 결합');
  const id = document.getElementById('emailId').value;
  const dom = document.getElementById('emailDomain').value;
  const combinedEmail = id && dom ? `${id}@${dom}` : '';
  console.log('결합된 이메일:', combinedEmail);
  document.getElementById('fullEmail').value = combinedEmail;
}

function initEmail() {
  const full = document.getElementById('fullEmail').value;
  if (full) {
    const [id, dom] = full.split('@');
    document.getElementById('emailId').value = id;
    document.getElementById('emailDomain').value = dom;
    document.getElementById('emailDomain').readOnly = true;
  }
}

// 회원가입 처리 로직 추가
function handleSubmit(e) {
  e.preventDefault();
  // 아이디 중복 확인 체크
  if (!idCheck) {
    showIdStatus('아이디 중복확인을 해주세요.', true);
    return;
  }
  // 비밀번호 일치 여부 확인
  const p1 = document.querySelector("input[name='mpw']").value;
  const p2 = document.getElementById('mpwCheck').value;
  if (p1 !== p2) {
    const msg = document.getElementById('pwMatchMessage');
    msg.textContent = '비밀번호가 서로 일치하지 않습니다.';
    msg.style.color = 'var(--error)';
    msg.style.display = 'block';
    return;
  }
  // 이메일 조합 및 폼 제출
  combineEmail();
  document.getElementById('registerForm').submit();
} 