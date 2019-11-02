import java.io.IOException;  
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*socket()함수 사용하여 tcp socket 생성
 * bind() 함수 이용해 소켓에 포트번호 부여
 * listen() 함수 이용해 클라이언트 접속 기다림
 * 클라이언트로 부터 접속이 되면 accept() 함수 호출해 클라이언트 연결에 대한 새로운 소켓을 생성
 * send()함수와 recv() 함수를 이용해 서버소켓과 새로만든 클라이언트 소켓을 이용해 데이터 주고받는다
 * !!소켓통신을 간단히 말하자면 서버는 특정 포트가 바인딩된 소켓을 가지고 컴퓨터 위에서 돕니다. 
 * 해당 서버는 클라이언트의 연결 요청을 소켓을 통해 리스닝 하면서 기다림
 * 

*/
public class ChatServer {
	public static final int PORT = 5000;

	public static void main(String[] args) {
		Connection con = null;
		String url = "jdbc:mysql:///Daily?serverTimezone=Asia/Seoul";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url,"root","jsppass");
			Statement stmt = con.createStatement();
			String sql ="delete from userCheck;";
			stmt.executeUpdate(sql);
		} catch (ClassNotFoundException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ServerSocket serverSocket = null;
		List<PrintWriter> listWriters = new ArrayList<PrintWriter>();

		try {
			// 1. 서버 소켓 생성
			serverSocket = new ServerSocket();
   
			// 2. 바인딩//소켓포트를 바인딩하므로서 클라이언트의 연결을 기다린다 .
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			serverSocket.bind(new InetSocketAddress(hostAddress, PORT));
			consoleLog("연결 기다림 - " + hostAddress + ":" + PORT);

			// 3. 요청 대기
			while (true) {
				Socket socket = serverSocket.accept();//클라이언트로 부터 접속이 되면 accept() 함수 호출해 클라이언트 연결에 대한 새로운 소켓을 생성
				new ChatServerProcessThread(socket, listWriters).start();
				System.out.println("접속됨");
			}
		} catch (IOException e) {  
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
				}
			} catch (IOException e) {//입출력에 관해서 예외처리를 해준다.
				e.printStackTrace();
			}
		}
	}  

	private static void consoleLog(String log) {
		System.out.println("[server " + Thread.currentThread().getId() + "] " + log);
	}
}