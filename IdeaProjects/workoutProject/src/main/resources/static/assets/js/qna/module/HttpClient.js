export default class HttpClient {
    constructor() {
        this.token = document.querySelector("meta[name='_csrf']")?.content;
        this.header = document.querySelector("meta[name='_csrf_header']")?.content;
        console.log('CSRF 토큰:', this.token);
        console.log('CSRF 헤더:', this.header);
    }

    getHeaders(isMultipart = false) {
        const headers = {};
        if (!isMultipart) {
            headers['Content-Type'] = 'application/json';
            headers['Accept'] = 'application/json';
        }
        if (this.token && this.header) {
            headers[this.header] = this.token;
        }
        return headers;
    }

    async get(url) {
        const response = await fetch(url, {
            headers: this.getHeaders(),
            credentials: 'include'
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    }

    async post(url, data, isMultipart = false) {
        try {
            console.log('POST 요청 시작:', url);
            console.log('데이터 유형:', isMultipart ? 'multipart/form-data' : 'application/json');

            const fetchUrl = url;
            const headers = {};
            if (!isMultipart) {
                headers['Content-Type'] = 'application/json';
                headers['Accept'] = 'application/json';
            } else {
                headers['Accept'] = 'application/json, text/plain, */*';
            }
            
            if (this.token && this.header) {
                headers[this.header] = this.token;
            }
            
            const body = isMultipart ? data : JSON.stringify(data);

            console.log('요청 헤더:', headers);
            if (!isMultipart) {
                console.log('요청 본문:', body);
            } else {
                console.log('FormData 키:', Array.from(data.keys()));
            }

            const response = await fetch(fetchUrl, {
                method: 'POST',
                headers,
                body,
                credentials: 'include'
            });

            console.log('응답 상태:', response.status);
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('응답 에러:', errorText);
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            } else {
                const text = await response.text();
                if (text) {
                    try {
                        return JSON.parse(text);
                    } catch (e) {
                        return { message: text };
                    }
                }
                return { success: true };
            }
        } catch (error) {
            console.error('POST 요청 오류:', error);
            throw error;
        }
    }

    async patch(url, data) {
        try {
            const fetchUrl = url;
            console.log('PATCH 요청 URL:', fetchUrl);
            
            const headers = this.getHeaders();
            console.log('PATCH 요청 헤더:', headers);
            
            const response = await fetch(fetchUrl, {
                method: 'PATCH',
                headers: headers,
                body: JSON.stringify(data),
                credentials: 'include'
            });
            
            console.log('PATCH 응답 상태:', response.status);
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('PATCH 응답 에러:', errorText);
                throw new Error(`서버 응답 오류: ${response.status}`);
            }
            
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            } else {
                const text = await response.text();
                if (text) {
                    try {
                        return JSON.parse(text);
                    } catch (e) {
                        return { message: text };
                    }
                }
                return { success: true };
            }
        } catch (error) {
            console.error('PATCH 요청 오류:', error);
            throw error;
        }
    }

    async delete(url) {
        try {
            const fetchUrl = url;
            const response = await fetch(fetchUrl, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }


            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                return await response.json();
            }

            return null;
        } catch (error) {

            throw error;
        }
    }


}