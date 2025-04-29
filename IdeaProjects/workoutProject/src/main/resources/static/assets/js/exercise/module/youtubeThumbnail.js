/**
 * 유튜브 URL에서 비디오 ID를 추출하는 함수
 * @param {string} url - 유튜브 동영상 URL 또는 임베드 URL
 * @returns {string|null} - 유튜브 비디오 ID 또는 추출 실패 시 null
 */
export function extractYoutubeVideoId(url) {
    if (!url) return null;
    
    // 표준 YouTube URL 패턴 확인
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
    const match = url.match(regExp);
    
    // 임베드 URL에서 특수 처리
    if (url.includes('youtube.com/embed/')) {
        const embedMatch = url.match(/youtube\.com\/embed\/([^?&\/]+)/);
        if (embedMatch && embedMatch[1]) {
            return embedMatch[1];
        }
    }
    
    // 일반 URL 패턴 매칭
    return (match && match[2].length === 11) ? match[2] : null;
}

/**
 * 유튜브 비디오 ID로부터 썸네일 URL을 생성하는 함수
 * @param {string} videoId - 유튜브 비디오 ID
 * @param {string} quality - 썸네일 품질 (default, mqdefault, hqdefault, sddefault, maxresdefault)
 * @returns {string} - 유튜브 썸네일 URL
 */
export function getYoutubeThumbnailUrl(videoId, quality = 'hqdefault') {
    if (!videoId) return '';
    return `https://img.youtube.com/vi/${videoId}/${quality}.jpg`;
}

/**
 * 유튜브 URL을 썸네일 이미지 URL로 변환하는 함수
 * @param {string} url - 유튜브 URL
 * @param {string} quality - 썸네일 품질
 * @returns {string} - 썸네일 URL 또는 실패 시 빈 문자열
 */
export function youtubeUrlToThumbnail(url, quality = 'hqdefault') {
    const videoId = extractYoutubeVideoId(url);
    if (!videoId) return '';
    return getYoutubeThumbnailUrl(videoId, quality);
}

/**
 * 유튜브 URL로 이동하는 onclick 이벤트 핸들러 문자열을 생성하는 함수
 * @param {string} url - 유튜브 URL
 * @returns {string} - onclick 이벤트 핸들러 문자열
 */
export function createYoutubeClickHandler(url) {
    return `window.open('${url}', '_blank')`;
} 