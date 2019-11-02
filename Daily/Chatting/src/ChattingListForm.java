import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.sql.*;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.events.MouseEvent;

import com.mysql.jdbc.PreparedStatement;

public class ChattingListForm extends JFrame implements ActionListener {
	private static final String SERVER_IP = "169.254.116.139";
	private static final int SERVER_PORT = 5000;
	Color color;

	private JFrame frame;
	private JLabel label;
	private JPanel panel;

	Container contentPane;

	String tableCells[][] = new String[15][3]; // 15부분에 디비 갯수
	String colNames[] = { "번호", "방 제목", "인원 수" };
	JTable table;
	DefaultTableModel model;
	JScrollPane scrollpane;

	JPanel bottomPanel;
	JButton makeButton;
	JTextField roomText;

	JPanel listPanel;
	JLabel listLabel;
	JLabel listLabel2;
	JList list;
	JList list2;
	DefaultListModel listModel;
	DefaultListModel listModel2;
	JScrollPane Listscrollpane;
	JScrollPane Listscrollpane2;
	JTextField listText;
	JPanel btnPanel;
	JButton addButton;
	JButton delButton;

	Connection con = null;
	String sql;
	String url = "jdbc:mysql:///Daily?serverTimezone=Asia/Seoul";
	String Link_id;
	String Link_name;
	String friend_id = "";
	String friend_name = "";
	String[] f_list;
	PrintWriter printWriter;
	int Cells = 0;
	int r_index = 1;

	Socket socket = new Socket();
	private int friends_size;

	public ChattingListForm(String id, String name) {

		color = new Color(243, 218, 232); // 위 아래 색깔 (바꿔도 돼요)

		frame = new JFrame();
		contentPane = frame.getContentPane();

		panel = new JPanel();
		label = new JLabel();
		roomText = new JTextField(20);
		table = new JTable(tableCells, colNames);

		list = new JList(new DefaultListModel());
		listModel = (DefaultListModel) list.getModel();
		list2 = new JList(new DefaultListModel());
	    listModel2 = (DefaultListModel) list2.getModel();
	    
		listPanel = new JPanel();
		listLabel = new JLabel();
		listLabel2 = new JLabel();
		listText = new JTextField(9);
		addButton = new JButton("추가");
		delButton = new JButton("삭제");
		btnPanel = new JPanel();

		bottomPanel = new JPanel();
		makeButton = new JButton("방 만들기");
		makeButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		makeButton.setBackground(Color.BLACK);
		makeButton.setForeground(Color.WHITE);
		this.Link_id = id;
		this.Link_name = name;

		try {
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
					true);
			String request = "join:" + Link_id + "\r\n";
			System.out.println("보낼 값 = " + request);
			printWriter.println(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		new ChattingThread(socket).start();

		addButton.addActionListener(this);
		delButton.addActionListener(this);
		makeButton.addActionListener(this);

		// listModel
		
		show();
		table.addMouseListener(new java.awt.event.MouseAdapter() { // 테이블 셀 이벤트
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {		
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) { // row 행 , col 열
					System.out.println(model.getValueAt(row, 0));
					if (table.getValueAt(row, 0) != null) {
						new ChattingForm(model.getValueAt(row, 0).toString(), model.getValueAt(row, 1).toString(),
								Link_id, Link_name, socket);
						// printWriter.println("list_quit");
						/*
						 * try { socket.close(); } catch (IOException e) { // TODO Auto-generated catch
						 * block e.printStackTrace(); }
						 */
					}

					/*
					 * if(evt.getClickCount()>=2) { System.out.println("두번누르셨군요?"); }
					 */

				}
			}
		});
	}

