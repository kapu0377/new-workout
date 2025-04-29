import { getReplies, postReply, putReply, deleteReply } from './module/ReplyService.js';

export async function getList({ bno, page = 1, size = 10, goLast = false }) {
  return await getReplies(bno, page, size);
}

// 댓글 등록
export async function addReply(replyObj) {
  const result = await postReply(replyObj);
  return result;
}

// 댓글 수정
export async function modifyReply({ rno, replyText }) {
  const result = await putReply(rno, replyText);
  return result;
}

// 댓글 삭제
export async function removeReply(rno) {
  const result = await deleteReply(rno);
  return result;
}





