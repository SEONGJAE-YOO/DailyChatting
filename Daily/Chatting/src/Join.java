
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.event.*;
import java.sql.*;

public class Join extends JFrame{  
	JLabel sideLa = new JLabel(new ImageIcon("image/leftside.png"));
	JLabel pageName = new JLabel("");
	
	JLabel name = new JLabel("이름");
	JTextField nameF = new JTextField(){@Override public void setBorder(Border border) {} };
	
	JLabel id = new JLabel("ID");
	JTextField idF = new JTextField(){@Override public void setBorder(Border border) {} };
	
	JLabel pw = new JLabel("PW");  
	JTextField pwF = new JTextField(){@Override public void setBorder(Border border) {} };
	
	JLabel pw2 = new JLabel("PW");
	JTextField pw2F = new JTextField(){@Override public void setBorder(Border border) {} };
	
	JLabel pwc = new JLabel("check");
	JTextField pwcF = new JTextField(){@Override public void setBorder(Border border) {} };
	
	JTextArea terms = new JTextArea(){@Override public void setBorder(Border border) {} };

	JLabel termsL = new JLabel("이용 약관에 동의합니다.");
	JCheckBox termsC = new JCheckBox();
	
	JButton joinBT = new JButton("OK");
	JButton cancleBT = new JButton("취소");
	
