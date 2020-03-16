package gdpi.kjk.order.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import gdpi.kjk.cart.dao.CartItemDao;
import gdpi.kjk.cart.service.CartItemService;
import gdpi.kjk.order.service.OrderService;
import gdpi.kjk.po.CartItem;
import gdpi.kjk.po.Order;
import gdpi.kjk.po.OrderItem;
import gdpi.kjk.po.PageBean;
import gdpi.kjk.po.User;

public class OrderServlet extends BaseServlet{
	OrderService orderService=new OrderService();
	CartItemService cartItemService =new CartItemService();
	public String create(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取所有购物车条目id，以及收货地址
		 */
		String cartItemIds = req.getParameter("cartItemIds");
		String address = req.getParameter("address");
		/*
		 * 2. 通过CartItemService加载所有购物车条目
		 */
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		/*
		 * 3. 创建订单
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid());//设置oid
		order.setOrdertime(String.format("%tF %<tT", new java.util.Date()));//设置下单时间为当前时间
		// 设置合计
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : cartItemList) {
			total = total.add(new BigDecimal(Double.toString(cartItem.getSubtotal())));
		}
		order.setTotal(total.doubleValue());
		
		order.setStatus(1);// 设置状态，刚生成的订单为1状态，表示未付款
		order.setAddress(address);// 设置地址
		// 设置所属会员
		User owner = (User)req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		/*
		 * 4. 通过List<CartItem>来创建List<OrderItem>，再把List<OrderItem>设置给Order
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(CartItem cartItem : cartItemList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());//设置orderItemId
			orderItem.setQuantity(cartItem.getQuantity());//设置数量
			orderItem.setSubtotal(cartItem.getSubtotal());//设置小计
			orderItem.setBook(cartItem.getBook());//设置book
			orderItem.setOrder(order);//设置所属订单
			
			orderItemList.add(orderItem);
		}
		order.setOrderItemList(orderItemList);//把订单条目设置给订单
		/*
		 * 5. 调用orderService方法生成order
		 */
		orderService.create(order);//设置order
		// 删除购物车中用来生成订单的条目
		cartItemService.deleteBatch(cartItemIds);
		/*
		 * 6. 保存Order到request中，转发到/jsps/order/ordersucc.jsp
		 */
		req.setAttribute("order", order);
		return "f:/jsps/order/ordersucc.jsp";
	}
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
	public String cancel(HttpServletRequest req,HttpServletResponse res) throws Exception {
		String oid=req.getParameter("oid");
		int status=5;
		orderService.updateStatus(oid,status);
		return myOrders(req, res);
	}
	public String payfor(HttpServletRequest req,HttpServletResponse res) throws Exception {
		String oid=req.getParameter("oid");
		req.setAttribute("order", orderService.load(oid));//加载订单并保存到request中
		return "/jsps/order/pay.jsp";//转发到desc.jsp
	}
	public String finish(HttpServletRequest req,HttpServletResponse res) throws Exception {
		String oid=req.getParameter("oid");
		int status=4;
		orderService.updateStatus(oid,status);
		return myOrders(req, res);
	}
	public String pay(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException {
		String oid=req.getParameter("oid");
		int status=3;
		orderService.updateStatus(oid, status);
		return myOrders(req, res);
	}
	

}
