package gdpi.kjk.book.service;

import java.sql.SQLException;

import gdpi.kjk.book.dao.BookDao;
import gdpi.kjk.po.Book;
import gdpi.kjk.po.PageBean;

public class BookService {
	BookDao bookDao=new BookDao();
	public PageBean<Book> findByCategory(String cid, int pc) {
		try {
			return bookDao.findByCategory(cid, pc);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public PageBean<Book> findByBname(String bname,int pc) {
		try {
			return bookDao.findByBname(bname,pc);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public PageBean<Book> findByAuthor(String author,int pc) {
		try {
			return bookDao.findByAuthor(author,pc);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public PageBean<Book> findByPress(String press,int pc) {
		try {
			return bookDao.findByPress(press,pc);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public PageBean<Book> findByCombination(Book b,int pc) {
		try {
			return bookDao.findByCombination(b,pc);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Object load(String bid) {
		try {
			return bookDao.load(bid);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Book edit(Book b) {
		try {
			return bookDao.edit(b);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
