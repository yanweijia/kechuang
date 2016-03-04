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
 * 测试用参数
 * ?uid=10016&password=passwordtest&name=严唯嘉&sex=男&qq=405102248&email=happyboyywj@163.com&phone=17091952844&place=上海&school=上海电力学院&about=喜欢结交朋友&interest=计算机&blog=www.yanweijia.cn&birthdate=19960927
 */

@WebServlet("/updateUserInfo")
public class updateUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Connection conn_user = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
	
	
	
    public updateUserInfo() {    super();    }

    //先验证密码是否正确,如果正确,则进行修改
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		
		String uid = request.getParameter("uid");
		String password = request.getParameter("password");
		String name = "" + request.getParameter("name");
		String sex = "" + request.getParameter("sex");
		String qq = "" + request.getParameter("qq");
		String email = "" + request.getParameter("email");
		String phone = "" + request.getParameter("phone");
		String place = "" + request.getParameter("place");
		String school = "" + request.getParameter("school");
		String about = "" + request.getParameter("about");
		String interest = "" + request.getParameter("interest");
		String blog = "" + request.getParameter("blog");
		String birthdate = "" + request.getParameter("birthdate");
		//初步检查参数是否正确
		boolean isSexCorrect = (sex.equals("未知") || sex.equals("男") || sex.equals("女"));
		if(uid==null || password==null || !isSexCorrect){
			json.put("status", "fail");
			json.put("describe", "参数传递错误,未输入uid或密码或性别错误");
			response.getWriter().append(json.toString());
			return;
		}
		
		//验证uid,密码是否正确,正确->更新数据,错误->提示并结束
		try {
			conn_user = DBHelper.getConnection_user();
			stmt = conn_user.createStatement();
			
			if(!DBHelper.isUidPwCorrect(uid, password)){
				//如果没有用户或密码出错
				//数据库中不存在该用户或密码错误
				json.put("status", "fail");
				json.put("describe","不存在该用户或密码错误!");
				response.getWriter().append(json.toString());
				return;
			}
			
			//正常,则更新数据
			sql = "update user_info set name='" + name + "',sex='" + sex + "',qq='" + qq + "',email='"
					+ email + "',phone='" + phone + "',place='" + place + "',school='" + school
					+ "',about='" + about + "',interest='" + interest + "',blog='" + blog + "',birthdate='"
					+ birthdate +"' "
					+ " where uid=" + uid;
			int count = stmt.executeUpdate(sql);
			//受影响的行数为0,即更新失败
			if(count == 0){
				json.put("status", "fail");
				json.put("describe", "未在数据库中找到该用户信息,更新失败!");
				response.getWriter().append(json.toString());
				return;
			}
			//更新正常
			json.put("status", "success");
			json.put("uid", uid);
			response.getWriter().append(json.toString());
			
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
