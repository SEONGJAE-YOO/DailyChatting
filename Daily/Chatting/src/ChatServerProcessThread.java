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

public class ChatServerProcessThread<E> extends Thread {
	private String nickname = null;
	private Socket socket = null;
	private String index = "";
	List<PrintWriter> listWriters = null;
	UserCheck check;

	public ChatServerProcessThread(Socket socket, List<PrintWriter> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
		check = new UserCheck();
	}

	@Override
	public void run() {
		try {
			BufferedReader buffereedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

			PrintWriter printWriter = new PrintWriter(
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