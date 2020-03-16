package gdpi.kjk.admin.order.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import gdpi.kjk.cart.service.CartItemService;
import gdpi.kjk.order.service.OrderService;
import gdpi.kjk.po.CartItem;
import gdpi.kjk.po.Order;
import gdpi.kjk.po.OrderItem;
import gdpi.kjk.po.PageBean;
import gdpi.kjk.po.User;

public class AdminOrderServlet extends BaseServlet{
	OrderService orderService=new OrderService();
	CartItemService cartItemService =new CartItemService();

	/*
	 * 获取当前页码
	 */
	private int getPageCode(HttpServletRequest req) {
		String pageCode = req.getParameter("pc");
		if(pageCode == null) return 1;
		try {
			return Integer.parseInt(pageCode); 
		} catch(RuntimeException e) {
			return 1;
		}
	}
	
	/*
	 * 获取请求的url，但去除pc参数
	 */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		int fromIndex = url.lastIndexOf("&pc=");
		if(fromIndex == -1) return url;
		int toIndex = url.indexOf("&", fromIndex + 1);
		if(toIndex == -1) return url.substring(0, fromIndex);
		return url.substring(0, fromIndex) + url.substring(toIndex);
	}
	
	public String myOrders(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取当前用户uid
		 */
		User user = (User)req.getSession().getAttribute("sessionUser");
		PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(req);
		pb.setUrl(url);
		/*
		 * 4. 把PageBean保存到request，转发到/jsps/order/list.jsp
		 */
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
	}
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");//获取订单编号
		String oper = req.getParameter("oper");//获取操作，包括：查看、确认收货、取消
		req.setAttribute("order", orderService.load(oid));//加载订单并保存到request中
		req.setAttribute("oper", oper);//把操作也保存到request中，在desc.jsp中会通过oper来显示不同的按钮
		return "/jsps/order/desc.jsp";//转发到desc.jsp
	}
	public String findAll(HttpServletRequest req,HttpServletResponse res) {
		/*
		 * 1. 获取当前页码
		 */
		int pc = getPageCode(req);
		/*
		 * 2. 获取当前用户uid
		 */
		PageBean<Order> pb = orderService.myOrders(null, pc);
		/*
		 * 3. 获取url，设置给PageBean
		 */
		String url = getUrl(req);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "/adminjsps/admin/order/list.jsp";
	}
}
