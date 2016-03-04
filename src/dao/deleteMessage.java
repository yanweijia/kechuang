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
 * 测试用参数
 * ?uid=10016&password=passwordtest&mid=2
 */
@WebServlet("/deleteMessage")
public class deleteMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_message = null;
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

    public deleteMessage() {  super();   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String mid = request.getParameter("mid");
		//未获取到参数时
		if(uid==null || password==null || mid==null)
		{
			json.put("status", "fail");
			json.put("describe", "参数传递错误");
			response.getWriter().append(json.toString());
			return;
		}
		try {
			//先判断uid与密码是否正确
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			sql = "select uid from account where uid=" + uid + " and pw='" + password + "'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				//如果没有结果,即uid与密码不匹配
				json.put("status", "fail");
				json.put("describe", "用户名与密码不匹配,删除消息失败!");
				response.getWriter().append(json.toString());
				return;
			}
			//用户名和密码正确
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			sql = "delete from msg_info where mid=" + mid;
			int count = stmt.executeUpdate(sql);
			//受影响的行数为0,删除
			if(count == 0){
				json.put("status", "fail");
				json.put("describe", "删除消息失败,数据库未做出应答!");
				response.getWriter().append(json.toString());
				return;
			}
			//删除同时删除编号为mid的消息计数字段
			sql = "delete from msg_count where mid=" + mid;
			stmt.executeUpdate(sql);
			sql = "delete from likelist where mid=" + mid;
			stmt.executeUpdate(sql);
			sql = "delete from favorite where mid=" + mid;
			stmt.executeUpdate(sql);
			
			
			//没发生异常,则成功
			json.put("status", "success");
			response.getWriter().append(json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			json.put("status", "fail");
			json.put("describe", "未知错误,服务器数据库异常!");
			response.getWriter().append(json.toString());
			return;
		}
		
	}

	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
