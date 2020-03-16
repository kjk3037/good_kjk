package gdpi.kjk.order.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import gdpi.kjk.po.Book;
import gdpi.kjk.po.Order;
import gdpi.kjk.po.OrderItem;
import gdpi.kjk.po.PageBean;
import gdpi.kjk.po.PageConstants;

public class OrderDao {
	TxQueryRunner qr=new TxQueryRunner();
	public void add(Order o) throws SQLException {
		String sql1="insert into t_order(oid,ordertime,total,status,address,uid) values(?,?,?,?,?,?)";
		Object[] params1= {
				o.getOid(),
				o.getOrdertime(),
				o.getTotal(),
				o.getStatus(),
				o.getAddress(),
				o.getOwner().getUid()
		};
		qr.update(sql1,params1);
		List<OrderItem> lo=o.getOrderItemList();
		String sql2="insert into t_orderItem(orderItemId,quantity,subtotal,bid,bname,currPrice,image_b,oid) values(?,?,?,?,?,?,?,?) ";
		for(OrderItem oi:lo) {
			Object[] o1= {
					oi.getOrderItemId(),
					oi.getQuantity(),
					oi.getSubtotal(),
					oi.getBook().getBid(),
					oi.getBook().getBname(),
					oi.getBook().getCurrPrice(),
					oi.getBook().getImage_b(),
					o.getOid()
			};
			qr.update(sql2,o1);
		}
	}
	/**
	 * 按用户查询订单
	 * @param uid
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Order> findByUser(String uid, int pc) throws SQLException {
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put("uid", uid);
		return findByCriteria(criteria, pc);
	}
	
	/*
	 * 根据条件分页查询
	 */
	private PageBean<Order> findByCriteria(Map<String,Object> criteria, int pc) throws SQLException {
		/*
		 * 1. 创建sql语句条件子句
		 */
		List<Object> params = new ArrayList<Object>();//条件，对应sql中的“?”
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		for(String name : criteria.keySet()) {//循环遍历每个条件;
			Object value = criteria.get(name);
			if(value == null) {//如果值为空，说明没有这个条件
				continue;
			}
			whereSql.append(" and ").append(name).append("=?");
			params.add(value);
		}
		
		/*
		 * 2. 创建排序和limit子句
		 */
		String orderByAndLimitSql = " order by ordertime desc limit ?,?";
		
		/*
		 * 3. 生成个数查询语句，执行sql，得到总记录数
		 */
		String cntSql = "select count(*) from t_order" + whereSql;
		Number cnt = (Number)qr.query(cntSql, new ScalarHandler(), params.toArray());
		int tr = cnt != null ? cnt.intValue() : 0;
		
		/*
		 * 4. 生成记录查询，执行SQL语句，得到当前页记录
		 */
		String sql = "select * from t_order" + whereSql + orderByAndLimitSql;

		// 计算limit参数
		int ps = PageConstants.BOOK_PAGE_SIZE;//得到每页记录数
		params.add(ps * (pc-1));//计算当前页第一条记录的下标位置，下标从0开始
		params.add(ps);//一共查询几条记录

		// 把mapList映射成List<Book>
		List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class), params.toArray());
		for(Order order : orderList) {
			loadOrderItemList(order);//为当前Order加载其所有OrderItem
		}
		/*
		 * 5. 创建PageBean，返回
		 */
		PageBean<Order> pb = new PageBean<Order>();
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		pb.setDataList(orderList);
		
		return pb;
	}

	/*
	 * 为当前Order对象加载其所有的OrderItem
	 */
	private void loadOrderItemList(Order order) throws SQLException {
		/*
		 * 1. 执行SQL语句，得到List<Map>，List<Map>中的很Map对象t_orderitem表中的一行记录
		 */
		String sql = "select * from t_orderitem where oid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), order.getOid());
		/*
		 * 2. 循环遍历每个Map，把每个Map映射成一个OrderItem对象和一个Book对象，然后再建立关系
		 *   再把OrderItem添加到List中
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map : mapList) {
			OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
			Book book = CommonUtils.toBean(map, Book.class);
			orderItem.setBook(book);
			orderItem.setOrder(order);
			
			orderItemList.add(orderItem);
		}
		/*
		 * 把生成的List<OrderItem>设置给当前Order对象。
		 */
		order.setOrderItemList(orderItemList);
	}
	public Order load(String oid) throws SQLException {
		String sql = "select * from t_order where oid=?";
		Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
		loadOrderItemList(order);
		return order;
	}
	public void updateStatus(String oid, int status) throws SQLException {
		String sql="update t_order set status=? where oid=?";
		qr.update(sql,status,oid);
	}

}
