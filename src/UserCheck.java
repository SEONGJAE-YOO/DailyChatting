import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserCheck {
	Connection con=null;
	Statement stmt;
	ResultSet rs;
	String name;
	String sql;
	public UserCheck() {
		
	}
	public void Check_insert(String name) {
		sql = "insert into userCheck values('"+name+"','대기');";
		Queryexecute(sql);
	}
	public void Check_delete(String name) {
		sql = "delete from userCheck where Login_id = '"+name+"';";
		Queryexecute(sql);
	}
	private void Queryexecute(String sql) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql:///Daily?serverTimezone=Asia/Seoul","root","jsppass");
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
