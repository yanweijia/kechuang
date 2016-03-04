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


@WebServlet("/changeHead")
public class changeHead extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
	
	
	
	
    public changeHead() {  super();   }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String head = "" + request.getParameter("head");
		//使用URLDecode对head解码,因为里面有/斜杠,写在一起容易出错
		head = java.net.URLDecoder.decode(head, "utf-8");
		
		if(uid==null || password==null || head==null){
			json.put("status", "fail");
			json.put("describe", "未传递正确的参数!");
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
				json.put("describe","密码错误,修改头像失败");
				response.getWriter().append(json.toString());
				return;
			}else{
				//存在结果,即uid与旧密码匹配,更新头像
				sql="update user_info set head='" + head + "' where uid=" + uid;
				int count = stmt.executeUpdate(sql);
				if(0==count){
					//如果受影响的行数为0,即更新失败
					json.put("status", "fail");
					json.put("describe","修改头像失败,数据库未改变");
					response.getWriter().append(json.toString());
					return;
				}else{
					//更新头像成功
					json.put("status", "success");
					json.put("uid", uid);
					response.getWriter().append(json.toString());
					return;
				}
			}	
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
