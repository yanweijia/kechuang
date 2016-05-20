package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBHelper {
   
	private static final String driver = "com.mysql.jdbc.Driver"; //数据库驱动
	//连接数据库的URL地址
	private static final String url_user="jdbc:mysql://localhost:3306/user?useUnicode=true&characterEncoding=UTF-8"; 
	private static final String url_message="jdbc:mysql://localhost:3306/message?useUnicode=true&characterEncoding=UTF-8";
	private static final String username="root";//数据库的用户名
	private static final String password="";//数据库的密码
    
	private static Connection conn_user=null;
	private static Connection conn_message=null;
	
	//静态代码块负责加载驱动
	static 
	{
		try
		{
			Class.forName(driver);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//单例模式返回数据库连接对象
	public static Connection getConnection_user() throws Exception
	{
		if(conn_user==null)
		{
			conn_user = DriverManager.getConnection(url_user, username, password);
			return conn_user;
		}
		return conn_user;
	}
	public static Connection getConnection_message() throws Exception
	{
		if(conn_message==null)
		{
			conn_message = DriverManager.getConnection(url_message,username,password);
			return conn_message;
		}
		return conn_message;
	}
	//返回一个新的链接
	public static Connection getNewConnection_message() throws Exception{
		return DriverManager.getConnection(url_message,username,password);
	}
	public static Connection getNewConnection_user() throws Exception{
		return DriverManager.getConnection(url_user,username,password);
	}
	public static boolean isUidPwCorrect(String uid,String password){
		try {
			conn_user = DBHelper.getConnection_user();
			Statement stmt = conn_user.createStatement();
			String sql = "select uid,pw from account where uid=" + uid + " and pw='" + password + "'";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				//如果存在结果,即uid与密码匹配
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
		
		
		return false;
	}
}
