package gdpi.kjk.book.dao;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import gdpi.kjk.po.Book;
import gdpi.kjk.po.Category;
import gdpi.kjk.po.PageBean;
import gdpi.kjk.po.PageConstants;

public class BookDao {
	TxQueryRunner qr=new TxQueryRunner();
	/**
	 * 按分类查询（分页）
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Book> findByCategory(String cid, int pc) throws SQLException {
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put("cid", cid);
		return findByCriteria(criteria, pc);
	}
	
	/*
	 * 根据条件分页查询
	 */
	private PageBean<Book> findByCriteria(Map<String,Object> criteria, int pc) throws SQLException {
		/*
		 * 1. 创建sql语句条件子句
		 */
		List<Object> params = new ArrayList<Object>();//条件，对应sql中的“?”
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		for(String name : criteria.keySet()) {//循环遍历每个条件
			Object value = criteria.get(name);
			if(value == null) {//如果值为空，说明没有这个条件
				continue;
			}
			whereSql.append(" and ").append(name).append(" like ?");
			params.add("%" + value + "%");
		}
		
		/*
		 * 2. 创建排序和limit子句
		 */
		String orderByAndLimitSql = " order by orderBy limit ?,?";
		
		/*
		 * 3. 生成个数查询语句，执行sql，得到总记录数
		 */
		String cntSql = "select count(*) from t_book" + whereSql;
		Number cnt = (Number)qr.query(cntSql, new ScalarHandler(), params.toArray());
		int tr = cnt != null ? cnt.intValue() : 0;
		
		/*
		 * 4. 生成记录查询，执行SQL语句，得到当前页记录
		 */
		String sql = "select * from t_book" + whereSql + orderByAndLimitSql;

		// 计算limit参数
		int ps = PageConstants.BOOK_PAGE_SIZE;//得到每页记录数
		params.add(ps * (pc-1));//计算当前页第一条记录的下标位置，下标从0开始
		params.add(ps);//一共查询几条记录
	
		// 把mapList映射成List<Book>
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), params.toArray());
		List<Book> bookList = new ArrayList<Book>();
		for (Map<String, Object> map : mapList) {
			Book book = CommonUtils.toBean(map, Book.class);
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			bookList.add(book);
		}
		
		/*
		 * 5. 创建PageBean，返回
		 */
		PageBean<Book> pb = new PageBean<Book>();
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		pb.setDataList(bookList);
		return pb;
	}
	/**
	 * 按作者查询
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author, int pc) throws SQLException {
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put("author", author);
		return findByCriteria(criteria, pc);
	}

	/**
	 * 按出版社查询
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press, int pc) throws SQLException {
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put("press", press);
		return findByCriteria(criteria, pc);
	}
	
	/**
	 * 按图名查询
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname, int pc) throws SQLException {
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put("bname", bname);
		return findByCriteria(criteria, pc);
	}
	
	/**
	 * 多条件组合查询
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book book, int pc) throws SQLException {
		Map<String,Object> criteria = new HashMap<String,Object>();
		criteria.put("bname", book.getBname());
		criteria.put("author", book.getAuthor());
		criteria.put("press", book.getPress());
		return findByCriteria(criteria, pc);
	}
	/**
	 * 通过bid加载图书
	 * 需要查询出图书所属1级、2级分类
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public Book load(String bid) throws SQLException {
		// 多表查询
		String sql = "select * from t_book b, t_category c where b.cid=c.cid and bid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(), bid);
		
		// 把结果映射成Book
		Book book = CommonUtils.toBean(map, Book.class);
		// 把结果映射成Category，它是2级分类
		Category category = CommonUtils.toBean(map, Category.class);
		
		// 创建父分类
		Category parent = new Category();
		// 设置pid，即父分类id为其cid
		parent.setCid((String)map.get("pid"));
		
		
		category.setParent(parent);
		book.setCategory(category);
		return book;
	}

	public Book edit(Book b) throws SQLException {
		
		int i=0;
		String sql1="update t_book set"+"\t";
		Map<String, Object> v=new HashMap<String,Object>();
		v.put("bname", b.getBname());
		v.put("author",b.getAuthor());
		v.put("price", b.getPrice());
		v.put("currPrice", b.getCurrPrice());
		v.put("discount", b.getDiscount());
		v.put("press", b.getPress());
		v.put("publishtime", b.getPublishtime());
		v.put("edition", b.getEdition());
		v.put("pageNum", b.getPageNum());
		v.put("wordNum", b.getWordNum());
		v.put("printtime", b.getPrinttime());
		v.put("booksize", b.getBooksize());
		v.put("paper",b.getPaper());
		v.put("cid", b.getCategory().getCid());
		v.put("image_b", b.getImage_b());
		v.put("image_w", b.getImage_w());
		StringBuilder sql2=new StringBuilder();
		for(String name : v.keySet()) {//循环遍历每个条件
			Object value = v.get(name);
			sql2.append(name+"="+"\'"+value+"\'"+"\t");
			i++;
			if(i!=16) {
				sql2.append(",");
			}
		}
		String sql3="where bid="+"\'"+b.getBid()+"\';";
		System.out.println(sql1+sql2+sql3);
		qr.update(sql1+sql2+sql3);
		Book book=this.load(b.getBid());
		return book;
	}
	public Book findByBid(String bid) throws SQLException {
		String sql="select * from t_book where bid=?";
		return qr.query(sql, new BeanHandler<Book>(Book.class),bid);
	}
}
