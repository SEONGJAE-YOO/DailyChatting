import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
//자바에서 Thread를 만드는 방법은 크게 Thread 클래스를 상속받는 방법과 Runnable인터페이스를 구현하는 방법이 있다.
//인터페이스 상속시 다중상속이 가능하므로 implements를 쓰고 extends쓸때는 하나만 상속이 가능하다 .
public class ChatServerProcessThread<E> implements Runnable {/*식별자없이 실행가능한 함수-람다식 장점
	1. 코드를 간결하게 만들 수 있습니다.*/
	private String nickname = null; 
	private Socket socket = null;
	private String index = "";
	List<PrintWriter> listWriters = null; //순서화 된 자료구조에서 printwriter를 쓰므로서 주어진 데이터를 문자스트림에 출력한다.  
	UserCheck check;

	public ChatServerProcessThread(Socket socket, List<PrintWriter> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
		check = new UserCheck();
	}
     
	@Override
	public void run() {
		try {
			BufferedReader buffereedReader = new BufferedReader( //Creates a buffering character-input stream
					new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
//스트림이란 배열이나 문자열 같든 데이터 컬렉션을 말하며, 자료를 입출력하기 위하여 사용하는 것이다.
			//버퍼는 기본적으로 입출력 전송 속도차이에 대한 성능을 보완하기 위해 사용합니다. 입력속도에 비해 출력속도가 느린경우 데이터를 임시 저장하는 공간을 말하며, 임시저장장치라고도 합니다.
			
			PrintWriter printWriter = new PrintWriter( //output스트림으로 문자열 출력한다.
					new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

			while (true) {
				String request = buffereedReader.readLine();
				/*if (request == null) {
					consoleLog("클라이언트로부터 연결 끊김");
					doQuit(printWriter);
					break;
				}*/

				String[] tokens = request.split(":");
				System.out.println("tokens[0]=" + tokens[0]);
				if ("table".equals(tokens[0])) {
					broadcast(request);
				} else if ("list_quit".equals(request)) {
					removeWriter(printWriter);
				}

				else if ("join".equals(tokens[0])) {
					doJoin(tokens[1], printWriter);
				} else if ("chatting-join".equals(tokens[0])) {
					this.index = tokens[1];
					chatJoin(tokens[2], printWriter);

				} else if ("sendMassage".equals(tokens[0])) {
					System.out.println(tokens[1] + tokens[2]);
					doMessage(tokens[0] + ":" + tokens[1] + ":" + nickname + ":" + tokens[2]);
				} else if ("quit".equals(tokens[0])) {
					doQuit(printWriter,tokens[1]);
				} else if ("bye".equals(tokens[0])) {
					broadcast("bye:" + tokens[1]);
					removeWriter(printWriter);
				}
			}
		} catch (IOException e) {
			consoleLog(this.nickname + "님이 접속을 종료하셨습니다.");

		} finally {
			check.Check_delete(this.nickname);
		}
	}

	private void doQuit(PrintWriter writer,String tokens) {

		String data = "quit:" + tokens + ":" + this.nickname + "님이 퇴장했습니다.";

		broadcast(data);
		// removeWriter(writer);
	}

	private void removeWriter(PrintWriter writer) {
		synchronized (listWriters) {
			listWriters.remove(writer);
		}  
	}

	private void doMessage(String data) {
		broadcast(data);
	}

	private void doJoin(String nickname, PrintWriter writer) {
		this.nickname = nickname;

		String data = "join:" + nickname;
		System.out.println("data=" + data);
		addWriter(writer);
		broadcast(data);
		check.Check_insert(this.nickname);
		// writer pool에 저장

	}

	private void chatJoin(String nickname, PrintWriter writer) {
		this.nickname = nickname;

		String data = "chatjoin:" + index + ":" + nickname;
		System.out.println("data=" + data);
		 addWriter(writer);
		broadcast(data);

		// writer pool에 저장

	}

	private void addWriter(PrintWriter writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void broadcast(String data) {
		synchronized (listWriters) {
			for (PrintWriter writer : listWriters) {
				writer.println(data);
				writer.flush();
			}
		}
	}

	private void consoleLog(String log) {
		System.out.println(log);
	}

	
}