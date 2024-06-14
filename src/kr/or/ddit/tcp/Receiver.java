package kr.or.ddit.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// 받는 기능을 갖고 있음, 스레드 상속
public class Receiver extends Thread {
	private DataInputStream dis; // 읽기

	public Receiver(Socket socket) {
		try {
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (dis != null) {
			try {
				// DataInputStream 클래스의 readUTF() 메서드 : UTF-8 형식으로 코딩된 문자열을 읽어온다.
				System.out.println(dis.readUTF());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
