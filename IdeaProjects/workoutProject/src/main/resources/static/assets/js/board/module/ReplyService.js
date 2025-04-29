// CSRF 토큰 설정
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || '';
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || '';

if (csrfToken && csrfHeader) {
  axios.defaults.headers.common[csrfHeader] = csrfToken;
}
axios.defaults.headers.post['Content-Type'] = 'application/json';

// 댓글 목록 조회
export async function getReplies(bno, page = 1, size = 10) {
  try {
    const response = await axios.get(`/board/api/replies/${bno}`, { params: { page, size } });
    return response.data;
  } catch (error) {
    console.error('댓글 목록 조회 중 오류 발생:', error);
    return [];
  }
}

// 댓글 등록
export async function postReply(replyObj) {
  try {
    const response = await axios.post(`/board/api/replies/${replyObj.bno}`, replyObj);
    return response.data;
  } catch (error) {
    console.error('댓글 등록 중 오류 발생:', error);
    throw error;
  }
}

// 댓글 수정
export async function putReply(rno, replyText) {
  try {
    const response = await axios.put(`/board/api/replies/${rno}`, { replyText });
    return response.data;
  } catch (error) {
    console.error('댓글 수정 중 오류 발생:', error);
    throw error;
  }
}

// 댓글 삭제
export async function deleteReply(rno) {
  try {
    const response = await axios.delete(`/board/api/replies/${rno}`);
    return response.data;
  } catch (error) {
    console.error('댓글 삭제 중 오류 발생:', error);
    throw error;
  }
} 