	String termsStr = new String("제1조(목적)\r\n" + 
			"이 약관은 Pay B&W(이하 \"회사\"라고 함)가 제공하는 게임 및 제반 서비스의 이용과 관련하여 \r\n" + 
			"회원과 회사 간의 조건 및 절차에 관한 기본적인 사항을 정함을 목적으로 합니다.\r\n" + 
			"제2조(개인정보의 보호 및 사용)\r\n" + 
			"① 회사는 관계법령이 정하는 바에 따라 이용자 등록정보를 포함한 이용자의 개인 정보를 보\r\n" + 
			"호하기 위해 노력합니다. 이용자의 개인 정보 보호 및 사용에 대해서는 관련 법령 및 회사의 \r\n" + 
			"개인정보처리방침이 적용됩니다. 단, 회사의 공식 사이트 이외의 링크된 사이트에서는 회사의 \r\n" + 
			"개인정보처리방침이 적용되지 않습니다.\r\n" + 
			"② 회사는 이용자의 귀책 사유로 인해 노출된 이용자의 계정 정보를 비롯한 모든 정보에 대해\r\n" + 
			"서 일체의 책임을 지지 않습니다\r\n" + 
			"제3조(약관의 규정 외 사항에 관한 준칙)\r\n" + 
			"이 약관에 규정 되지 않은 사항과 이 약관의 해석에 대해서는 회사가 정한 개별 서비스 이용약\r\n" + 
			"관, 운영정책 및 관계법령이 적용됩니다.");
	String url = "jdbc:mysql:///Daily?serverTimezone=Asia/Seoul";
	String sql;
	Join()
	{
		sideLa.setBounds(10,10,250,480);
		pageName.setBounds(43,45,190,57);
		pageName.setFont(new Font("함초롬돋움",Font.PLAIN,45));
		pageName.setForeground(Color.white);
		
		name.setBounds(275,1,90,50);
		name.setFont(new Font("함초롬돋움",Font.PLAIN,38));
		nameF.setBounds(365,10,320,40);
		nameF.setBackground(new Color(235,235,235));
		nameF.setFont(new Font("함초롬돋움",Font.PLAIN,36));
		
		id.setBounds(275,59,60,35);
		id.setFont(new Font("함초롬돋움",Font.PLAIN,38));
		idF.setBounds(330,60,355,40);
		idF.setBackground(new Color(235,235,235));
		idF.setFont(new Font("함초롬돋움",Font.PLAIN,36));
		
		pw.setBounds(275,112,80,30);
		pw.setFont(new Font("함초롬돋움",Font.PLAIN,38));
		pwF.setBounds(355,110,330,40);
		pwF.setBackground(new Color(235,235,235));
		pwF.setFont(new Font("함초롬돋움",Font.PLAIN,36));
		
		pw2.setBounds(275,150,90,50);
		pw2.setFont(new Font("함초롬돋움",Font.PLAIN,38));
		pw2F.setBounds(365,160,320,40);
		pw2F.setBackground(new Color(235,235,235));
		pw2F.setFont(new Font("함초롬돋움",Font.PLAIN,36));
		
		pwc.setBounds(330,180,30,14);
		pwc.setFont(new Font("함초롬돋움",Font.PLAIN,10));
		
		terms.setBounds(275,210,410,210);
		terms.setFont(new Font("함초롬돋움",Font.PLAIN,12));
		terms.setBackground(new Color(230,230,230));
		terms.append(termsStr);
		terms.setEnabled(false);
		
		termsL.setBounds(557,423,116,13);
		termsL.setFont(new Font("함초롬돋움",Font.PLAIN,10));
		
		termsC.setBounds(668,421,17,14);
		termsC.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) joinBT.setEnabled(true);
				else joinBT.setEnabled(false);
			}
		});
		
		cancleBT.setBounds(535,445,70,45);
		cancleBT.setFont(new Font("함초롬돋움",Font.BOLD,15));
		cancleBT.addActionListener(new JoinAction());
		
		joinBT.setBounds(615,445,70,45);
		joinBT.setFont(new Font("함초롬돋움",Font.BOLD,15));
		joinBT.addActionListener(new JoinAction());
		joinBT.setEnabled(false);
		
		add(pageName);
		add(sideLa);
		add(name);
		add(nameF);
		add(id);
		add(idF);
		add(pw);
		add(pwF);
		add(pw2);
		add(pw2F);
		add(pwc);
		add(terms);
		add(termsL);
		add(termsC);
		add(joinBT);
		add(cancleBT);
		add(new JLabel());
		
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(url,"root","1234");
		Statement stmt = con.createStatement();
		sql= "create table if not exists user("
				+"user_id char(15) primary key,"
			    +"user_name varchar(16),"
			    +"user_pw varchar(16)"
			+")engine innoDB DEFAULT CHARSET=utf8;";
		stmt.executeUpdate(sql);
		sql = "create table if not exists room("
				+"room_index int primary key auto_increment,"
			    +"room_name varchar(20),"
			    +"room_member int"
			+")engine innoDB DEFAULT CHARSET=utf8; ";
		stmt.executeUpdate(sql);
		sql = "create table if not exists user_chat("
				+"chat_index int ,"
			    +"user_id char(15),"
			    +"user_name varchar(16),"
			    +"foreign key (chat_index) references room (room_index)"
			+")engine innoDB DEFAULT CHARSET=utf8; "; 
		stmt.executeUpdate(sql);
		sql = "create table if not exists chat_list("
				+"user_id char(15),"
			    +"friend_id char(15),"
				+"friend_name varchar(16),"
			    +"foreign key(user_id) references User(user_id)"
			+")engine innoDB DEFAULT CHARSET=utf8;";
		stmt.executeUpdate(sql);
		}
		catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		setMid();
		display();
	}
	void setMid()
	{
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); // 모니터화면의 해상도 얻기
		
		// 프레임이 화면 중앙에 위치하도록 left, top 계산
		int left = (screen.width / 2) - (700 / 2);
		int top = (screen.height / 2) - (500 / 2);

		setLocation(left,top);
	}
	void display()
	{
		this.getContentPane().setBackground(new Color(255,255,255));
		setTitle("회원가입");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		this.getRootPane().setPreferredSize(new Dimension(700,500));//프레임 크기 정확하게
		this.pack();	//- - - - - - -  - - - - - - - - - - - -- - - 프레임 크기 정확하게
	}
	void showMessage_(int a)
	{
		if(a == 0)JOptionPane.showMessageDialog(this,"회원가입 성공!");
		else JOptionPane.showMessageDialog(this,"회원가입에 실패했습니다.");
	}
	class JoinAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == joinBT)
			{   
				//입력 유무 체크
				if(nameF.getText().equals(""))JOptionPane.showMessageDialog(null,"이름을 입력해주세요."); //비어있으면
				else if(idF.getText().equals("")) JOptionPane.showMessageDialog(null,"ID를 입력해주세요."); //비어있으면
				else if(pwF.getText().equals(""))JOptionPane.showMessageDialog(null,"비밀번호를 입력해주세요."); //비어있으면
				else if(!pwF.getText().equals(pw2F.getText()))JOptionPane.showMessageDialog(null,"비밀번호가 일치하지 않습니다."); //비밀번호 불일치
				
				else
				{
					Connection con = null;
					try {   
						String equl = "";
						Class.forName("com.mysql.jdbc.Driver"); // 드라이버 연결
						//                                         ↓DB이름  나머지는 복붙
						 // DB연결
						con = DriverManager.getConnection(url, "root", "1234"); //url, ID, PW
						Statement stmt = con.createStatement();   
						
						sql = "select user_id from User where user_id ='"+idF.getText()+"';";
						
						ResultSet rs = stmt.executeQuery(sql);
						int row=0;
						while (rs.next()) {
							row++;
							break;
						}
						
						if(row!=0) {
							JOptionPane.showMessageDialog(null,"이미 존재하는 ID 입니다.");
						}
						else {
						sql = "insert into User(user_id, user_pw, user_name) values ('" 
								+ idF.getText() +"','" + pwF.getText() +"','"+ nameF.getText() +"');";
						int i = stmt.executeUpdate(sql);
						if (i == 1)
						{
							showMessage_(0);
							new Login();
							dispose();
						}  
						else
							showMessage_(1);
						}
				
					} catch (ClassNotFoundException ex) {
						ex.printStackTrace();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
				
			}
			if(e.getSource() == cancleBT)
			{
				new Login();
				dispose();
			}
		}
		
	}
}
