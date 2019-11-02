
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChattingForm extends JFrame {

   Color color;
   Color color2;

   private JFrame frame;
   private JPanel panel;
   private JPanel textpanel;
   private JLabel label;
   private JLabel label2;
   private JButton buttonSend;
   private JTextField textField;
   private JTextArea textArea;
   private JScrollPane scrollpane;

   Connection con = null;
   Statement stmt;
   PreparedStatement ps;
   ResultSet rs;

   String url = "jdbc:mysql:///Daily?serverTimezone=Asia/Seoul";
   String sql;

   Socket socket;
   private String Link_id;
   private String Link_name;
   String index;
   String title;
   String[] FriendList;

   PrintWriter printWriter;

   public ChattingForm(String index, String title, String id, String name, Socket socket) {
	   this.socket = socket;
	      this.Link_id = id;
	      this.Link_name = name;
	      this.title = title;
	      this.index = index;
	      String member ="";
	   try {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(url,"root","jsppass");
		stmt = con.createStatement();
		sql="select user_name from user_chat where chat_index = "+index+";";
		rs = stmt.executeQuery(sql);
		while(rs.next()) {
			member += rs.getString("user_name")+" ";
		}
	} catch (ClassNotFoundException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	   
      color = new Color(243, 218, 232); // 위 아래 색깔 (바꿔도 돼요)
      color2 = new Color(255, 250, 251); // 채팅창 바탕 색깔 (바꿔도 돼요)

      frame = new JFrame();
      panel = new JPanel();
      textpanel = new JPanel();
      buttonSend = new JButton("전송");
      textField = new JTextField();
      label = new JLabel(title, JLabel.CENTER);
      label2 = new JLabel(member, JLabel.CENTER); // 맴버
      textArea = new JTextArea(30, 80);

      

      show();
      new ChatFormThread(socket).start(); // 클라이언트 스레드 시작

      try {
         printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
               true);
         printWriter.println("chatting-join:" + index + ":" + Link_name);
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

      buttonSend.addActionListener(new ActionListener() {
         
         @Override
         public void actionPerformed(ActionEvent e) {
            inputEvent();
         }
      });
      textField.addKeyListener(new KeyListener() {

         @Override
         public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub

         }

         @Override
         public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub

         }

         @Override
         public void keyPressed(KeyEvent e) {
            // TODO Auto-generated method stub
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
            inputEvent();

         }
      });

   }

   public void show() {
      // Button
      buttonSend.setBackground(new Color(209, 82, 119));
      buttonSend.setFont(new Font("맑은 고딕", Font.BOLD, 15));
      buttonSend.setForeground(Color.WHITE);

      // label (방 제목)
//      label.setText("방 제목");
      label.setFont(new Font("맑은 고딕", Font.BOLD, 25));
      label.setBackground(color);
      label.setPreferredSize(new Dimension(700, 30));
      textpanel.add(label, BorderLayout.NORTH);

      // label (참여자)
      label2.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
      label2.setBackground(color);
      label2.setPreferredSize(new Dimension(700, 30));
      textpanel.add(label2, BorderLayout.SOUTH);

      textpanel.setPreferredSize(new Dimension(700, 70));
      textpanel.setBackground(color);
      frame.add(BorderLayout.NORTH, textpanel);

      // Textfield
      textField.setColumns(30);
      textField.setFont(new Font("맑은 고딕", Font.BOLD, 15));

      // Pannel
      panel.setBackground(color);
      panel.add(textField);
      panel.add(buttonSend);
      frame.add(BorderLayout.SOUTH, panel);

      // TextArea
      textArea.setEditable(false);
      textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
      textArea.setBackground(color2);
      textArea.setLineWrap(true);
      scrollpane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      frame.add(BorderLayout.CENTER, scrollpane);

      // frame
      frame.addWindowListener(new WindowAdapter() { // 창 x키 누르면 닫히는거
         public void windowClosing(WindowEvent e) {
            try {
               printWriter = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
               String request = "quit:"+index;
               printWriter.println(request);
               dispose();
            } catch (IOException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }

         }
      });

      Dimension dim = new Dimension(700, 500); // 윈도우 창 크기
      frame.setPreferredSize(dim);
      frame.setTitle("Daily Chatting Program");
      frame.setVisible(true);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      frame.setResizable(false);
      frame.pack();
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); // 모니터화면의 해상도 얻기

      setDefaultCloseOperation(DISPOSE_ON_CLOSE);

      // 프레임이 화면 중앙에 위치하도록 left, top 계산
      int left = (screen.width / 2) - (700 / 2);
      int top = (screen.height / 2) - (500 / 2);

      setLocation(left, top);

      try {
         Class.forName("com.mysql.jdbc.Driver");
         con = DriverManager.getConnection(url,"root","jsppass");
         Statement stmt = con.createStatement();

         sql = "select user_name,user_text from chatting where chatting_index = " + index + " ;";
         rs = stmt.executeQuery(sql);

         while (rs.next()) {
            textArea.append(rs.getString("user_name") + ":" + rs.getString("user_text"));
            textArea.append("\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void inputEvent() {
      try {
         Class.forName("com.mysql.jdbc.Driver");
         con = DriverManager.getConnection(url,"root","jsppass");
         Statement stmt = con.createStatement();
         printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
               true);
         if (textField.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "채팅칸이 빈칸입니다.");
         } else {
            printWriter.println("sendMassage:" + index + ":" + textField.getText());
            sql = "insert into chatting values(" + index + ",'" + Link_name + "','" + textField.getText() + "')";
            stmt.executeUpdate(sql);

            textField.setText("");
         }
      } catch (ClassNotFoundException e2) {
         // TODO Auto-generated catch block
         e2.printStackTrace();
      } catch (SQLException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private class ChatFormThread extends Thread {
      Socket socket;

      public ChatFormThread(Socket socket) {
         // TODO Auto-generated constructor stub
         this.socket = socket;
      }

      public void run() {
         try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,"root","jsppass");
            stmt = con.createStatement();

            BufferedReader br = new BufferedReader(
                  new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            while (true) {
               String msg = br.readLine();
               String tokens[] = msg.split(":");
               System.out.println("chatting message : " + msg);
               if (tokens[1].equals(index)) {
                  if (tokens[0].equals("quit")) {
                     textArea.append(tokens[2]+"\n");
                  }
                  else if (tokens[0].equals("chatjoin")) {
                     textArea.append(tokens[2] + "님이 채팅방에 참가하셨습니다.\n");
                  }
                  else if (tokens[0].equals("sendMassage")) {
                     System.out.println(tokens[2]+"/"+tokens[3]);
                     textArea.append(tokens[2] + ":" + tokens[3]+"\n");
                     textArea.setCaretPosition(textArea.getDocument().getLength());
                  }
               }
            }
         } catch (IOException | ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

}