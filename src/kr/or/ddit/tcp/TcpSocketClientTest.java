package kr.or.ddit.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// TcpSocketClientTest와 연결
public class TcpSocketClientTest {
	public static void main(String[] args) throws IOException {
		
		// TcpSocketServerTest를 먼저 실행하고
		// TcpSocketClientTest를 실행해야 함
		
		// 통신에 필요한 객체 => socket
		
		// 지금은 Tcp 방식이라서 연결을 먼저함
		
		String serverIp = "127.0.0.1";
		// 자기 자신의 컴퓨터(호스트)를 나타내는 방법
		// IP : 127.0.0.1
		// 도메인명 : localhost
		
		System.out.println(serverIp + " 서버에 접속 중입니다.");
		
		// 소켓을 설정해서 서버에 연결을 요청한다.
		Socket socket = new Socket(serverIp, 7777);
		
		// 연결이 되면 이후의 내용이 실행된다.
		System.out.println("서버에 연결되었습니다.");
		
		// 서버에서 보내온 메시지 받기
		
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		
		System.out.println("서버로부터 받은 메시지 : " + dis.readUTF());
		
		System.out.println("클라이언트 연결 종료");
		
		dis.close();
		
		socket.close();
	}
}
