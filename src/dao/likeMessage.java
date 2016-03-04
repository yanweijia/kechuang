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
 * ?uid=10016&password=passwordtest&mid=1
 */
@WebServlet("/likeMessage")
public class likeMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

    public likeMessage() {    super();   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String mid = request.getParameter("mid");
		//判断参数完整性
		if(uid==null || password==null || mid==null){
			json.put("status", "fail");
			json.put("describe", "参数传递错误!");
			response.getWriter().append(json.toString());
			return;
		}
		
		try {
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			sql="select uid,pw from account where uid=" + uid + " and pw='" + password + "'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				//如果没有结果,即不存在uid或uid与旧密码不匹配
				json.put("status", "fail");
				json.put("describe","密码错误,点赞失败");
				response.getWriter().append(json.toString());
				return;
			}
			//用户是否对该消息点过赞,默认没点过
			boolean isFirstLike = true;
			
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			sql = "select uid,mid from likelist where uid=" + uid + " and mid=" + mid;
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				//没有数据就点赞,更新点赞列表并计数器自增;
				sql = "insert into likelist (uid,mid)values(" + uid + "," + mid + ")";
				stmt.executeUpdate(sql);
				sql = "update msg_count set count_like=(count_like+1) where mid=" + mid;
				stmt.executeUpdate(sql);
				isFirstLike = true;
			}else{
				//该用户已经赞过该微博,则取消点赞列表并计数器减1
				sql = "delete from likelist where uid=" + uid + " and mid=" + mid;
				stmt.executeUpdate(sql);
				sql = "update msg_count set count_like=(count_like-1) where mid=" + mid;
				stmt.executeUpdate(sql);
				isFirstLike = false;
			}
			json.put("status", "success");
			json.put("isFirstLike", isFirstLike);
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


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
