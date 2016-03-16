package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.DBHelper;
import net.sf.json.JSONObject;

/*
 * 测试数据
 * ?username=yanweijiatest&password=passwordtest
 */
@WebServlet("/login")
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

	public login() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		// 判断参数完整性
		if (username == null || password == null) {
			json.put("status", "fail");
			json.put("describe", "参数传递错误!");
			response.getWriter().append(json.toString());
			return;
		}
		try {
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			sql="select uid,username,pw,status from account where username='" + username + "' and pw='" + password + "'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				//如果没有结果,即不存在username或username与旧密码不匹配
				json.put("status", "fail");
				json.put("describe","用户名或密码不匹配");
				response.getWriter().append(json.toString());
				return;
			}
			//用户名密码正确
			String uid=rs.getString("uid");
			String status = rs.getString("status");
			if(!status.equals("ok")){
				//如果账户当前状态不正常
				json.put("status", "fail");
				json.put("describe","该用户账户存在异常,系统已经封禁该用户!");
				response.getWriter().append(json.toString());
				return;
			}
			//未发生上述异常,登录成功
			json.put("status", "success");
			json.put("uid", uid);
			response.getWriter().append(json.toString());
			return;
			
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
			json.put("status", "fail");
			json.put("describe","未知错误");
			response.getWriter().append(json.toString());
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
