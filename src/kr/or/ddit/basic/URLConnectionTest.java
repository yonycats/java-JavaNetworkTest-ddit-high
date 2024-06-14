package kr.or.ddit.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLConnectionTest {
	
	/*
	 	IOException는 스트림, 파일 및 디렉터리를 사용하여 정보에 액세스하는 동안 throw된 예외에 대한 기본 클래스이다.
		기본 클래스 라이브러리에는 각각 의 파생 클래스 IOException인 다음 형식이 포함된다.
		
		DirectoryNotFoundException
		EndOfStreamException
		FileNotFoundException
		FileLoadException
		PathTooLongException
	
		적절한 경우 IOException 대신 이러한 형식을 사용한다.
	 */
	
	public static void main(String[] args) throws IOException {
		
		// URLConnection => 애플리케이션과 URL간의 통싱 연결을 위한 추상 클래스
		
		// 특정 서버(예 : 네이버)의 리소스 접근해 가져오기
		
		URL url = new URL("https://www.naver.com/index.html");
		
		// Header 정보 가져오기
		
		URLConnection urlConn = url.openConnection();
		
		System.out.println("Content-Type : " + urlConn.getContentType());
		System.out.println("Encoding : " + urlConn.getContentEncoding());
		System.out.println("Content : " + urlConn.getContent());
		
		
		///////////////////////////////////////////
		
		// HttpInputStream를 이용해서 실제 내용인  html 내용을 가져오기
		// 바이트 기반을 문자 기반 스트림으로 변환해서 가져올 것임
		
		// 내가 갖고 오고 싶은 곳의 url을 알면 해당 데이터의 리소스를 끌어올 수 있음

		InputStream is = (InputStream) urlConn.getContent();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		
		// 위 3줄을 1줄에 쓰기
		BufferedReader br1 = new BufferedReader(new InputStreamReader( (InputStream) urlConn.getContent(), "UTF-8" ));
		
		
		String temp = "";
		
		while ( (temp = br.readLine()) != null ) {
			System.out.println(temp);
		}
		br.close();
				
		// 소켓 : 네트워크로 연결된 두대의 호스트 간의 통신을 위한 양쪽 끝을 의미한다.
		
		
	}
}