	public void show() {

		// label
	      label.setFont(new Font("맑은 고딕", Font.BOLD, 20));
	      label.setText("채팅방 목록");

	      panel.setBackground(color);
	      panel.add(label);
	      frame.add(BorderLayout.NORTH, panel);

	      // 방 만들기
	      bottomPanel.setBackground(color);
	      roomText.setFont(new Font("맑은 고딕", Font.BOLD, 20));
	      bottomPanel.add(roomText);
	      bottomPanel.add(makeButton);
	      frame.add(bottomPanel, BorderLayout.SOUTH);

	      // table
	      table.setRowHeight(40);
	      table.getColumn("번호").setPreferredWidth(5);
	      table.getColumn("방 제목").setPreferredWidth(300);
	      table.getColumn("인원 수").setPreferredWidth(5);
	      scrollpane = new JScrollPane(table);
	      scrollpane.setPreferredSize(new Dimension(550, 500));
	      contentPane.add(scrollpane, BorderLayout.WEST);

	      // list
	      listLabel2.setText("접속자 목록");
	      listLabel2.setFont(new Font("맑은 고딕", Font.BOLD, 10));
	      listLabel2.setPreferredSize(new Dimension(130,15));
	      
	      Listscrollpane2 = new JScrollPane(list2);
	      Listscrollpane2.setPreferredSize(new Dimension(130,130));
	      listPanel.add(listLabel2,BorderLayout.NORTH);
	      listPanel.add(Listscrollpane2,BorderLayout.SOUTH);
	      
	      listLabel.setText("친구 목록");
	      listLabel.setFont(new Font("맑은 고딕", Font.BOLD, 10));
	      listLabel.setPreferredSize(new Dimension(130,15));

	      Listscrollpane = new JScrollPane(list);
	      Listscrollpane.setPreferredSize(new Dimension(130, 130));
	      listPanel.add(listLabel, BorderLayout.NORTH);
	      listPanel.add(Listscrollpane, BorderLayout.SOUTH);

	      
	      listText.setFont(new Font("맑은 고딕", Font.BOLD, 15));
	      listPanel.add(listText, BorderLayout.SOUTH);
	      
	      // 친구 추가 버튼
	      addButton.setPreferredSize(new Dimension(60, 28));
	      addButton.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	      addButton.setBackground(Color.BLACK);
	      addButton.setForeground(Color.WHITE);
	      btnPanel.add(addButton, BorderLayout.WEST);

	      // 친구 삭제 버튼
	      delButton.setPreferredSize(new Dimension(60, 28));
	      delButton.setFont(new Font("맑은 고딕", Font.BOLD, 13));
	      delButton.setBackground(Color.RED);
	      delButton.setForeground(Color.WHITE);
	      btnPanel.add(delButton, BorderLayout.EAST);

	      listPanel.add(btnPanel, BorderLayout.SOUTH);
	      btnPanel.setBackground(Color.WHITE);
	      listPanel.setBackground(Color.WHITE);

		frame.add(listPanel, BorderLayout.CENTER);

		String friend_id = "";
		String friend_name = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");

			con = DriverManager.getConnection(url, "root", "1234");
			Statement stmt = con.createStatement();
			sql = "select * from Chat_list where user_id = '" + Link_id + "';";

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				friend_id = rs.getString("friend_id");
				friend_name = rs.getString("friend_name");
				listModel.addElement(friend_id);
			} // 친구 추가된 리스트를 불러와 추가 chat_list 참조

