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


@WebServlet("/deleteFavorite")
public class deleteFavorite extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

    public deleteFavorite() {    super();   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String mid = request.getParameter("mid");
		
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
				json.put("describe","密码错误");
				response.getWriter().append(json.toString());
				return;
			}
			//存在结果,即uid与旧密码匹配,验证通过
			sql="select uid,mid from user_favorite where uid=" + uid + " and favorite_mid=" + mid;;
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				//如果没有结果,即用户未收藏过mid这个消息
				json.put("status", "fail");
				json.put("describe", "删除失败,未收藏过该消息!");
				response.getWriter().append(json.toString());
				return;
			}
			
			
			sql = "delete from user_favorite where uid=" + uid + " and favorite_mid=" + mid;
			int count = stmt.executeUpdate(sql);
			//受影响的行数为0,删除失败
			if(0 == count){
				json.put("status", "fail");
				json.put("describe", "删除失败,数据库未变动");
				response.getWriter().append(json.toString());
				return;
			}
			//如果没发生以上情况,删除成功
			json.put("status", "success");
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
