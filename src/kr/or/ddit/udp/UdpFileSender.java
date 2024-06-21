package kr.or.ddit.udp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpFileSender {
	private DatagramSocket ds;
	private DatagramPacket dp;
	
	private InetAddress receiveAddr;
	private int port;
	private File file;
	
	// 파일 내용 보내기
	public UdpFileSender(String receiveIP, int port) {
		try {
			// 초기화함
			ds = new DatagramSocket();
			this.port = port;
			// InetAddress 객체가 필요해서 receiveIP를 InetAddress로 바꿔서 넣어줌
			// DatagramPacket의 3번째 파라미터에 필요함
			receiveAddr = InetAddress.getByName(receiveIP);
			file = new File ("d:/D_Other/jindalrae.jpg");
			
			if (!file.exists()) {
				System.out.println("파일이 존재하지 않습니다.");
				System.exit(0);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void start() {
		long fileSize = file.length();
		// 1000씩 증가, 마지막에는 1000보다 작은 잔여 데이터 사이즈로 초기화됨
		long totalReadBytes = 0;
		
		long startTime = System.currentTimeMillis();
		
		try {
			// sendData 3개는 파일을 보내기 위한 사전 기본 정보 전송
			// sendData 1번
			sendData("start".getBytes()); // 전송 시작을 알려주기 위한 문자열 전송
			
			// sendData 2번
			sendData(file.getName().getBytes()); // 파일명 전송
			
			// sendData 3번
			sendData(String.valueOf(fileSize).getBytes()); // 총 파일 크기 전송 (bytes)
			
			// 여기부터 실제 데이터 전송
			FileInputStream fis = new FileInputStream(file);
			
			// 1000바이트씩 잘라서 보냄
			// 여기서 속도 조절
			byte[] buffer = new byte[1000];
			
			
			while (true) {
				Thread.sleep(10); // 패킷 전송 간의 시간 간격을 주기 위해서
				
				// 1000 바이트 길이 맥시멈으로 읽어들여라
				// 마지막 잔여 데이터 길이를 읽기 위한 변수
				int readBytes = fis.read(buffer, 0, buffer.length);
				
				// 더 이상 읽을 파일 내용이 없을 경우 -1이 리턴됨
				if (readBytes == -1) { 
					break;
				}
				
				sendData(buffer, readBytes); // 읽어온 파일 내용 전송하기
				
				totalReadBytes += readBytes; // 전체 길이 합산
				
				System.out.println("진행 상태 : " + totalReadBytes + "/" + fileSize + " byte(s) (" + (totalReadBytes * 100 / fileSize) + " %)");
			}
			
			long endTime = System.currentTimeMillis();
			long diffTime = endTime - startTime;
			double transferSpeed = fileSize / diffTime;
			
			System.out.println("걸린 시간 : " + diffTime + " (ms)");
			System.out.println("평균 전송 속도 : " + transferSpeed + " Bytes/ms");
			
			System.out.println("전송 완료");
			
			fis.close();
			ds.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 
	/**
	 * 바이트 배열 데이터 전송하기, 지정된 길이만큼 잘라서 전송
	 * @param data 보낼 바이트 배열 데이터
	 * @param length 보낼 바이트 배열 크기
	 * @throws IOExceptionE
	 */
	public void sendData(byte[] data, int length) throws IOException {
		// 1000바이트씩 보내고 마지막 잔여 데이터 크기의 범위 밖을 잘라내는 기능을 가짐
		dp = new DatagramPacket(data, length, receiveAddr, port);
		ds.send(dp);
	}
	
	
	/**
	 * 바이트 배열 데이터 전송하기, 전체 길이 전송
	 * @param data
	 * @throws IOException
	 */
	public void sendData(byte[] data) throws IOException {
		sendData(data, data.length);
	}
	
	
	public static void main(String[] args) {
		new UdpFileSender("192.168.36.125", 8888).start();
	}
	
}
