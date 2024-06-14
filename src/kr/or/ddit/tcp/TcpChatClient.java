package kr.or.ddit.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

// Receiver와 Sender을 기반으로 TcpChatServer와 연결
public class TcpChatClient {
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		// 짝꿍과 채팅하기
		// 먼저 서버 역할을 할 클래스(TcpChatServer)를 실행하고
		// -> 클라이언트 역할을 할 클래스(TcpChatClient)를 실행하면 원격호스트(다른 사람의 서버)와 연결됨
		Socket socket = new Socket("192.168.36.125", 7777);
		
		System.out.println("채팅 서버에 연결되었습니다.");
		
		// 메시지 전송용 스레드 생성 및 실행
		Sender sender = new Sender(socket);
		// 메인 스레드는 실행을 시키고 죽을 것임
		sender.start();
		
		// 메시지 수신용 스레드 생성 및 실행
		Receiver receiver = new Receiver(socket);
		receiver.start();
		
		
	}
}