			sql = "select * from room where room_index = any(select chat_index from user_chat where user_id= '"
					+ Link_id + "'); ";
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				tableCells[Cells][0] = Integer.toString(rs.getInt("room_index"));
				tableCells[Cells][1] = rs.getString("room_name");
				tableCells[Cells][2] = rs.getString("room_member");
				Cells++;
			}
			model = new DefaultTableModel(tableCells, colNames);
			model.addRow(tableCells);
			
			sql = "select * from userCheck;";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				if(!(rs.getString("Login_id").equals(Link_id)))
				listModel2.addElement(rs.getString("Login_id"));
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// frame
		frame.addWindowListener(new WindowAdapter() { // 창 x키 누르면 닫히는거
			public void windowClosing(WindowEvent e) {
				printWriter.println("bye:"+Link_id);
				new UserCheck().Check_delete(Link_id);
				/*
				 * try { socket.close(); printWriter.close();
				 * 
				 * } catch (IOException e1) { // TODO Auto-generated catch block
				 * e1.printStackTrace(); }
				 */
				System.exit(0);
			}
		});

		Dimension dim = new Dimension(700, 500); // 윈도우 창 크기
		frame.setPreferredSize(dim);
		frame.setTitle("데일리 채팅 프로그램");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.pack();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); // 모니터화면의 해상도 얻기

		// 프레임이 화면 중앙에 위치하도록 left, top 계산
		int left = (screen.width / 2) - (700 / 2);
		int top = (screen.height / 2) - (500 / 2);

		setLocation(left, top);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int Querycase;
		if (e.getSource() == addButton) { // 친구추가
			if (listText.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "친구 아이디를 입력해주세요.", "알림", JOptionPane.OK_OPTION);
				return;
			}

			sql = "SELECT * FROM User where user_id = '" + listText.getText() + "'";
			Querycase = 1;
			SQLQuery(Querycase, sql);
		}
		if (e.getSource() == delButton) { // 친구삭제
			if (list.getSelectedValue() != null) {
				JOptionPane.showMessageDialog(null, list.getSelectedValue() + "님이 친구목록에서 삭제되었습니다.");
				sql = "delete from chat_list where friend_id = '" + list.getSelectedValue() + "' and user_id = '"
						+ Link_id + "';";
				Querycase = 2;
				SQLQuery(Querycase, sql);
			}
		}
		if (e.getSource() == makeButton) { // 방 리스트 추가
			List friends = list.getSelectedValuesList();
			friends_size = (friends.size() + 1);
			// getSelectedValuesList() = 선택된 목록들 값 받아오기
			String temp = friends.toString().replace("[", "");
			temp = temp.replace("]", "");
			temp = temp.replace(" ", "");
			f_list = temp.split(",");

			sql = "insert into room(room_name,room_member) values('" + roomText.getText() + "'," + friends_size + ") ;";

			Querycase = 3;
			SQLQuery(Querycase, sql);
		}
	}

	void SQLQuery(int CASE, String sql) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "1234");
			Statement stmt = con.createStatement();
			ResultSet rs;

			if (CASE == 1) { // 친구 추가
				rs = stmt.executeQuery(sql);
				friend_id = "";
				friend_name = "";
				while (rs.next()) {
					if (!Link_id.equals(rs.getString("user_id"))) {

						friend_id = rs.getString("user_id");
						friend_name = rs.getString("user_name");
						for (int i = 0; i < listModel.getSize(); i++) {
							if (friend_id.equals(listModel.getElementAt(i))) {
								JOptionPane.showMessageDialog(null, "이미 등록된 친구입니다.");
								return;
							}
						}
					} else {
						JOptionPane.showMessageDialog(null, "니 아이디잖아");
						return;
					}
				}
				if (friend_id.equals("")) {
					JOptionPane.showMessageDialog(this, "아이디가 존재하지 않습니다.", "알림", JOptionPane.OK_OPTION);
				} else {
					sql = "insert into Chat_list value('" + Link_id + "','" + friend_id + "' , '" + friend_name + "');";
					stmt.executeUpdate(sql);
					// sql = "insert into Chat_list
					// value('"+friend_id+"','"+Link_id+"','"+Link_name+"');";
					listModel.addElement(friend_id);
					listText.setText("");
				}
			}

			if (CASE == 2) { // 친구삭제 이벤트
				stmt.executeUpdate(sql);
				listModel.removeElementAt(list.getSelectedIndex());
			}
			if (CASE == 3) {

				stmt.executeUpdate(sql);
				sql = "select max(room_index) as room_index from room;";
				rs = stmt.executeQuery(sql);
				if (rs.next()) { // 방의 마지막 index를 검색하여 r_index에 삽입
					r_index = (rs.getInt("room_index"));
				}

				StringBuffer sb = new StringBuffer(); // 채팅방에 들어갈 선택된 친구들을 채팅유저 테이블에 삽입
				for (int i = 0; i < f_list.length; i++) {
					sb.append("'" + f_list[i] + "'");
					if (i != f_list.length - 1)
						sb.append(" or user_id = ");
				}
				sql = "select user_id ,user_name from user where user_id =" + sb.toString() + " ;";

				rs = stmt.executeQuery(sql);
				sql = "INSERT into user_chat (user_id, user_name,chat_index) values(?, ?, ?);";

				java.sql.PreparedStatement ps = null;
				while (rs.next()) { // 친구 아이디
					ps = con.prepareStatement(sql);
					System.out.println(r_index);
					ps.setString(1, rs.getString("user_id"));
					ps.setString(2, rs.getString("user_name"));
					ps.setInt(3, r_index);
					ps.executeUpdate();
					ps.clearParameters();
				}
				ps = con.prepareStatement(sql);
				ps.setString(1, Link_id); // 자기 아이디
				ps.setString(2, Link_name);
				ps.setInt(3, r_index);
				ps.executeUpdate();
				ps.clearParameters();

			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
					true);

			printWriter.println("table:" + r_index + ":" + roomText.getText() + ":" + friends_size);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		roomText.setText("");
	}

	private class ChattingThread extends Thread {
		Socket socket = null;

		ChattingThread(Socket socket) {
			this.socket = socket;
		}

		public void run() { // 서버에서 받아온 메세지를 TextArea에 담아넣음
			try {

				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(url, "root", "1234");
				Statement stmt = con.createStatement();
				ResultSet rs;
				BufferedReader br = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

				while (true) { //
					String msg = br.readLine();
					String tokens[] = msg.split(":");
					System.out.println("chatList_Msg\t"+msg);
					if (tokens[0].equals("join")) {
						System.out.println(tokens[1] + "님이 접속 하셨습니다.");
						listModel2.addElement(tokens[1]);
					}
					else if(tokens[0].equals("bye")) {
						System.out.println(tokens[1]+"나감");
						for(int i=0; i<listModel2.size();i++) {
							System.out.println("getElementAt = "+listModel2.getElementAt(i));
							if(listModel2.getElementAt(i).equals(tokens[1]))
							{
								listModel2.remove(i);
							}
						}
					}
					else if (tokens[0].equals("table")) {
						System.out.println("msg = " + msg);
						System.out.println("tokens[1] = " + tokens[1]);
						sql = "select * from user_chat where user_id = '" + Link_id + "' and chat_index = '" + tokens[1]
								+ "' Limit 1 ;";
						rs = stmt.executeQuery(sql);
						Cells = 0;
						for(int i=0; i<tableCells.length;i++) {
							if(tableCells[i][0]== null) {
								Cells=i;
								break;
							}
						}
						System.out.println("Cells = "+Cells);
						while (rs.next()) { // 로그인 ID와 생성된 방 리스트 번호가 들어있을 때 UI방 생성
							System.out.println("테이블 전송 완료");
							/*
							 * tableCells[Cells][0] = tokens[1]; tableCells[Cells][1] = tokens[2];
							 * tableCells[Cells][2] = tokens[3];
							 */
							Vector row = new Vector();
							row.add(tokens[1]);
							row.add(tokens[2]);
							row.add(tokens[3]);
							model.insertRow(Cells, row);
							table.setValueAt(tokens[1], Cells, 0);
							table.setValueAt(tokens[2], Cells, 1);
							table.setValueAt(tokens[3], Cells, 2);
							System.out.println();
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	
}
