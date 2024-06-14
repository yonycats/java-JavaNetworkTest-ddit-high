package kr.or.ddit.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UdpServer {
	private DatagramSocket ds;
	private DatagramPacket dp;
	
	private byte[] msg; // 패킷 송수신을 위한 바이트배열 선언
	
	public void UdpServer(int port) {
		try {
			// 메시지 수신을 위한 포트번호 설정
			// 파라미터를 안넣고 빈칸으로 놔둬도 오류가 나지 않음, 사용 가능한 포트번호 중에 랜덤으로 사용함
			// 정확한 포트 사용을 위해서 파라미터로 포트번호를 지정함
			ds = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	
	public void start() throws IOException {
		while (true) {
			// 데이터를 수신하기 위한 패킷 객체를 생성한다.
			msg = new byte[1];
			dp = new DatagramPacket(msg, msg.length);
			
			System.out.println("패킷 수신 대기 중");
			
			// 패킷 객체를 통해 데이터 (바이트 배열)을 수신한다.
			// 여기서 대기함, 언제까지? -> 상대방이 패킷을 보낼 때까지
			ds.receive(dp);
			
			System.out.println("패킷 수신 완료");
			
			// 수신한 패킷 정보를 확인하여 보낸 사람의 IP주소와 포트번호를 알아낸다.
			InetAddress ipAddr = dp.getAddress();
			int port = dp.getPort();
			
			// 서버의 현재 시간을 시분초 형태 ([hh:mm:ss])의 문자열로 상대방에게 보내준다.
			SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:ss]");
			String time = sdf.format(new Date());
			msg = time.getBytes(); // 시간 문자열을 바이트 배열로 변환한다.
			
			// 패킷에 바이트 배열 데이터를 담아 상대방에게 보내준다.
			// 패킷을 초기화 하는 과정에서 필요한 파라미터들을 넣어줌
			// new DatagramPacket(바이트 배열(현재서버시간), 바이트 배열의 유효한 길이, 상대방ip주소, 상대방포트번호);
			dp = new DatagramPacket(msg, msg.length, ipAddr, port);
			// 상대방에게 전달함
			ds.send(dp);
		}
	}
}
