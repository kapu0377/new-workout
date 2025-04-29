let stompClient = null;
let localStream = null;
let peerConnections = {};
let userId = Math.random().toString(36).substr(2, 9); 
let isConnecting = false;

function log(message) {
    console.log("[VideoCall] " + message);
    addDebugInfo(message);
}

function addDebugInfo(message) {
    const debugInfoElement = document.getElementById('debugInfo');
    if (debugInfoElement) {
        const entry = document.createElement('div');
        entry.textContent = new Date().toLocaleTimeString() + ': ' + message;
        debugInfoElement.appendChild(entry);
        
        debugInfoElement.scrollTop = debugInfoElement.scrollHeight;
        
        const maxEntries = 50;
        while (debugInfoElement.childElementCount > maxEntries) {
            debugInfoElement.removeChild(debugInfoElement.firstChild);
        }
    }
}

document.addEventListener('DOMContentLoaded', function() {
    log("DOM이 로드되었습니다. videoCall.js 초기화 중...");
    
    const startBtn = document.getElementById('startBtn');
    if (startBtn) {
        startBtn.addEventListener('click', startLocalVideo);
        log("시작 버튼 이벤트 등록됨");
    } else {
        console.error("[VideoCall] 시작 버튼을 찾을 수 없음");
    }
});

const peerConnectionConfig = {
    'iceServers': [
        { 'urls': 'stun:stun.l.google.com:19302' },
        { 'urls': 'stun:stun1.l.google.com:19302' }
    ]
};

async function startLocalVideo() {
    try {
        log("카메라 접근 요청 중...");
        localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        document.getElementById('localVideo').srcObject = localStream;
        log("카메라 접근 성공, 로컬 스트림 설정됨");
        
        Object.values(peerConnections).forEach(pc => {
            if (pc && localStream) {
                localStream.getTracks().forEach(track => {
                    pc.addTrack(track, localStream);
                    log("기존 PeerConnection에 로컬 트랙 추가됨");
                });
            }
        });
        
        if (stompClient && stompClient.connected) {
            sendSignal(window.roomId, { type: 'join', userId });
            log("연결 재요청 신호 전송됨");
        } else {
            if (!isConnecting && window.roomId) {
                log("WebSocket 연결이 없어 재연결 시도");
                connectWebSocket(window.roomId);
            } else if (!window.roomId) {
                log("방 ID가 없어 WebSocket 연결 실패");
            }
        }
    } catch (e) {
        console.error("카메라 접근 실패:", e);
        log("카메라 접근 실패: " + e.message);
        alert('카메라 접근 실패: ' + e.message);
    }
}

function connectWebSocket(roomId) {
    if (!roomId) {
        log("방 ID가 없어 WebSocket 연결 실패");
        return;
    }
    
    if (isConnecting) {
        log("이미 WebSocket 연결 시도 중...");
        return;
    }
    
    isConnecting = true;
    log("WebSocket 연결 시도 중...");
    
    try {
        const randomParam = new Date().getTime();
        const socket = new SockJS('/ws?t=' + randomParam);
        stompClient = Stomp.over(socket);
        
        const isDebugMode = false; 
        if (!isDebugMode) {
            stompClient.debug = function() {}; 
        } else {
            stompClient.debug = function(str) {
                if (str.includes("CONNECTED") || str.includes("ERROR") || str.includes("DISCONNECT")) {
                    console.log("[STOMP] " + str); 
                    addDebugInfo("[STOMP] " + str);
                }
            };
        }
        
        const headers = {};
        
        const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
        const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content;
        
        if (csrfHeader && csrfToken) {
            headers[csrfHeader] = csrfToken;
            log("CSRF 토큰이 헤더에 추가됨: " + csrfToken);
            log("CSRF 헤더명: " + csrfHeader);
            window.csrfToken = csrfToken;
            window.csrfHeader = csrfHeader;
        } else {
            log("CSRF 토큰이 설정되지 않음, 헤더 없이 진행");
            log("현재 전체 메타 태그 확인:");
            document.querySelectorAll('meta').forEach(meta => {
                console.log(meta.getAttribute('name') + ": " + meta.getAttribute('content'));
            });
        }
        
        
        const connectionTimeoutMs = 10000; // 10초
        let connectionTimeout = setTimeout(() => {
            log("WebSocket 연결 타임아웃 (10초)");
            if (isConnecting) {
                isConnecting = false;
                if (socket.readyState !== WebSocket.CLOSED) {
                    socket.close();
                }
                setTimeout(() => connectWebSocket(roomId), 3000);
            }
        }, connectionTimeoutMs);
        
        stompClient.connect(headers, function (frame) {
            clearTimeout(connectionTimeout); 
            isConnecting = false;
            log("WebSocket 연결 성공, frame: " + frame);
            log("room/" + roomId + " 구독 중");
            
            const subscriptionTopic = '/topic/room/' + roomId;
            log("구독 주소: " + subscriptionTopic);
            
            stompClient.subscribe(subscriptionTopic, function (message) {
                try {
                    log("Raw message received: " + message.body);
                    const data = JSON.parse(message.body);
                    if (data.userId === userId) {
                        log("자신의 메시지 무시: " + data.type);
                        return; 
                    }
                    
                    log("시그널링 메시지 수신: " + data.type + " (from: " + (data.userId || data.from) + ")");
                    handleSignal(data);
                } catch (e) {
                    console.error("메시지 처리 오류:", e, message.body);
                    log("메시지 처리 오류: " + e.message);
                }
            });
            
            sendSignal(roomId, { type: 'join', userId });
            log("방 입장 신호 전송됨");
            
            connectionCheckInterval = setInterval(() => {
                if (stompClient && stompClient.connected) {
                    log("WebSocket 연결 상태: 정상");
                } else {
                    log("WebSocket 연결 상태: 끊김, 재연결 시도");
                    clearInterval(connectionCheckInterval);
                    connectWebSocket(roomId);
                }
            }, 30000); 
            
        }, function(error) {
            clearTimeout(connectionTimeout); 
            isConnecting = false;
            console.error("WebSocket 연결 실패:", error);
            if (error.headers) {
                console.error("오류 헤더:", error.headers);
            }
            log("WebSocket 연결 실패: " + (error.message || JSON.stringify(error)));
            
            let errorMessage = "알 수 없는 오류";
            if (error.message) {
                errorMessage = error.message;
            } else if (typeof error === 'string') {
                errorMessage = error;
            }
            
            log("연결 실패 원인: " + errorMessage);
            log("3초 후 재시도...");
            
            setTimeout(() => {
                connectWebSocket(roomId);
            }, 3000);
        });
    } catch (e) {
        isConnecting = false;
        console.error("WebSocket 초기화 오류:", e);
        log("WebSocket 초기화 오류: " + e.message);
        log("3초 후 재시도...");
        
        setTimeout(() => {
            connectWebSocket(roomId);
        }, 3000);
    }
}

