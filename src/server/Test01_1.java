package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;    
import java.net.Socket;  
public class Test01_1 {
  public static void main(String[] args) throws Exception {
    System.out.println("서버 실행 중...");
   
    // 1) 서버 소켓 만들기
    ServerSocket serverSocket = new ServerSocket(5000);
  
    // 2) 대기열의 첫 번째 클라이언트의 접속을 승인한다.
    // => 만약 대기 중인 클라이언트가 없다면, 리턴하지 않고 접속할 때까지 기다린다.
    // => 여러 클라이언트가 대기 중이라면 그 중 첫 번째 클라이언트와 연결한다.
    // => 리턴 값: 대기열에 등록된 첫 번째 클라이언트의 연결 정보를 가지고 만든 소켓 객체이다.
    Socket socket = serverSocket.accept();
   
    // 3) 소켓을 통해 입출력을 할 수 있도록 스트림 객체를 만든다.
    InputStream in = socket.getInputStream();
    OutputStream out = socket.getOutputStream();
   
    // 4) 소켓으로 클라이언트와 연결되면 데이터를 주고 받는 순서는 상관없다.
    //    그러나 일반적으로 클라이언트에서 먼저 데이터를 전송한다.
    //    그래서 서버는 클라이언트의 데이터를 먼저 읽고 그에 대한 응답을 한다.
    int b = in.read(); // 클라이언트에서 1바이트를 보낼 때까지 계속 기다린다(blocking).
    System.out.printf("%x\n", b);
   
    out.write(0x01); // 서버가 보낸 1바이트 데이터를 클라이언트에서 읽을 때까지 리턴하지 않는다.
   
    // 5) 클라이언트와 데이터 입출력이 끝났으면 연결을 끊는다.
    in.close();
    out.close();
    socket.close();
    serverSocket.close();
  }
}