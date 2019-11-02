
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends JFrame implements Runnable {
	JLabel loginLogo1 = new JLabel(new ImageIcon("image/Login_Logo.png"));
	JLabel loginLogo2 = new JLabel(new ImageIcon("image/Login_Logo2.png"));

	JLabel id = new JLabel("ID");
	JLabel pw = new JLabel("PW");

	JButton loginBT = new JButton(new ImageIcon("image/Login_login.png"));
	JButton signupBT = new JButton(new ImageIcon("image/Login_signup.png"));

	JTextField idInput = new JTextField();// {@Override public void setBorder(Border border) {} };

	JTextField pwInput = new JTextField();// {@Override public void setBorder(Border border) {} };

	JLabel startView = new JLabel(new ImageIcon("image/effect.png"));
	JLabel bg = new JLabel(new ImageIcon("image/effect.png"));

	String url = "jdbc:mysql:///Daily?serverTimezone=Asia/Seoul";
	String name;

	Login() {
		Thread th = new Thread(this);
		th.start();
		loginLogo1.setBounds(76, 31, 168, 168);
		loginLogo2.setBounds(170, 2, 355, 288);

		loginBT.setBounds(220, 407, 105, 65);
		loginBT.setPressedIcon(new ImageIcon("image/Login_login1.png"));
		loginBT.setBorderPainted(false);
		loginBT.setContentAreaFilled(false);

		signupBT.setBounds(375, 407, 105, 65);
		signupBT.setPressedIcon(new ImageIcon("image/Login_singup1.png"));
		signupBT.setBorderPainted(false);
		signupBT.setContentAreaFilled(false);

		id.setBounds(176, 258, 50, 60);
		id.setFont(new Font("함초롬돋움", Font.BOLD, 39));
		pw.setBounds(176, 300, 80, 80);
		pw.setFont(new Font("함초롬돋움", Font.BOLD, 39));

		idInput.setBounds(250, 270, 300, 40);
		idInput.setFont(new Font("돋움", Font.BOLD, 20));
		pwInput.setBounds(250, 320, 300, 40);
		pwInput.setFont(new Font("돋움", Font.BOLD, 20));
		pwInput.addKeyListener(new EnterKey());

		startView.setBounds(0, 0, 700, 500);

		loginBT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoginCheck(idInput.getText(), pwInput.getText());
				// if(idInput.getText().equals("1234") && pwInput.getText().equals("1234"))
				// {
				// new Main();
				// dispose();
				// }

			}
		});

		signupBT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Join();
				dispose();

			}
		});

		bg.setBounds(0, 0, 700, 500);
		add(startView);
		add(bg);
		add(loginLogo1);
		add(loginLogo2);
		add(loginBT);
		add(signupBT);
		add(id);
		add(pw);
		add(idInput);
		add(pwInput);

		JButton b = new JButton();
		add(b);

		setMid();
		display();
	}

	// 실행화면 효과 함수
	public void run() {
		try {
			Thread.sleep(0);
			startView.setIcon(new ImageIcon("image/effect.png"));
			Thread.sleep(100);
			bg.setVisible(false);
			Thread.sleep(500);
			startView.setVisible(false);
		} catch (InterruptedException e) {
		}
	}

	void setMid() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); // 모니터화면의 해상도 얻기

		// 프레임이 화면 중앙에 위치하도록 left, top 계산
		int left = (screen.width / 2) - (700 / 2);
		int top = (screen.height / 2) - (500 / 2);

		setLocation(left, top);
	}

	void display() {
		this.getContentPane().setBackground(new Color(255, 255, 255));
		setTitle("로그인페이지");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		this.getRootPane().setPreferredSize(new Dimension(700, 500));// 프레임 크기 정확하게
		this.pack(); // - - - - - - - - - - - - - - - - - - -- - - 프레임 크기 정확하게
	}

	// 아이디, 비밀번호 받아서 DB에 있는지 체크하는 함수
	void LoginCheck(String id, String pw) {
		String idC = "", pwC = "";
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 드라이버 연결
			// ↓DB이름 나머지는 복붙 // DB연결
			con = DriverManager.getConnection(url, "root", "1234"); // url, ID, PW
			Statement stmt = con.createStatement();
			// 실행할 문장
			String sql = "SELECT * FROM User where user_id = '" + id + "'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				idC = rs.getString("user_id");
				pwC = rs.getString("user_pw");
				name = rs.getString("user_name");
			}
			if (!id.equals(idC)) // 아이디가 틀렸을 때
			{
				JOptionPane.showMessageDialog(this, "아이디가 존재하지 않습니다.", "알림", JOptionPane.OK_OPTION);
				// con.close();
				return;
			} else if (!pw.equals(pwC)) {
				JOptionPane.showMessageDialog(this, "비밀번호가 틀렸습니다.", "알림", JOptionPane.OK_OPTION);
				return;
			} else {
				sql = "select * from userCheck where Login_id = '" + idC + "';";
				rs = stmt.executeQuery(sql);
				String Check = "";

				while (rs.next()) {
					Check = rs.getString("Login_id");
				}
				if (Check.equals(idC)) {
					JOptionPane.showMessageDialog(this, "이미 접속된 아이디입니다.", "알림", JOptionPane.OK_OPTION);
				} else {
					new ChattingListForm(id, name);
					this.show();
					dispose();
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("드라이버를 찾을 수 없습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("연결에 실패하였습니다.");
		}
	}

	class EnterKey extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				LoginCheck(idInput.getText(), pwInput.getText());
		}
	}
}