function sendSignal(roomId, data) {
    if (stompClient && stompClient.connected) {
        try {
            const message = JSON.stringify({ ...data, roomId, userId });
            log("시그널링 메시지 전송: " + data.type);
            
            const headers = {};
            if (window.csrfHeader && window.csrfToken) {
                headers[window.csrfHeader] = window.csrfToken;
            }
            
            stompClient.send('/app/signal/' + roomId, headers, message);
        } catch (e) {
            console.error("시그널링 메시지 전송 오류:", e);
        }
    } else {
        console.error("WebSocket 연결이 없어 메시지를 보낼 수 없음");
        if (!isConnecting) {
            log("WebSocket 연결이 없어 재연결 시도");
            connectWebSocket(roomId);
        }
    }
}

function handleSignal(data) {
    try {
        const { type, from, sdp, candidate, userId: remoteId } = data;
        const peerId = from || remoteId; 
        if (!peerId) {
            log("Peer ID가 없어 메시지 처리 불가");
            return;
        }
        
        if (type === 'join') {
            log("새 참가자 입장: " + peerId);
            createPeerConnection(peerId, true);
        } else if (type === 'offer') {
            log("Offer 수신: " + peerId);
            if (!peerConnections[peerId]) {
                log("Offer에 대한 새 PeerConnection 생성");
                createPeerConnection(peerId, false);
            }
            
            const pc = peerConnections[peerId];
            pc.setRemoteDescription(new RTCSessionDescription(sdp))
                .then(() => {
                    log("RemoteDescription(offer) 설정 성공");
                    return pc.createAnswer();
                })
                .then(answer => {
                    log("Answer 생성 성공");
                    return pc.setLocalDescription(answer);
                })
                .then(() => {
                    log("LocalDescription(answer) 설정 성공, Answer 전송");
                    sendSignal(roomId, { type: 'answer', sdp: pc.localDescription, from: userId, to: peerId });
                })
                .catch(error => {
                    console.error("Offer 처리 중 오류:", error);
                });
        } else if (type === 'answer') {
            log("Answer 수신: " + peerId);
            const pc = peerConnections[peerId];
            if (pc) {
                pc.setRemoteDescription(new RTCSessionDescription(sdp))
                    .then(() => {
                        log("RemoteDescription(answer) 설정 성공");
                    })
                    .catch(error => {
                        console.error("Answer 처리 중 오류:", error);
                    });
            } else {
                console.error("Answer 수신했으나 해당 PeerConnection이 없음:", peerId);
            }
        } else if (type === 'candidate') {
            log("ICE Candidate 수신: " + peerId);
            const pc = peerConnections[peerId];
            if (pc) {
                pc.addIceCandidate(new RTCIceCandidate(candidate))
                    .then(() => {
                        log("ICE Candidate 추가 성공");
                    })
                    .catch(error => {
                        console.error("ICE Candidate 추가 중 오류:", error);
                    });
            } else {
                console.error("ICE Candidate 수신했으나 해당 PeerConnection이 없음:", peerId);
            }
        }
    } catch (e) {
        console.error("시그널링 메시지 처리 오류:", e);
    }
}

