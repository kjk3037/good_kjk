package gdpi.kjk.user.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import gdpi.kjk.po.User;
import gdpi.kjk.user.service.UserService;

public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	
	/**
	 * 异步校验登录名
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String loginname = req.getParameter("loginname");
		boolean flag = userService.validateLoginname(loginname);//如果登录名已被注册返回true
		resp.getWriter().print(flag + "");
		return "f:/index.jsp";
	}

	/**
	 * 异步校验Email
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String email = req.getParameter("email");
		boolean flag = userService.validateEmail(email);//如果Email已被注册返回true
		resp.getWriter().print(flag + "");
		return null;
	}
	
	/**
	 * 异步校验验证码
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String validateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String vCode = (String) req.getSession().getAttribute("vCode");
		String verifyCode = req.getParameter("verifyCode");
		boolean flag = vCode.equalsIgnoreCase(verifyCode);//如果验证码正确返回true
		resp.getWriter().print(flag + "");
		return null;
	}
	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User对象中
		 */
		User user = CommonUtils.toBean(req.getParameterMap(), User.class);
		
		/*
		 * 2. 对表单数据进行服务器端校验
		 */
		Map<String,String> errors = validateRegist(user, req);
		if(errors != null && errors.size() > 0) {//是否存在校验错误信息
			req.setAttribute("errors", errors);
			req.setAttribute("user", user);
			return "f:/jsps/user/regist.jsp";
		}
		
		/*
		 * 3. 调用userService完成注册
		 */
		userService.regist(user);
		
		/*
		 * 4. 保存注册成功信息，转发到msg.jsp显示
		 */
		req.setAttribute("code", "success");//是成功信息还是错误信息
		req.setAttribute("msg", "恭喜，注册成功！请马上到邮箱完成激活！");
		return "f:/jsps/msg.jsp";
	}
	private Map<String, String> validateRegist(User user, HttpServletRequest req) {
		Map<String,String> errors = new HashMap<String,String>();
		//对loginname进行校验
		String loginname = user.getLoginname();
		if(loginname == null || loginname.isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if(loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} else if(userService.validateLoginname(loginname)) {
			errors.put("loginname", "用户名已被注册过！");
		}
		
		// 对loginpass进行校验
		String loginpass = user.getLoginpass();
		if(loginpass == null || loginpass.isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if(loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		
		// 对确认密码进行校验
		String reloginpass = user.getReloginpass();
		if(reloginpass == null || reloginpass.isEmpty()) {
			errors.put("reloginpass", "确认密码不能为空！");
		} else if(!reloginpass.equalsIgnoreCase(loginpass)) {
			errors.put("reloginpass", "两次输入密码不一致！");
		}
		
		// 对Email进行校验
		String email = user.getEmail();
		if(email == null || email.isEmpty()) {
			errors.put("email", "Email不能为空！");
		} else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "错误的Email格式！");
		} else if(userService.validateEmail(email)) {
			errors.put("email", "Email已被注册过！");
		}
		
		// 对验证码进行校验
		String verifyCode = user.getVerifyCode();
		String vCode = (String) req.getSession().getAttribute("vCode");
		if(verifyCode == null || verifyCode.isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if(verifyCode.length() != 4) {
			errors.put("verifyCode", "错误的验证码！");
		} else if(!verifyCode.equalsIgnoreCase(vCode)) {
			errors.put("verifyCode", "错误的验证码！");
		}
		return errors;
	}

	//登录验证
	/*public String login(HttpServletRequest req,HttpServletResponse res) {
		HttpSession s=req.getSession();
		String sr=req.getParameter("verifyCode");
		String tp=(String) s.getAttribute("vCode");
		if(! sr.equals(tp) ) {
			req.setAttribute("msg", "验证码错误!");
			return "/jsps/user/login.jsp";
		}
		Boolean flag=userService.findUser(req.getParameter("loginname"), req.getParameter("loginpass"));
		if(flag == true) {
			s.setAttribute("user", req.getParameter("loginname"));
			return "f:/index.jsp";
		}
		req.setAttribute("msg", "你输入的密码或者账号错误，请输入正确的账号和密码!");
		return "/jsps/user/login.jsp";
	}*/
	public String login(HttpServletRequest req,HttpServletResponse res) {
		User formBean=CommonUtils.toBean(req.getParameterMap(), User.class);
		Map<String, String> errors=new HashMap<String,String>();
		HttpSession s=req.getSession();
		String vCode=(String) s.getAttribute("vCode");
		String verifyCode=req.getParameter("verifyCode");
		if(formBean.getLoginname() == null || formBean.getLoginname().isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		}else if(formBean.getLoginpass()==null || formBean.getLoginpass().isEmpty()) {
			errors.put("loginpass", "密码不能为空");
		}else if(formBean.getVerifyCode() == null || formBean.getVerifyCode().isEmpty()) {
			errors.put("verifyCode", "验证码不能为空");
		}
		User u=userService.findUser(formBean.getLoginname(), formBean.getLoginpass());
		if(errors.isEmpty()) {
			if(u==null) {
				errors.put("用户名或者密码错误!", "请输入正确的用户名和密码");
				req.setAttribute("errors", errors);
				return "/jsps/user/login.jsp";
			}else if(!vCode.equalsIgnoreCase(verifyCode)){
				errors.put("验证码错误!", "请输入正确的验证码");
				req.setAttribute("msg", errors);
				return "jsps/user/login.jsp";
			}
			s.setAttribute("sessionUser", u);
			return "f:/index.jsp";
		}
		req.setAttribute("errors", errors);
		return "/jsps/user/login.jsp";
	}
	public String quit(HttpServletRequest req,HttpServletResponse res) {
		HttpSession s=req.getSession();
		s.invalidate();
		return "r:/index.jsp";
	}
}

