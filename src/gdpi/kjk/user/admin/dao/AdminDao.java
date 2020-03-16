package gdpi.kjk.user.admin.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.jdbc.TxQueryRunner;
import gdpi.kjk.po.Admin;

public class AdminDao {
	private TxQueryRunner qr=new TxQueryRunner();
	public Admin findAdmin(String adminname,String adminpwd) throws SQLException {
		String sql = "select * from t_admin where adminname=? and adminpwd=?";
		return qr.query(sql, new BeanHandler<Admin>(Admin.class),adminname,adminpwd);
	}
}
