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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 测试数据
 * ?uid=10016&password=passwordtest
 */
@WebServlet("/getHomepage")
public class getHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private ResultSet temp_rs = null;
	private String sql = null;

    public getHomePage() {    super();   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");

		//判断参数完整性
		if(uid==null || password==null){
			json.put("status", "fail");
			json.put("describe", "参数传递错误!");
			response.getWriter().append(json.toString());
			return;
		}
		
		try {
			conn_message = DBHelper.getConnection_message();
			conn_user = DBHelper.getConnection_user();
			stmt = conn_message.createStatement();
			Statement stmt_msgTemp =  DBHelper.getNewConnection_message().createStatement();
			
			
			//TODO:如果做得完善的话,这里的应该查的是uid关注的用的的用户所post的消息,而不是大众homepage
			
			sql = "SELECT mid,uid,mcontent,mtype,mstate,mtime,mattach FROM msg_info ORDER BY mtime DESC LIMIT 0,50";
			rs = stmt.executeQuery(sql);
			JSONArray jsonArray = new JSONArray();
			
			while(rs.next()){	//注意:在循环中不能再用stmt来执行更新,否则之前的ResultSet会被关闭
				JSONObject jsonTemp = new JSONObject();
				String temp_mid = "" + rs.getString("mid");
				String temp_uid = "" + rs.getString("uid");
				String temp_mcontent = "" + rs.getString("mcontent");
				String temp_mtype = "" + rs.getString("mtype");
				String temp_mstate = "" + rs.getString("mstate");
				String temp_mtime = "" + rs.getString("mtime");
				String temp_mattach = "" + rs.getString("mattach");
				
				//将被拉消息的被浏览数+1
				sql = "update msg_count set count_view=(count_view+1) where mid=" + temp_mid;
				stmt_msgTemp.executeUpdate(sql);
				
				//获取指定消息的被浏览数,被转发数,评论数,点赞数等
				sql = "SELECT count_view,count_like,count_comment,count_repost FROM msg_count WHERE mid=" + temp_mid;
				temp_rs = stmt_msgTemp.executeQuery(sql);
				temp_rs.next();
				String count_view = "" + temp_rs.getString("count_view");
				String count_like = "" + temp_rs.getString("count_like");
				String count_comment = "" + temp_rs.getString("count_comment");
				String count_repost = "" + temp_rs.getString("count_repost");
				
				//获取发消息的人的head和name
				Statement stmt_user = conn_user.createStatement();
				sql = "SELECT name,head FROM user_info WHERE uid=" + temp_uid;
				temp_rs = stmt_user.executeQuery(sql);
				temp_rs.next();
				String temp_name = "" + temp_rs.getString("name");
				String temp_head = "" + temp_rs.getString("head");
				
				//获取该消息是否被favorite过
				sql = "SELECT uid,favorite_mid FROM user_favorite WHERE uid=" + temp_uid + " AND favorite_mid=" + temp_mid;
				temp_rs = stmt_user.executeQuery(sql);
				boolean isFavorited = temp_rs.next();
					
				
				jsonTemp.put("mid", temp_mid);
				jsonTemp.put("uid",temp_uid);
				jsonTemp.put("mcontent", temp_mcontent);
				jsonTemp.put("mtype", temp_mtype);
				jsonTemp.put("mstate", temp_mstate);
				jsonTemp.put("mtime", temp_mtime);
				jsonTemp.put("mattach", temp_mattach);
				jsonTemp.put("count_view", count_view);
				jsonTemp.put("count_like", count_like);
				jsonTemp.put("count_comment", count_comment);
				jsonTemp.put("count_repost", count_repost);
				jsonTemp.put("name", temp_name);
				jsonTemp.put("head", temp_head);
				jsonTemp.put("isFavorited", isFavorited);
				
				jsonArray.add(jsonTemp);
			}
			json.put("status", "success");
			json.put("homePage", jsonArray);
			response.getWriter().append(json.toString());
			return;

			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			json.put("status", "fail");
			json.put("describe","未知错误");
			response.getWriter().append(json.toString());
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
