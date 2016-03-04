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

/**
 * Servlet implementation class getUserInfo
 */
@WebServlet("/getDetailUserInfo")
public class getDetailUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
    
    public getDetailUserInfo() { super(); }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		JSONObject json = new JSONObject();
		if(uid==null || password==null){
			json.put("status", "fail");
			json.put("describe", "未传递正确的参数!");
			response.getWriter().append(json.toString());
			return;
		}
		
		try {
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			
			
			
			
			sql="select uid from account where uid=" + uid + " and pw='" + password + "'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				//如果没有用户或密码出错
				//数据库中不存在该用户或密码错误
				json.put("status", "fail");
				json.put("describe","不存在该用户或密码错误!");
				response.getWriter().append(json.toString());
				return;
			}
			
			//判断是否存在用户详细信息
			sql="select * from user_info where uid=" + uid;
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				//数据库中存在该用户详细信息
				String name,head,sex,qq,email,phone,place,school,about,interest,birthdate;
				name = ""+rs.getString("name");
				head = ""+rs.getString("head");
				sex = ""+rs.getString("sex");
				qq = ""+rs.getString("qq");
				email = ""+rs.getString("email");
				phone = ""+rs.getString("phone");
				place = ""+rs.getString("place");
				school = ""+rs.getString("school");
				about = ""+rs.getString("about");
				interest = ""+rs.getString("interest");
				birthdate = ""+rs.getString("birthdate");
				
				json.put("status", "success");
				json.put("name", name);
				json.put("head", head);
				json.put("sex", sex);
				json.put("qq", qq);
				json.put("email", email);
				json.put("phone",phone);
				json.put("place", place);
				json.put("school", school);
				json.put("about", about);
				json.put("interest", interest);
				json.put("birthdate", birthdate);
				
				response.getWriter().append(json.toString());
			}else{
				//数据库中不存在该用户详细信息
				json.put("status", "fail");
				json.put("describe","没有uid为"+uid+"的用户详细信息");
				response.getWriter().append(json.toString());
			}
			
			
		} catch (Exception e) {
			json.clear();
			json.put("status", "fail");
			json.put("status", "服务器异常");
			response.getWriter().append(json.toString());
			e.printStackTrace();
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
