package homework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Homework12_0617_MultiChatServer {

	
	public static void main(String[] args) {
	      new Homework12_0617_MultiChatServer().serverStart();
	}

	// 대화명 및 클라이언트의 Socket 객체를 저장하기 위한 Map객체변수 선언하기
	// Map<대화명, 소켓객체> -> 키값으로 대화명 = name을 사용
	private Map<String, Socket> clients;
	
	public Homework12_0617_MultiChatServer() {
		// 동기화를 위해 Collections.synchronizedMap(new HashMap<>(); 을 사용
		clients = Collections.synchronizedMap(new HashMap<String, Socket>());
	}
	
	
	public void serverStart() {
		
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(7777);
			
			System.out.println("멀티챗 서버가 시작되었습니다.");
			
			while (true) {
				// accept() : 클라이언트의 접속 요청을 기다린다.
				// 클라이언트가 소켓에 접속할 때까지
				socket = serverSocket.accept();
			
				System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] 에서 접속하였습니다.");
				
				// 사용자가 보내준 메시지를 처리하기 위한 스레드 생성 및 실행
				// 메인 스레드는 무한 루프와 스타트만 돌림
				// 실제로 대응하는 곳 : ServerReceiver 클래스
				ServerReceiver handler = new ServerReceiver(socket);
				handler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 대화명 즉, Map에 저장된 모든 사용자에게 채팅 메시지를 전송하기 위한 메서드 
	 * @param msg 채팅 메시지
	 * @param from 채팅을 보내는 사람의 대화명
	 */
	private void sendMessage(String msg, String from) {
		// 채팅 메시지일 때는 파라미터가 2개인 해당 메서드를 통해서 
		// 파라미터가 1개인 메서드를 불러서 메시지를 뿌림
		sendMessage("[" + from + "]" + msg);
	}
	

	/**
	 * 대화명 즉, Map에 저장된 모든 사용자에게 안내 메시지를 전송하기 위한 메서드 
	 * @param msg
	 */
	private void sendMessage(String msg) {

		Iterator<String> it = clients.keySet().iterator();
		
		// clients에 들어와있는 모든 사용자들에게 메시지를 보냄
		while (it.hasNext()) {
			
			try {
				String name = it.next();
				
				// 대화명에 해당하는 Socket 객체를 가져와서 메시지 보내기
				Socket socket = clients.get(name);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				dos.writeUTF(msg);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	private void whisperSendMessage(String msg, String receiveName, String sendName) {
			try {
				String name = receiveName;
				Socket socket = clients.get(name);
				
				msg = msg.replaceAll("/w " + receiveName, "[귓속말] " + "[" + sendName + "]");
				msg = msg.replaceAll("/W " + receiveName, "[귓속말] " + "[" + sendName + "]");
				
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(msg);
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	
	// 서버에서 클라이언트로부터 수신한 메시지를 처리하기 위한 클래스
	// Inner클래스로 정의함 (Inner클래스는 부모(Outer)클래스의 멤버들을 직접 접근할 수 있다.)
	
	// ServerReceiver => MultiChatServer에서만 사용하는 클래스, 내부 클래스로 선언해서 감추는 효과도 있다.
	class ServerReceiver extends Thread {
		
		private Socket socket;
		private DataInputStream dis;
		private String name;
		
		// 초기화 작업
		public ServerReceiver(Socket socket) {
			this.socket = socket;
			
			try {
				dis = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		// 실제 작업
		@Override
		public void run() {
			
			try {
				// 서버에서는 클라이언트가 보내는 최초의 메시지 즉, 대화명을 수신해야 한다.
				// 최초의 메시지가 대화명이 가도록 코딩할 것임
				name = dis.readUTF();
				name = name.replaceAll(" ", "");
				
				// 대화명을 받아서 다른 모든 클라이언트에게 대화방 참여 메세지를 보낸다.
				sendMessage("#" + name + "님이 입장했습니다");
				
				// 대화명과 소켓객체를 Map에 저장한다.
				clients.put(name, socket);
				System.out.println("현재 서버 접속자 수는 " + clients.size() + "명 입니다.");
				
				/////////////////////////////////////////
				
				// 이후의 메시지 처리는 채팅 메시지이다.
				while (dis != null) {
					
					String chat = dis.readUTF();
					String[] chatArray = chat.split(" ");
					
					if ( chatArray[0].equalsIgnoreCase("/w") ) {
						whisperSendMessage(chat, chatArray[1], name);
					} else if ( chat.equalsIgnoreCase("end") ) {
						System.out.println("채팅방을 나갔습니다.");
						break;
					} else {
						sendMessage(chat, name);						
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// 이 finally 영역이 실행된다는 것은 클라이언트의 접속이 종료되었다는 의미이다.
				// finally에 왔다는 건, 예외(catch)를 거쳐서 왔다는 의미 => 정상적인 실행이 더이상 불가능
				sendMessage("#" + name + "님이 퇴장했습니다.");
				
				// Map에서 해당 사용자 정보 제거하기
				// Outer클래스의 메서드와 멤버변수를 바로 가져와서 쓸 수 있음. 
				// Inner클래스를 썼을 때의 장점이라고 할 수 있다.
				clients.remove(name);
				
				System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] 에서 접속 종료하였습니다.");
				System.out.println("현재 서버 접속자 수는 " + clients.size() + "명 입니다.");
			}
		}
	}
}
