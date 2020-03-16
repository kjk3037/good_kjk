package gdpi.kjk.category.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanHandler;

import gdpi.kjk.category.dao.CategoryDao;
import gdpi.kjk.po.Category;

public class CategoryService {
	CategoryDao categoryDao=new CategoryDao();
	public List<Category> findAll() {
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Boolean add(Category c) {
		try {
			return categoryDao.add(c);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public Category findCategoryByCid(String cid) {
		try {
			return categoryDao.findCategoryByCid(cid);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Category findCategoryByCname(String cname) {
		try {
			return categoryDao.findCategoryByCid(cname);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Boolean editCategory(Category c) {
		try {
			return categoryDao.editCategory(c);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Boolean deleteCategory(Category c) {
		try {
			return categoryDao.deleteCategory(c);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public List<Category> findCategoryByPid(String pid) throws SQLException {
		try {
			return categoryDao.findCategoryByPid(pid);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
