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
@WebServlet("/getFavorites")
public class getFavorites extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;

    public getFavorites() {    super();   }


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
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			
			sql="select uid,favorite_mid from user_favorite where uid=" + uid;
			rs = stmt.executeQuery(sql);
			JSONArray jsonArray = new JSONArray();

			
			json.put("status", "success");
			
			while(rs.next()){
				JSONObject jsonTemp = new JSONObject();
				jsonTemp.put("uid", rs.getString("uid"));
				jsonTemp.put("favorite_mid", rs.getString("favorite_mid"));
				jsonArray.add(jsonTemp);
			}
			json.put("favorites", jsonArray);
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
