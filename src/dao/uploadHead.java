package dao;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.apache.tomcat.util.http.fileupload.*;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import net.sf.json.JSONObject;

@WebServlet("/uploadHead")
public class uploadHead extends HttpServlet {
	private static final long serialVersionUID = 1L;
    List piclist = new ArrayList();	//放上传的图片名
    public uploadHead() {  super();  }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("contentType", "text/html;charset=utf-8");
		JSONObject json = new JSONObject();
		String path=request.getRealPath("/heads");
        
        DiskFileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload sfu=new ServletFileUpload(factory);
        sfu.setHeaderEncoding("UTF-8");  //处理中文问题
        sfu.setSizeMax(1024*1024);   //限制文件大小
        
        try {
            List<FileItem> fileItems= sfu.parseRequest(new ServletRequestContext(request));  //解码请求 得到所有表单元素
            for (FileItem fi : fileItems) {
                //有可能是 文件，也可能是普通文字 
                if (fi.isFormField()) { //这个选项是 文字 
                    System.out.println("表单值为："+fi.getString());
                }else{
                    // 是文件
                    String fn=fi.getName();
                    System.out.println("文件名是："+fn);  //文件名 
                    // fn 是可能是这样的 c:\abc\de\tt\fish.jpg
                    //path为目录,fn为文件名
                    //TODO:修改fn为当前时间戳+jpg
                    fi.write(new File(path,fn));
                    
                    if (fn.endsWith(".jpg")||true/*只要是文件就先保存*/) {
                        piclist.add(fn);  //把图片放入集合
                    }
                    
                    
                    json.put("status", "success");
                    json.put("head", "heads/" + fn);
                    response.getWriter().append(json.toString());
                    
                }                
            }    
            
        } catch (Exception e) {
            e.printStackTrace();
            json.put("status", "fail");
            json.put("describe", "服务器异常"+e.getMessage());
            response.getWriter().append(json.toString());
        }
        
        
        
        
        /*//去显示上传的文件
        request.setAttribute("pics", piclist);
        request.getRequestDispatcher("show").forward(request, response); */
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
