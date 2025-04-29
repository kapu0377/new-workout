document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.faq-close').forEach(function (el) {
        el.addEventListener('click', closeFaqModal);
    });

    window.addEventListener('click', function (e) {
        if (e.target.classList.contains('faq-modal')) {
            closeFaqModal();
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeFaqModal();
        }
    });
});

async function loadFaqs() {
    try {
        const response = await fetch('/api/faqs');
        if (!response.ok) {
            throw new Error('FAQ 데이터를 불러오는데 실패했습니다.');
        }
        const faqs = await response.json();
        const faqList = document.getElementById('faqList');
        if (!faqs || faqs.length === 0) {
            faqList.innerHTML = '<p class="text-center">등록된 FAQ가 없습니다.</p>';
            return;
        }
        const faqHtml = faqs.map(faq => `
            <div class="faq-item">
                <div class="faq-question">${escapeHtml(faq.question)}</div>
                <div class="faq-answer">${escapeHtml(faq.answer)}</div>
            </div>
        `).join('');
        faqList.innerHTML = faqHtml;
    } catch (error) {
        document.getElementById('faqList').innerHTML = '<p class="text-center">FAQ를 불러오는 중 오류가 발생했습니다.</p>';
    }
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function openFaqModal() {
    const modal = document.getElementById('faqModal');
    if (modal) {
        modal.style.display = 'block';
        modal.style.opacity = 1;
    }
    document.body.style.overflow = 'hidden';
    loadFaqs();
}

function closeFaqModal() {
    const modal = document.getElementById('faqModal');
    if (modal) {
        modal.style.opacity = 0;
        modal.style.display = 'none';
    }
    document.body.style.overflow = 'auto';
}
