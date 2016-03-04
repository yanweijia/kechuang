package dao;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DBHelper;
import net.sf.json.JSONObject;


@WebServlet("/register")
public class register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
	
	
    public register() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		JSONObject json = new JSONObject();
		if(username==null || password==null || name==null){
			json.put("status", "fail");
			json.put("describe", "服务器故障");
			response.getWriter().append(json.toString());
			return;
		}
		try {
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			//判断是否存在用户了
			sql="select username from account where username='" + username + "'";
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				//数据库中已经有该用户了
				json.put("status", "fail");
				json.put("describe", "已经有该用户名,请换一个用户名");
				response.getWriter().append(json.toString());
			}else{
				//数据库中不存在此人
				//插入数据
				sql="insert into account (username,pw)values('" + username + "','" + password +"')";
				stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
				rs = stmt.getGeneratedKeys();
				rs.next();
				String uid = rs.getString("GENERATED_KEY");	//这里的结果集里面只有一列,表头为自动生成列GENERATED_KEY
				json.put("status", "success");
				json.put("uid", uid);
				json.put("username", username);
				
				//向用户信息表中插入数据
				
				sql="insert into user_info (uid,name)values(" + uid + ",'" + name + "')";
				stmt.executeUpdate(sql);
				json.put("name", name);

				response.getWriter().append(json.toString());
			}
			
			
		} catch (Exception e) {
			json.clear();
			json.put("status", "fail");
			json.put("status", "服务器异常");
			response.getWriter().append(json.toString());
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
