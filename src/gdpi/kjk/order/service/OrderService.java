package gdpi.kjk.order.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.jdbc.JdbcUtils;
import gdpi.kjk.order.dao.OrderDao;
import gdpi.kjk.po.Order;
import gdpi.kjk.po.PageBean;

public class OrderService {
	OrderDao orderDao=new OrderDao();
	public void create(Order order) {
		try {
			JdbcUtils.beginTransaction();
			orderDao.add(order);
			JdbcUtils.commitTransaction();
		} catch(SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);	
			}
			throw new RuntimeException(e);
		}
	}
	public PageBean<Order> myOrders(String uid, int pc) {
		try {
			return orderDao.findByUser(uid, pc);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Object load(String oid) {
		try {
			return orderDao.load(oid);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public void updateStatus(String oid, int status) {
		try {
			orderDao.updateStatus(oid,status);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
