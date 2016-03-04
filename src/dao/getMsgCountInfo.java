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

@WebServlet("/getMsgCountInfo")
public class getMsgCountInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
	
	
	
    public getMsgCountInfo() {   super(); }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		String mid = request.getParameter("mid");
		if(mid==null){
			json.put("status", "fail");
			json.put("describe", "参数传递错误!");
			response.getWriter().append(json.toString());
			return;
		}
		try {
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			sql = "select count_view,count_like,count_comment,count_repost from msg_count where mid=" + mid;
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				//如果无数据
				json.put("status", "fail");
				json.put("describe", "数据库无响应");
				response.getWriter().append(json.toString());
				return;
			}
			//若返回正常结果
			String count_view = rs.getString("count_view");
			String count_like = rs.getString("count_like");
			String count_comment = rs.getString("count_comment");
			String count_repost = rs.getString("count_repost");
			
			json.put("status", "success");
			json.put("count_view",count_view);
			json.put("count_like", count_like);
			json.put("count_comment", count_comment);
			json.put("count_repost", count_repost);
			response.getWriter().append(json.toString());
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			json.put("status", "fail");
			json.put("describe","未知错误");
			response.getWriter().append(json.toString());
			return;
		}
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
