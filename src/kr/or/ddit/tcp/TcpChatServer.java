package kr.or.ddit.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Receiver와 Sender을 기반으로 TcpChatClient와 연결
public class TcpChatServer {
	public static void main(String[] args) {
		
		// ServerSocket : 서버 소켓으로, 클라이언트의 연결 요청을 받아들이는 역할을 함
		// Socket : 클라이언트와 서버 간의 실제 통신을 담당하는 소켓 객체
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			// 예제의 7777은 아무 프로그램도 쓰지 않을 것 같은 포트라서 임시로 넣는 것 
			// 65535까지만 됨

			// 나의 7777번 포트를 열기
			serverSocket = new ServerSocket(7777);
			
			System.out.println("채팅 서버 준비완료");
			
			// 접속이 이루어지면 serverSocket에서 새로운 소켓 객체를 만들어서 리턴함
			// accept() 메서드는 ServerSocket이 준비(7777 열기)된 상태에서 클라이언트의 연결 요청을 받아들이고, 
			// 새로운 Socket 객체를 생성하여 반환하는 역할을 수행하며, 이를 통해 서버와 클라이언트 간의 통신 채널이 만들어짐
			socket = serverSocket.accept();
			
			// 이 메시지가 출력되면 채팅이 가능한 상태라고 보면 됨
			System.out.println("클라이언트와 연결됨");
			
			// 메시지 전송용 스레드 생성 및 실행
			Sender sender = new Sender(socket);
			// 메인 스레드는 실행을 시키고 죽을 것임
			sender.start();
			
			// 메시지 수신용 스레드 생성 및 실행
			Receiver receiver = new Receiver(socket);
			receiver.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
