package kr.or.ddit.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// TcpSocketServerTest와 연결
public class TcpSocketServerTest {
	public static void main(String[] args) throws IOException {
		
		// Tcp 소켓 통신을 위한 서버소켓 생성하기
		// 포트번호 : 호스트 내의 어떠한 프로그램에 접속을 할지 구분하는 번호
		
		// 예제의 7777은 아무 프로그램도 쓰지 않을 것 같은 포트라서 임시로 넣는 것 
		ServerSocket server = new ServerSocket(7777);
		System.out.println("서버가 접속을 기다립니다.");
		
		// accept() 메서드는 클라이언트에서 연결 요청이 올 때까지 계속 기다린다.
		// 연결 요청이 오면 Socket 객체를 생성해서 클라이언트의 Socket과 연결한다.
		Socket socket = server.accept();
		
		// ---------------------------------------------
		// 이 이후는 클라이언트와 연결된 후의 작업을 진행하면 된다.
		
		System.out.println("접속한 클라이언트 정보");
		System.out.println("주소 : " + socket.getInetAddress());
		
		// 클라이언트에게 메시지 보내기
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeUTF("어서오세요. 방가방가!");
		
		System.out.println("클라이언트에게 메시지를 보냈습니다.");
		
		dos.close();
	}
}
