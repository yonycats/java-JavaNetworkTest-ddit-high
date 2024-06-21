package kr.or.ddit.udp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpFileReceiver {
	private DatagramSocket ds;
	private DatagramPacket dp;
	
	private byte[] buffer;
	
	public UdpFileReceiver(int port) {
		try {
			ds = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 패킷 데이터 수신하기
	 * @return 수신한 바이트 배열 데이터
	 * @throws IOException
	 */
	public byte[] receiveData() throws IOException {
		buffer = new byte[1000]; // 버퍼 초기화
		dp = new DatagramPacket(buffer, buffer.length);
		// 상대방이 나에게 패킷을 보내기할 때까지 대기
		ds.receive(dp);
		
		// 상대방이 보낸 데이터를 받기 시작
		return dp.getData();
	}
	
	
	public void start() throws IOException {
		long fileSize = 0;
		long totalReadBytes = 0;
		
		int readBytes = 0;
		
		System.out.println("파일 수신 대기 중");

		// UdpFileSender에서 보낸 sendData 1번 받기
		// trim()을 사용해서 양 끝 공백 제거
		String str = new String(receiveData()).trim();
		
		if (str.equals("start")) {
			
			// UdpFileSender에서 보낸 sendData 2번 받기
			// 전송 파일명 받기
			str = new String(receiveData()).trim();
			// 파일 다운받을 경로 지정
			FileOutputStream fos = new FileOutputStream("d:/D_Other/" + str);
			
			// UdpFileSender에서 보낸 sendData 3번 받기
			// 전송 파일 크기(bytes) 받기
			str = new String(receiveData()).trim();
			fileSize = Long.parseLong(str);
			
			long startTime = System.currentTimeMillis();
			
			while (true) {
				byte[] data = receiveData();
				// sender에서 보내는 파일의 크기를 순차적으로 받음, 1000씩 지정했으므로 1000씩
				readBytes = dp.getLength(); // 받은 데이터의 크기
				fos.write(data, 0, readBytes);
				
				// 받은 데이터의 크기를 모두 합산
				totalReadBytes += readBytes;
				
				System.out.println("진행 상태 : " + totalReadBytes + "/" + fileSize + " byte(s) (" + (totalReadBytes * 100 / fileSize) + " %)");
				
				// 파일을 다 받으면 루프를 빠져나옴
				if (totalReadBytes >= fileSize) {
					break;
				}
			}
			
			long endTime = System.currentTimeMillis();
			long diffTime = endTime - startTime;
			double transferSpeed = fileSize / diffTime;
			
			System.out.println("걸린 시간 : " + diffTime + " (ms)");
			System.out.println("평균 수신 속도 : " + transferSpeed + " Bytes/ms");
			
			System.out.println("수신 완료");
			
			fos.close();
			ds.close();
			
		} else {
			System.out.println("비정상 데이터 발견");
			ds.close();
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		new UdpFileReceiver(8888).start();
	}
}
