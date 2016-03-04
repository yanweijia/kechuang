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
import db.Tools;
import net.sf.json.JSONObject;


@WebServlet("/sendComment")
public class sendComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
	
	
    public sendComment() {   super();  }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String mid = request.getParameter("mid"); 
		String content = request.getParameter("content");
		String comment_time = Tools.getNowTime();
		//如果参数传递错误
		if(uid==null || content==null || mid==null){
			json.put("status", "fail");
			json.put("describe", "参数传递错误");
			response.getWriter().append(json.toString());
			return;
		}
		
		try{
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			//判断用户名密码是否匹配
			if(!DBHelper.isUidPwCorrect(uid, password)){
				//如果没有结果,即不存在uid或uid与旧密码不匹配
				json.put("status", "fail");
				json.put("describe","密码错误");
				response.getWriter().append(json.toString());
				return;
			}
			//身份确认成功,可以进行将数据插入到数据库中
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			sql = "insert into comment (uid,content,comment_time)values(" + uid + ",'" + content + "','" + comment_time +"')";
			int count = stmt.executeUpdate(sql);
			if(count==0){
				//受影响的行数为0,插入失败
				json.put("status", "fail");
				json.put("describe", "发表评论失败,数据库未做出应答!");
				response.getWriter().append(json.toString());
				return;
			}
			//插入数据到消息表中
			
			
			
			//插入数据到消息评论表
			
			
			//插入消息到用户评论表
			
			
			//消息计数表计数器自增
			
			
			
			
			
			
		}catch(Exception e){
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
