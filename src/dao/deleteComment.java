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
 * 测试参数
 *	正确数据:
 * ?uid=10016&password=passwordtest&cid=2
 *  错误数据:
 * ?uid=10016&password=passwordtest&cid=10
 */
@WebServlet("/deleteComment")
public class deleteComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
    public deleteComment() {  super();  }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String cid = request.getParameter("cid");
		
		//如果传递参数错误
		if(uid==null || password==null || cid==null){
			json.put("status", "fail");
			json.put("describe", "参数传递错误");
			response.getWriter().append(json.toString());
			return;
		}
		try{
			//获取数据库连接
			conn_user = DBHelper.getConnection_user();
			conn_message = DBHelper.getConnection_message();
			
			stmt=conn_user.createStatement();
			//判断用户名密码是否匹配
			if(!DBHelper.isUidPwCorrect(uid, password)){
				//如果没有结果,即不存在uid或uid与旧密码不匹配
				json.put("status", "fail");
				json.put("describe","密码错误");
				response.getWriter().append(json.toString());
				return;
			}
			//判断是否是uid用户发表的评论cid
			sql="select account.uid,comment.cid from user.account,message.comment where account.uid=comment.uid and comment.cid="+cid;
			rs = stmt.executeQuery(sql);
			//如果不村在cid或者不是该用户发表的评论
			if(!rs.next()){
				json.put("status", "fail");
				json.put("describe","不存在该评论或不是您发表的!");
				response.getWriter().append(json.toString());
				return;
			}
			//删除用户评论表
			sql="delete from user_comment where cid="+cid;
			stmt.executeUpdate(sql);
			//获取mid并删除消息评论表
			stmt = conn_message.createStatement();
			
			sql="select mid from msg_comment where cid="+cid;//获取mid
			rs = stmt.executeQuery(sql);
			rs.next();
			String mid = rs.getString("mid");
			
			sql="delete from msg_comment where cid="+cid;//删除消息评论表
			stmt.executeUpdate(sql);
			//删除评论
			sql="delete from comment where cid="+cid;
			stmt.executeUpdate(sql);
			//消息计数器-1
			sql="update msg_count set count_comment=(count_comment-1) where mid="+mid;
			stmt.executeUpdate(sql);
			
			
			json.put("status", "success");
			response.getWriter().append(json.toString());
			
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
