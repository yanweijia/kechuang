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
@WebServlet("/favoriteMessage")
public class favoriteMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

    public favoriteMessage() {    super();   }


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
				json.put("describe","密码错误,收藏失败");
				response.getWriter().append(json.toString());
				return;
			}
			
			
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			sql = "select uid,mid from favoritelist where uid=" + uid + " and mid=" + mid;
			rs = stmt.executeQuery(sql);
			//用户是否收藏过该消息,如果收藏过消息,则提示已经收藏过
			if(!rs.next()){
				//没有收藏过就进行收藏
				sql = "insert into favoritelist (uid,mid)values(" + uid + "," + mid + ")";
				stmt.executeUpdate(sql);
				json.put("status", "success");
				response.getWriter().append(json.toString());
				return;
			}else{
				//该用户已经收藏过该消息
				json.put("status", "fail");
				json.put("describe", "收藏失败,您已收藏过该消息!");
				response.getWriter().append(json.toString());
				return;
			}
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
