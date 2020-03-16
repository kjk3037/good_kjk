package gdpi.kjk.category.web.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;
import gdpi.kjk.category.service.CategoryService;
import gdpi.kjk.po.Category;

public class CategoryServlet extends BaseServlet{
	CategoryService categoryService=new CategoryService();
	public String findAll(HttpServletRequest req,HttpServletResponse res) {
		List<Category> parents=categoryService.findAll();
		req.setAttribute("parents", parents);
		return "jsps/left.jsp";
	}
}