function createPeerConnection(peerId, isOffer) {
    log("PeerConnection 생성 중: " + peerId + (isOffer ? " (Offer 발신)" : " (Offer 수신)"));
    
    if (peerConnections[peerId]) {
        log("기존 PeerConnection 제거: " + peerId);
        peerConnections[peerId].close();
        delete peerConnections[peerId];
    }
    
    try {
        const pc = new RTCPeerConnection(peerConnectionConfig);
        peerConnections[peerId] = pc;
        
        if (localStream) {
            log("PeerConnection에 로컬 스트림 추가");
            localStream.getTracks().forEach(track => {
                pc.addTrack(track, localStream);
            });
        } else {
            log("로컬 스트림이 없어 PeerConnection에 추가하지 않음");
        }
        
        pc.onicecandidate = (event) => {
            if (event.candidate) {
                log("ICE Candidate 생성: " + peerId);
                sendSignal(roomId, { 
                    type: 'candidate', 
                    candidate: event.candidate, 
                    from: userId, 
                    to: peerId 
                });
            } else {
                log("ICE Candidate 수집 완료: " + peerId);
            }
        };
        
        pc.oniceconnectionstatechange = (event) => {
            log("ICE 연결 상태 변경: " + pc.iceConnectionState + " (" + peerId + ")");
            if (pc.iceConnectionState === 'connected' || pc.iceConnectionState === 'completed') {
                log("ICE 연결 성공: " + peerId);
            } else if (pc.iceConnectionState === 'disconnected' || 
                pc.iceConnectionState === 'failed' || 
                pc.iceConnectionState === 'closed') {
                log("PeerConnection 연결 종료: " + peerId);
                const remoteVideo = document.getElementById('remoteVideo_' + peerId);
                if (remoteVideo) {
                    remoteVideo.parentNode.removeChild(remoteVideo);
                }
            }
        };
        
        pc.onconnectionstatechange = (event) => {
            log("Connection 상태 변경: " + pc.connectionState + " (" + peerId + ")");
        };
        
        pc.ontrack = (event) => {
            log("원격 트랙 수신: " + peerId);
            
            let remoteVideo = document.getElementById('remoteVideo_' + peerId);
            if (!remoteVideo) {
                log("원격 비디오 엘리먼트 생성: " + peerId);
                remoteVideo = document.createElement('video');
                remoteVideo.id = 'remoteVideo_' + peerId;
                remoteVideo.autoplay = true;
                remoteVideo.playsInline = true;
                remoteVideo.style.width = '100%';
                remoteVideo.style.maxHeight = '300px';
                remoteVideo.style.backgroundColor = '#333';
                remoteVideo.style.borderRadius = '8px';
                document.getElementById('remoteVideos').appendChild(remoteVideo);
            }
            
            if (remoteVideo.srcObject !== event.streams[0]) {
                log("원격 비디오에 스트림 설정: " + peerId);
                remoteVideo.srcObject = event.streams[0];
                
                remoteVideo.onloadedmetadata = () => {
                    log("원격 비디오 메타데이터 로드됨: " + peerId);
                    remoteVideo.play().catch(e => {
                        console.error("비디오 재생 실패:", e);
                    });
                };
            }
        };
        
        if (isOffer) {
            log("Offer 생성 중: " + peerId);
            pc.createOffer()
                .then(offer => {
                    log("Offer 생성 성공: " + peerId);
                    return pc.setLocalDescription(offer);
                })
                .then(() => {
                    log("LocalDescription(offer) 설정 성공, Offer 전송: " + peerId);
                    sendSignal(roomId, { 
                        type: 'offer', 
                        sdp: pc.localDescription, 
                        from: userId, 
                        to: peerId 
                    });
                })
                .catch(error => {
                    console.error("Offer 생성 중 오류:", error);
                });
        }
        
        return pc;
    } catch (e) {
        console.error("PeerConnection 생성 오류:", e);
        return null;
    }
}

if (typeof roomId !== 'undefined' && roomId) {
    log("방 ID 감지, WebSocket 연결 시작: " + roomId);
    setTimeout(() => {
        connectWebSocket(roomId);
    }, 500);
} else {
    console.error("roomId가 정의되지 않음, WebSocket 연결 실패");
}

window.onbeforeunload = function() {
    log("페이지 종료, 연결 정리");
    Object.keys(peerConnections).forEach(key => {
        if (peerConnections[key]) {
            peerConnections[key].close();
        }
    });
    
    if (localStream) {
        localStream.getTracks().forEach(track => track.stop());
    }
    
    if (stompClient && stompClient.connected) {
        stompClient.disconnect();
    }
}; 