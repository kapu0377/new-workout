import { youtubeUrlToThumbnail, extractYoutubeVideoId } from './module/youtubeThumbnail.js';

/**
 * 페이지 내의 모든 YouTube iframe을 썸네일 이미지로 대체합니다.
 * 이미지 클릭 시 해당 유튜브 영상으로 이동합니다.
 */
document.addEventListener('DOMContentLoaded', function() {
    // 메인 페이지 비디오 썸네일 처리
    const videoThumbnails = document.querySelectorAll('.video-thumbnail');
    videoThumbnails.forEach(thumbnailContainer => {
        const iframe = thumbnailContainer.querySelector('iframe');
        if (!iframe) return;
        
        const videoUrl = iframe.getAttribute('src');
        if (!videoUrl || !videoUrl.includes('youtube')) return;
        
        const videoId = extractYoutubeVideoId(videoUrl);
        if (!videoId) return;
        
        // 원본 URL 저장 (나중에 클릭 이벤트에서 사용)
        const originalUrl = videoUrl.startsWith('http') ? videoUrl : 
                          videoUrl.startsWith('//') ? 'https:' + videoUrl :
                          'https://www.youtube.com/watch?v=' + videoId;
        
        // 썸네일 이미지 생성
        const img = document.createElement('img');
        img.src = youtubeUrlToThumbnail(videoUrl, 'hqdefault');
        img.alt = '유튜브 영상 썸네일';
        img.classList.add('youtube-thumbnail');
        
        // 재생 버튼 오버레이 추가
        const playButton = document.createElement('div');
        playButton.classList.add('play-button-overlay');
        
        // 클릭 이벤트 추가
        thumbnailContainer.addEventListener('click', function() {
            window.open(originalUrl, '_blank');
        });
        
        // 기존 iframe 제거 및 썸네일 추가
        thumbnailContainer.innerHTML = '';
        thumbnailContainer.appendChild(img);
        thumbnailContainer.appendChild(playButton);
        thumbnailContainer.classList.add('thumbnail-loaded');
    });
}); 