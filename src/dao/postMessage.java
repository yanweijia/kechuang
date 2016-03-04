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
 * ?uid=10016&password=passwordtest&mcontent=%E8%BF%99%E6%98%AF%E4%B8%80%E6%9D%A1%E6%B5%8B%E8%AF%95%E6%B6%88%E6%81%AF%2C123456hello&mtype=0&mattach=
 */
@WebServlet("/postMessage")
public class postMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_message = null;
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

    public postMessage() {  super();   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String mcontent = "" + request.getParameter("mcontent");
		mcontent = java.net.URLDecoder.decode(mcontent, "utf-8");
		String mtype = request.getParameter("mtype");
		String mstate = "" + (Tools.isSensitive(mcontent)?1:0);
		String mtime = Tools.getNowTime();
		String mattach = "" + request.getParameter("mattach");
		
		//未获取到参数时
		if(uid==null || password==null)
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
			String mid = rs.getString("GENERATED_KEY");
			
			//向消息计数表中插入一条新数据
			sql = "insert into msg_count (mid,count_view,count_like,count_comment,count_repost)values("
					+ mid + ",0,0,0,0)";
			count = stmt.executeUpdate(sql);
			//插入计数数据失败
			if(count == 0){
				json.put("status", "fail");
				json.put("describe", "发送消息失败,数据库未做出应答!");
				//删除刚才插入的数据
				stmt.executeUpdate("delete from msg_info where mid=" + mid);
				response.getWriter().append(json.toString());
				return;
			}
			//没有异常,两个表的数据都插入成功.
			json.put("status", "success");
			json.put("uid", uid);
			json.put("mid", mid);
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
