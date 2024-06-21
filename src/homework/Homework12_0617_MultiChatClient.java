package homework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Homework12_0617_MultiChatClient {

	
	public static void main(String[] args) throws UnknownHostException, IOException {
		new Homework12_0617_MultiChatClient().clientStart();
	}
	
	
	public void clientStart() throws UnknownHostException, IOException {

		// 선생님의 IP주소 => 선생님의 컴퓨터가 서버가 되고, 우리가 접속을 함
		Socket socket = new Socket("192.168.36.127", 7777);
		
		System.out.println("멀티 챗 서버에 접속되었습니다.");
		System.out.println("대화명을 입력해주세요.");

		// 송신용 스레드 생성 및 실행
		ClientSender sender = new ClientSender(socket);
		sender.start();
		
		// 수신용 스레드 생성 및 실행
		ClientReceiver receiver = new ClientReceiver(socket);
		receiver.start();
		
	}
	
	
	class ClientSender extends Thread {
		
		private DataOutputStream dos;
		private Scanner scan;
		
		
		public ClientSender(Socket socket) {

			scan = new Scanner(System.in);
			try {
				// Socket("192.168.36.148", 7777)의 정보를 넣어서 보내고 있는 것
				// 소켓의 출력 스트림을 가져오기 위해 AccessController.doPrivileged() 메소드를 사용,
				// 이 메소드는 보안 관리자가 허용한 권한 내에서 작업을 수행할 수 있도록 함.
				// 내부적으로 impl.getOutputStream()을 호출하여 실제 출력 스트림을 반환한다.
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				if (dos != null) {
					// 시작하자마자 자신의 대화명을 서버로 전송한다.
					System.out.println("대화명 >> ");
					dos.writeUTF(scan.nextLine());
					System.out.println("채팅방에 입장하셨습니다.");
					System.out.println("채팅방을 나가려면 'end'를 입력하고, 귓속말을 하려면 '/w 대화명'을 입력하세요.");
					System.out.println();
				}
				
				// 다음부터 보내는 메시지는 채팅 메시지
				while (dos != null) {
					dos.writeUTF(scan.nextLine());
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	class ClientReceiver extends Thread {
		
		private DataInputStream dis;
		
		public ClientReceiver(Socket socket) {

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
					// 서버에서 보내주는 메시지 콘솔에 출력하기
					System.out.println(dis.readUTF());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
