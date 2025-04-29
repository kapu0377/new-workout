글로벌 프로젝트 규칙 (global_rules.md)

1. 언어 및 커뮤니케이션
- 모든 사용자/시스템 메시지, 코드 주석, 문서화 등은 반드시 한국어로 작성합니다.
- 사용자와의 모든 대화, 안내, 오류 메시지도 한국어로 제공합니다.

2. 경로 및 URL 정책
- 동영상 관련 URL은 `/video` 하위 경로로 통일합니다.
  - 예시: `/video-rooms/list`, `/video-rooms/room/{roomId}` 등 영상/화상채팅, 영상방 관리는 `/video-rooms` 또는 `/video`로 시작하는 경로로만 처리합니다.
  - 컨트롤러: `org.zerock.workoutproject.video.controller.VideoController`
- 게시판 관련 URL은 `/board` 하위 경로로 통일합니다.
  - 예시: `/board/list`, `/board/read`, `/board/add`, `/board/api/*` 등 자유게시판, 댓글, 첨부파일, 조회수 등 모든 게시판 기능은 `/board`로 시작합니다.
  - 컨트롤러: `org.zerock.workoutproject.board.controller.*`
- QnA 관련 URL은 `/qna` 하위 경로로 통일합니다.
  - 예시: `/qna/list`, `/qna/view/{qno}`, `/qna/register` 등 QnA, FAQ 기능은 `/qna`로 시작합니다.
  - 컨트롤러: `org.zerock.workoutproject.qna.controller.*`
- 기타 게시판(운동 등)은 `/exercise` 등으로 분리하되, 각 도메인별 URL prefix를 반드시 사용합니다.
- URL 네이밍은 소문자, 하이픈(-) 없이 언더스코어(_) 없이 작성합니다.
- REST API는 `/api` 하위로 분리해 사용하며, 예시: `/board/api/view-counts`.
- API 호출 시 반드시 상대 경로를 사용합니다(예: `/api/users`). HTTPS 혼합 콘텐츠(Mixed Content) 문제를 방지하기 위해 `http://` 또는 `https://`로 시작하는 절대 URL을 직접 사용하지 않습니다.

3. 코드 품질 및 협업
- 코드 작성 시 가독성, 유지보수성을 최우선으로 고려합니다.
- 불필요한 중복 코드를 지양하며, 가능한 한 공통 모듈을 활용합니다.
- 커밋 메시지, PR, 코드 리뷰 등 협업 과정에서도 한국어를 사용합니다.

4. 보안 및 개인정보
- 사용자 정보 및 개인정보는 반드시 암호화/마스킹 처리 후 저장 및 전송합니다.
- 외부 API Key 등 민감정보는 코드에 하드코딩하지 않습니다.

5. 기타
- 새로운 기능, 정책, 예외 사항은 본 파일에 반드시 추가/갱신합니다.
- 본 파일은 프로젝트 내 모든 구성원이 항상 참고해야 합니다.

6. CSS 및 스타일링
- 모든 CSS 파일은 `src/main/resources/static/assets/css` 디렉토리에 위치시킵니다.
- 공통 스타일(레이아웃, 변수, 유틸리티 등)은 `base.css`, `layout.css`, `variables.css`, `utilities.css`, `components.css` 등에 정의하며, 각 페이지별/기능별 CSS는 별도 파일로 분리합니다.
- 새로운 페이지/컴포넌트 추가 시, 기존 공통 CSS를 최대한 재사용하고, 중복 스타일 작성을 지양합니다.
- CSS 클래스명은 의미를 명확히 하며, 한글 대신 영문(소문자, 하이픈(-) 표기법)으로 작성합니다.
- 불필요한 CSS 파일이나 미사용 스타일은 정기적으로 정리(cleanup-guide.md 참고).
- CSS 변경 시, 반드시 크로스브라우징(최신 Chrome/Edge/Firefox 기준) 테스트를 진행합니다.

7. 인증 및 로그아웃 정책
- 로그아웃 엔드포인트는 반드시 `/logout`(POST 방식 기본, GET 방식은 보안상 권장하지 않음)으로 통일합니다.
- CSRF(Cross-Site Request Forgery) 방어가 활성화된 경우, 반드시 POST 방식 로그아웃만 허용해야 하며, GET 로그아웃은 보안상 매우 위험하므로 사용하지 않습니다.
- Spring Security 등 프레임워크 기반 로그아웃 시, 세션 무효화 및 쿠키 삭제를 반드시 수행합니다.
- 로그아웃 성공 시, 메인 페이지(`/`)로 리다이렉트합니다.
- 로그아웃 관련 보안 설정은 `CustomSecurityConfig` 등 보안 설정 파일에 명시합니다.

8. 자바스크립트(JS) 모듈화 및 파일 구조 원칙
- 모든 자바스크립트 코드는 역할별로 모듈화하여 작성한다.
- 각 페이지별 엔트리 파일(예: list.js, view.js, regist.js 등)에서는 필요한 기능별 모듈만 import해서 사용한다.
- 공통 기능(HTTP 통신, UI 관리 등)은 module 디렉토리에 별도 파일로 분리한다.
- ES6 모듈(ESM) 방식(import/export)을 표준으로 사용한다.
- 새로운 JS 파일 작성 시 반드시 모듈 구조를 지키며, 기능별/역할별 책임을 분리한다.
- 글로벌 네이밍 컨벤션 및 폴더 구조를 준수한다.
- HTML에서 JavaScript 파일을 불러올 때는 반드시 `type="module"` 속성을 사용한다.
- 모듈 간 의존성은 명시적으로 import/export 문을 사용하여 관리한다.
- 비동기 작업은 async/await를 사용하여 처리한다.
- 함수와 변수명은 camelCase를 사용한다.
- 상수는 UPPER_SNAKE_CASE를 사용한다.
- 클래스명은 PascalCase를 사용한다.
- 모든 비동기 작업은 try-catch 구문으로 감싸서 에러를 처리한다.
- 사용자에게 보여줄 에러 메시지는 한글로 작성한다.
- API 통신은 axios를 사용한다.
- API 엔드포인트는 별도의 상수 파일에서 관리한다.
- API 응답 형식은 일관성 있게 유지한다.

- (예시 폴더 구조)
  /assets/js/qna/module/HttpClient.js
  /assets/js/qna/module/UIManager.js
  /assets/js/qna/list.js
  /assets/js/member.js

이 원칙은 코드 품질, 유지보수성, 확장성, 협업 효율을 보장하기 위한 것이다.

9. HTTPS 설정 및 리디렉션 관리
- HTTPS 구성 시, HTTP에서 HTTPS로의 리디렉션은 서버레벨에서 수행해야 합니다.
- Strict-Transport-Security 헤더 설정을 권장합니다 (HSTS).
- 쿠키에는 반드시 Secure, HttpOnly 플래그를 사용합니다.
- 애플리케이션 내 리디렉션 로직에서는 절대 경로(full URL)보다 상대 경로를 우선적으로 사용합니다.
- 로그인 성공 후 리디렉션 URL에 절대 경로가 포함된 경우 보안 검증을 수행해야 합니다.
- 무한 리디렉션 방지를 위해 리디렉션 카운터나 최대 리디렉션 횟수를 설정합니다.
- 인증/권한 관련 필터에서는 리디렉션 전에 URL의 유효성을 반드시 검증합니다.
