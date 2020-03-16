package gdpi.kjk.admin.category.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import com.google.gson.Gson;

import cn.itcast.servlet.BaseServlet;
import gdpi.kjk.category.service.CategoryService;
import gdpi.kjk.po.Category;

public class AdminCategoryServlet extends BaseServlet{
	CategoryService categoryService=new CategoryService();
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws SAXException, IOException {
		/*
		 * 1. 获取所有分类
		 */
		List<Category> parents = categoryService.findAll();
		/*
		 * 2. 保存到request中
		 */
		req.setAttribute("parents", parents);
		/*
		 * 3. 转发到left.jsp
		 */
		return "f:/adminjsps/admin/category/list.jsp";
	}
	public String addTwoLevelpre(HttpServletRequest req,HttpServletResponse res) throws SQLException {
		req.setAttribute("myparent", req.getParameter("cid"));
		req.setAttribute("parents", categoryService.findCategoryByPid(null));
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	//添加一级分类
	public String addOneLevel(HttpServletRequest req,HttpServletResponse res) throws SAXException, IOException {
		Category c=new Category();
		c.setCname(req.getParameter("cname"));
		c.setDesc(req.getParameter("desc"));
		Boolean flag=categoryService.add(c);
		if(!flag) {
			req.setAttribute("msg", "一类添加失败!");
			return "/adminjsps/admin/category/add.jsp";
		}
		return this.findAll(req, res);
	}
	//添加二级分类
	public String addTwoLevel(HttpServletRequest req,HttpServletResponse res) throws SAXException, IOException {
		Category c=new Category();
		Category cp=categoryService.findCategoryByCid(req.getParameter("pid"));
		c.setParent(cp);
		c.setCname(req.getParameter("cname"));
		c.setDesc(req.getParameter("desc"));
		Boolean flag=categoryService.add(c);
		if(!flag) {
			req.setAttribute("msg", "二类添加失败!");
			return "/adminjsps/admin/category/add.jsp";
		}
		return this.findAll(req, res);
	}
	//修改页面一类分类信息获取
	public String editOneLevelpre(HttpServletRequest req,HttpServletResponse res) {
		Category c=categoryService.findCategoryByCid(req.getParameter("cid"));
		req.setAttribute("c", c);
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	//修改一级分类
	public String editOneLevel(HttpServletRequest req,HttpServletResponse res) throws SAXException, IOException {
		Category c=new Category();
		c.setCid(req.getParameter("cid"));
		c.setCname(req.getParameter("cname"));
		c.setDesc(req.getParameter("desc"));
		Boolean flag=categoryService.editCategory(c);
		if(!flag) {
			req.setAttribute("msg", "一类修改失败!");
			return "/adminjsps/admin/category/edit.jsp";
		}
		return this.findAll(req, res);
	}
	//修改页面二类分类信息获取
		public String editTwoLevelpre(HttpServletRequest req,HttpServletResponse res) throws SQLException {
			Category c=categoryService.findCategoryByCid(req.getParameter("cid"));
			req.setAttribute("parents", categoryService.findCategoryByPid(null));
			req.setAttribute("c", c);
			return "f:/adminjsps/admin/category/edit2.jsp";
		}
	public String editTwoLevel(HttpServletRequest req,HttpServletResponse res) throws SAXException, IOException {
		Category c=new Category();
		c.setParent(categoryService.findCategoryByCid(req.getParameter("pid")));
		c.setCid(req.getParameter("cid"));
		c.setCname(req.getParameter("cname"));
		c.setDesc(req.getParameter("desc"));
		Boolean flag=categoryService.editCategory(c);
		if(!flag) {
			req.setAttribute("msg", "二类修改失败!");
			return "/adminjsps/admin/category/edit2.jsp";
		}
		return this.findAll(req, res);
	}
	//删除分类
	public String deleteCategory(HttpServletRequest req,HttpServletResponse res) throws SAXException, IOException, SQLException {
		Category c=categoryService.findCategoryByCid(req.getParameter("cid"));
		c.setChildren(categoryService.findCategoryByPid(c.getCid()));
		if(c.getChildren().equals(null)) {
			req.setAttribute("msg", "此一类中还有二类存在，不能被删除!");
			return "/adminjsps/admin/category/list.jsp";
		}
		Boolean flag=categoryService.deleteCategory(c);
		if(!flag) {
			req.setAttribute("msg", "删除失败");
			return "/adminjsps/admin/category/list.jsp";
		}
		return this.findAll(req, res);
	}
	//还差删除二类时查询其分类下有没有书本没完成
	//图书管理左列表
	public String findAllBook(HttpServletRequest req, HttpServletResponse resp)
			throws SAXException, IOException {
		/*
		 * 1. 获取所有分类
		 */
		List<Category> parents = categoryService.findAll();
		/*
		 * 2. 保存到request中
		 */
		req.setAttribute("parents", parents);
		/*
		 * 3. 转发到left.jsp
		 */
		return "f:/adminjsps/admin/book/left.jsp";
	}
	public String childrenForAjax(HttpServletRequest req,HttpServletResponse res) throws SQLException, IOException {
		List<Category> lc= categoryService.findCategoryByPid(req.getParameter("pid"));
		/*Iterator<Category> i=lc.iterator();
		String result = "{\"children\":";
		String cs=null;
		while(i.hasNext()) {
			if(cs==null) {
				cs="\""+i.next().getCname()+"\"";
			}else {
				cs=",\""+i.next().getCname()+"\"";
			}
			result=result+cs;
		}
		result=result+"}";
		System.out.println(result);
		res.getWriter().append(result);
		req.setAttribute("children", lc);*/
		Gson gson = new Gson();  
        String json = gson.toJson(lc);
        System.out.println(json);
        res.setCharacterEncoding("UTF-8");    
        res.setContentType("application/json; charset=utf-8");    
        PrintWriter writer = res.getWriter();
        writer.append(json);
		return null;
	}
}
