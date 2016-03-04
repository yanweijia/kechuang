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
 * ?mid=1
 */

@WebServlet("/getMessage")
public class getMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn_message = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	private String sql = null;
    

    public getMessage() {   super();  }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		
		
		String mid = request.getParameter("mid");
		if(mid==null){
			json.put("status", "fail");
			json.put("describe", "参数传递错误");
			response.getWriter().append(json.toString());
			return;
		}
		
		try {
			conn_message = DBHelper.getConnection_message();
			stmt = conn_message.createStatement();
			sql = "select uid,mcontent,mtype,mstate,mtime,mattach from msg_info where mid=" + mid;
			rs = stmt.executeQuery(sql);
			//如果没有结果,查询失败
			if(!rs.next()){
				json.put("status", "fail");
				json.put("describe", "未找到mid为" + mid + "的消息!");
				response.getWriter().append(json.toString());
				return;
			}
			//未发生上述情况,查询成功
			String uid = rs.getString("uid");
			String mcontent = rs.getString("mcontent");
			mcontent = java.net.URLEncoder.encode(mcontent, "utf-8");
			String mtype = rs.getString("mtype");
			int mstate = rs.getInt("mstate");
			String mtime = rs.getString("mtime");
			String mattach = rs.getString("mattach");
			switch(mstate){
			case 0://该消息状态正常
				json.put("status", "success");
				json.put("uid", uid);
				json.put("mcontent", mcontent);
				json.put("mtype",mtype);
				json.put("mtime", mtime);
				json.put("mattach",mattach);
				response.getWriter().append(json.toString());
				//消息被浏览数+1;
				sql = "update msg_count set count_view=(count_view+1) where mid=" + mid;
				stmt.executeUpdate(sql);
				return;
			case 1:
				json.put("describe", "此消息含有敏感词汇,被系统屏蔽");
				break;
			case 2:
				json.put("describe", "系统异常");
				break;
			case 3:
				json.put("describe", "此消息已经被主人删除啦~~");
				break;
			case 4:
				json.put("describe", "此消息被举报垃圾骚扰.");
				break;
			case 5:
				json.put("describe", "此消息被举报营销.");
				break;
			case 6:
				json.put("describe", "此消息被举报反动.");
				break;
			default:
					json.put("describe", "异常错误");
			
			}
			json.put("status", "fail");
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
