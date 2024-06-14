package kr.or.ddit.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpClient {
	private DatagramSocket ds;
	private DatagramPacket dp;
	
	private byte[] msg; // 패킷 송수신을 위한 바이트배열 선언
	
	
	public static void main(String[] args) throws IOException {
		new UdpClient().start();
	}
	
	
	public UdpClient() {
		
		msg = new byte[100];
		
		try {
			// 포트번호 지정 안할거임, 왜? -> 포트번호를 지정할 필요가 없어서, 서버에서 알아낼 것이기 때문
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	
	private void start() throws IOException {
		InetAddress serverAddr = InetAddress.getByName("192.168.36.131");

		// 서버쪽에 IP와 포트번호를 전달하기 위해서 단순히 1바이트의 데이터를 보냄, 데이터 자체의 의미는 없음
		dp = new DatagramPacket(msg, 1, serverAddr, 8888);
		ds.send(dp); // 패킷 전송하기
		
		dp = new DatagramPacket(msg, msg.length);
		// 서버가 나에게 패킷 정보를 보낼 때까지 대기
		ds.receive(dp); // 패킷 수신하기
		
		System.out.println("현재 서버 시간 => " + new String(dp.getData()));
	}
}
