package gdpi.kjk.user.admin.web.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.servlet.BaseServlet;
import gdpi.kjk.po.Admin;
import gdpi.kjk.user.admin.service.AdminService;

public class AdminServlet extends BaseServlet{
	AdminService as=new AdminService();
	public String login(HttpServletRequest req,HttpServletResponse res){
		Admin a=as.findAdmin(req.getParameter("adminname"),req.getParameter("adminpwd"));
		if(a==null){
			req.setAttribute("msg","您输入的账号或者密码错误，请输入正确的账号和密码!");
			return "/adminjsps/login.jsp";
		}
		HttpSession session=req.getSession();
		session.setAttribute("admin", a.getAdminname());
		return "f:/adminjsps/admin/index.jsp";
	}
	public String quit(HttpServletRequest req,HttpServletResponse res) {
		HttpSession s=req.getSession();
		s.removeAttribute("admin");
		System.out.println("账号退出,Session清除");
		return "f:/adminjsps/login.jsp";
	}
}
