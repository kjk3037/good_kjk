package gdpi.kjk.user.admin.service;

import java.sql.SQLException;

import gdpi.kjk.po.Admin;
import gdpi.kjk.user.admin.dao.AdminDao;

public class AdminService {
	private AdminDao adminDao=new AdminDao();
	public Admin findAdmin(String adminname,String adminpwd){
		try {
			return adminDao.findAdmin(adminname, adminpwd);
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
}
