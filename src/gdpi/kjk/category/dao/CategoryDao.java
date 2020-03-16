package gdpi.kjk.category.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import cn.itcast.jdbc.TxQueryRunner;
import gdpi.kjk.po.Category;;

public class CategoryDao {
	TxQueryRunner qr=new TxQueryRunner();
	public List<Category> findAll() throws SQLException {
		/*
		 * 1. 获取所有一级分类
		 *   pid为null就是一级分类。
		 */
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Category> parents = qr.query(sql, 
				new BeanListHandler<Category>(Category.class));
		
		/*
		 * 2. 循环遍历每个一级分类，为其加载它的所有二级分类
		 */
		sql = "select * from t_category where pid=? order by orderBy";
		for(Category parent : parents) {
			// 获取当前一级分类的所有二级分类
			List<Category> children = qr.query(sql, 
					new BeanListHandler<Category>(Category.class), 
					parent.getCid());
			// 给当前一级分类设置二级分类
			parent.setChildren(children);
			// 为每个二级分类设置一级分类
			for(Category child : children) {
				child.setParent(parent);
			}
		}
		/*
		 * 3. 返回一级分类List，每个一级分类都包含了自己的二级分类
		 */
		return parents;
	}
	public Boolean add(Category c) throws SQLException {
		String sql="insert into t_category(cname,`desc`,cid) values(?,?,?)";
		String str = UUID.randomUUID().toString();  
	    // 去掉"-"符号   
	    String temp = str.substring(0, 8) +str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) +str.substring(24);
	    //如果有parent，则将其添加成二类
	    if(c.getParent() != null) {
	    	String sql2="insert into t_category(cname,`desc`,cid,pid) values(?,?,?,?)";
	    	Object[] two= {
	    			c.getCname(),
	    			c.getDesc(),
	    			temp,
	    			c.getParent().getCid()
	    	};
	    	qr.update(sql2,two);
	    	return true;
	    }
	    String sqlone="SELECT * from t_category where pid is null && cid = (SELECT MAX(cid) from t_category where pid is null)";
	    Category cm=(Category)qr.query(sqlone ,new BeanHandler<Category>(Category.class) );
	    int max=Integer.parseInt(cm.getCid());
		Object[] one= {
				c.getCname(),
				c.getDesc(),
				max+1
		};
		qr.update(sql, one);
		return true;
	}
	public Category findCategoryByCid(String cid) throws SQLException {
		String sql="select * from t_category where cid=?";
		Category c=(Category)qr.query(sql, new BeanHandler<Category>(Category.class),cid);
		return c;
	}
	public Category findCategoryByCname(String cname) throws SQLException {
		String sql="select * from t_category where cname=?";
		Category c=(Category)qr.query(sql, new BeanHandler<Category>(Category.class),cname);
		return c;
	}
	public Boolean editCategory(Category c) throws SQLException {
		//如果参数有parent,则修改二类
		if(c.getParent() != null) {
			String sql2="update t_category set cname=?,`desc`=? ,pid=? where cid=?";
			qr.update(sql2,c.getCname(),c.getDesc(),c.getParent().getCid(),c.getCid());
			return true;
		}else {
		//如果参数没有parent,则修改一类
			String sql1="update t_category set cname=?,`desc`=? where cid=?";
			qr.update(sql1,c.getCname(),c.getDesc(),c.getCid());
			return true;
		}
	}
	//删除分类
	public Boolean deleteCategory(Category c) throws SQLException {
		String sql="delete from t_category where cid=?";
		qr.update(sql,c.getCid());
		return true;
	}
	public List<Category> findCategoryByPid(String pid) throws SQLException {
		String sql="select * from t_category where pid=?";
		if(pid==null) {
			sql="select * from t_category where pid is null";
			List<Category> c1=qr.query(sql, new BeanListHandler<Category>(Category.class));
			return c1;
		}
		if(pid.equals("all")) {
			sql="select * from t_category where pid is not null order by orderby";
			List<Category> c2=qr.query(sql, new BeanListHandler<Category>(Category.class));
			return c2;
		}
		List<Category> c=qr.query(sql, new BeanListHandler<Category>(Category.class),pid);
		return c;
	}
}
