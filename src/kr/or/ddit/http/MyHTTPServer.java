package kr.or.ddit.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.StringTokenizer;


// HTTP 프로토콜 요청을 처리할 수 있는 서버를 만드는 것
// 외부 컴퓨터에서 연 파일 주소도 들어갈 수 있음
// F12 > Network > Ctrl + R > Headers 에서 정의한 변수들 확인 가능
public class MyHTTPServer {
	private final int PORT = 80;
	private final String ENCODING = "UTF-8";
	
	
	public static void main(String[] args) {
		new MyHTTPServer().start();
	}
	
	
	public void start() {
		System.out.println("HTTP 서버가 시작되었습니다.");

		ServerSocket serverSocket = null;
		
		try {
			// 서버 생성
			serverSocket = new ServerSocket(this.PORT);

			while (true) {
				// 대기 후, 접속 요청 받아들이기
				Socket socket = serverSocket.accept();

				// HttpHandler -> HTTP 교환을 처리하기 위한 객체
				HttpHandler handler = new HttpHandler(socket);
				handler.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	/**
	 * HTTP 요청 처리를 위한 스레드
	 * @author PC-11
	 *
	 */
	class HttpHandler extends Thread {
		private Socket socket;
		
		
		public HttpHandler(Socket socket) {
			this.socket = socket;
		}
		
		
		@Override
		public void run() {
			BufferedOutputStream out = null;
			BufferedReader br = null;
			
			try {
				// 소켓으로부터 오는 정보를 한줄씩 읽어오기
				out = new BufferedOutputStream(socket.getOutputStream());
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				// 요청 헤더정보 파싱하기
				
				// Request Line
				// 인터넷 주소 창에 localhost/main/index.html 를 보냈을 경우
				// 출력 => Request Line : GET /main/index.html HTTP/1.1
				String reqLine = br.readLine(); // 첫 줄은 요청라인
				System.out.println("Request Line : " + reqLine);
				
				// 들어있는 경로 정보를 InputStream에 바디로 잘 감싸서 잘 보낼 것임
				String reqPath = ""; // 요청 경로
				
				// 요청 페이지 경로 정보 가져오기
				// split()를 써도 됨
				// reqLine을 공백을 기준으로 자르기
				StringTokenizer st = new StringTokenizer(reqLine);
				
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					
					// 시작 문자가 /이면 reqPath에 token 저장
					if (token.startsWith("/")) {
						reqPath = token;
						break;
					}
				}
				
				// URL 디코딩 처리 (한글 깨짐 문제 처리)
				// 파일명이나 경로가 한글인 경우를 대비
				
				// URLDecoder.decode(reqPath, ENCODING); => ENCODING(UTP-8) 형식의 값으로 변경
				// URLEncoder.encode(reqPath, ENCODING); => 웹에서 사용하는 형태의 코드값으로 변경(%문자 출력됨)
				reqPath = URLDecoder.decode(reqPath, ENCODING);
				System.out.println("reqPath : " + reqPath);
				
				// filePath =>  ./WebContent/hyperlink.html
				// 여기서 ./WebContent를 넣어서 경로를 지정해주고 있음
				String filePath = "./WebContent" + reqPath;
				System.out.println("filePath : " + filePath);
				
				// URLConnection => HTTP 통신을 가능하게 해 주는 클래스, 웹 서버와의 HTTP 요청 및 응답을 처리하는데 사용
				// 해당 파일 이름을 이용하여 Content-Type 정보 추출하기
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);
				
				// CSS 파일인 경우 인식이 안되서 추가함
				if (contentType == null && filePath.endsWith(".css")) {
					contentType = "text/css";
				}
				System.out.println("Content-Type : " + contentType);
				System.out.println();
				
				File file = new File(filePath);
				
				if (!file.exists()) {
					// 에러 페이지 출력하기
					return;
				}
				
				// https://noahlogs.tistory.com/34 참고
				byte[] body = makeResponseBody(filePath);
				System.out.println("body : " + body);
						
				byte[] header = makeResponseHeader(body.length, contentType);
				
				System.out.println("////////////////////////////////////////");
				////////////////////////////////////////
				
				// 응답 헤더정보 보내기
				out.write(header);
				
				// 응답 내용 보내기 전 반드시 Empty Line 보내기
				out.write("\r\n\r\n".getBytes());
				
				// 응답 내용 보내기
				out.write(body);
				
				out.flush();
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close(); // 소켓 연결 끊기
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 응답 헤더정보 생성하기
	 * @param contentLength 응답내용 크기
	 * @param mimeType 컨텐츠 타입정보
	 * @return 헤더정보 바이트배열
	 */
	private byte[] makeResponseHeader(int contentLength, String mimeType) {
		// \r\n를 다 붙이면 모든 운영체제(윈도우, 리눅스 등)에서 엔터로 인식함
		String header = "HTTP/1.1 200 OK\r\n"
					+ "Server : MyHTTPServer 1.0\r\n"
					+ "Content-Length : " + contentLength + "\r\n"
					+ "Content-Type : " + mimeType + "; charset = " + this.ENCODING;
		
		System.out.println("header => " + header);
		System.out.println();
		
		// 바이트 내용을 리턴함
		return header.getBytes();
	}
	
	
	/**
	 * 응답 내용 생성하기
	 * @param filePath 응답으로 사용할 파일경로
	 * @return 바이트 배열 데이터
	 */
	private byte[] makeResponseBody(String filePath) {
		// filePath =>  ./WebContent/hyperlink.html
		byte[] data = null;
		
		FileInputStream fis = null;
		
		try {
			File file = new File(filePath);
			// data 배열에 file.length()의 전체 길이만큼의 배열을 선언해줌, 
			// 파일 내용이 800바이트라면 => data = new byte[800];
			data = new byte[(int) file.length()];
			
			fis = new FileInputStream(file);
			// data에 파일 길이만큼의 배열 크기가 선언되어 있기 때문에 1번만 읽어서 내용을 다 넣는다.
			fis.read(data);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		// 바이트 배열(실제 파일 컨텐츠 내용의 바이트가 들어있는) 리턴 
		return data;
	}
	
}
