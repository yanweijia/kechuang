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
 * 测试数据:
 *  ?mid=1
 *  成功,返回结果示例:
 *  {"status":"success","comment_list":[{"name":"严唯嘉","comment_time":"2016-03-11 01:02:14.0","content":"这是评论","uid":"10016"}]}
 */
@WebServlet("/getComment")
public class getComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
    public getComment() {   super(); }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String mid = request.getParameter("mid");
		//判断参数是否正确
		if(mid==null){
			json.put("status", "fail");
			json.put("describe", "参数传递错误");
			response.getWriter().append(json.toString());
			return;
		}
		
		try{
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			//返回指定消息的评论格式信息如下
			sql="SELECT user_info.head,user_info.name,comment.comment_time,comment.content,comment.uid "
					+ "FROM user.user_info,message.comment,message.msg_comment "
					+ "WHERE msg_comment.cid=comment.cid AND comment.uid=user_info.uid AND msg_comment.mid=" + mid;
			/*
			 *	+------+--------+---------------------+----------+-------+
				| head | name   | comment_time        | content  | uid   |
				+------+--------+---------------------+----------+-------+
				| NULL | 严唯嘉         | 2016-03-11 01:02:14 | 这是评论           | 10016 |
				+------+--------+---------------------+----------+-------+
			 */
			
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				JSONObject temp = new JSONObject();
				temp.put("head", rs.getString("head"));
				temp.put("name", rs.getString("name"));
				temp.put("comment_time", rs.getString("comment_time"));
				temp.put("content", rs.getString("content"));
				temp.put("uid", rs.getString("uid"));
				jsonArray.add(temp);
			}
			//将结果写入到页面
			json.put("status", "success");
			json.put("comment_list",jsonArray);
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
