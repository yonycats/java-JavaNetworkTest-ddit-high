package kr.or.ddit.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// 보내는 기능을 갖고 있음, 스레드 상속
public class Sender extends Thread {

	private Scanner scan;
	private String name;
	private DataOutputStream dos; // 보내기
	
	public Sender(Socket socket) {
		name = "[" + socket.getLocalAddress() + ":" + socket.getLocalPort() + "]";
		
		scan = new Scanner(System.in);
		
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		while (dos != null) {
			
			try {
				// DataOutputStream 클래스의 writeUTF(String str) 메서드 : UTF-8 형식으로 코딩된 문자열을 객체에 넣는다.
				dos.writeUTF( name + " >>> " + scan.nextLine() ); // 입력 문자열 전송하기
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}
	}
}
