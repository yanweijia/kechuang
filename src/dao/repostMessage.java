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

/*
 * 测试用参数
 * ?uid=10016&password=passwordtest&mid=1&mcontent=转发消息测试
 */
@WebServlet("/repostMessage")
public class repostMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_message = null;
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

    public repostMessage() {  super();   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String messageMid = request.getParameter("mid");
		String mcontent = request.getParameter("mcontent");
		String mtype = "3";	//代码3代表repost
		String mstate = "0";
		String mtime = Tools.getNowTime();
		String mattach = "" + messageMid;
		
		//未获取到参数时
		if(uid==null || password==null || messageMid==null)
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
				json.put("describe", "用户名与密码不匹配,发送失败!");
				response.getWriter().append(json.toString());
				return;
			}
			//用户名和密码正确
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			sql = "insert into msg_info (uid,mcontent,mtype,mstate,mtime,mattach)values("
					+ uid + ",'" + mcontent + "'," + mtype + "," + mstate + ",'" + mtime
					+ "','" + mattach + "')";
			int count = stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
			//受影响的行数为0,发送失败
			if(count == 0){
				json.put("status", "fail");
				json.put("describe", "发送消息失败,数据库未做出应答!");
				response.getWriter().append(json.toString());
				return;
			}
			//没发生异常,则成功插入数据到消息表
			rs = stmt.getGeneratedKeys();
			rs.next();
			String newMid = rs.getString("GENERATED_KEY");
			
			//向消息计数表中插入一条新数据
			sql = "insert into msg_count (mid,count_view,count_like,count_comment,count_repost)values("
					+ newMid + ",0,0,0,0)";
			count = stmt.executeUpdate(sql);
			//插入计数数据失败
			if(count == 0){
				json.put("status", "fail");
				json.put("describe", "发送消息失败,数据库未做出应答!");
				//删除刚才插入的数据
				stmt.executeUpdate("delete from msg_info where mid=" + newMid);
				response.getWriter().append(json.toString());
				return;
			}
			
			//没有异常,两个表的数据都插入成功.
			json.put("status", "success");
			json.put("uid", uid);
			json.put("mid", newMid);
			response.getWriter().append(json.toString());
			
			//将被转发的消息计数器更新
			sql = "update msg_count set count_repost=(count_repost+1) where mid=" + messageMid;
			stmt.executeUpdate(sql);
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
