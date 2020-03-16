package gdpi.kjk.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.jdbc.TxQueryRunner;
import gdpi.kjk.po.Admin;
import gdpi.kjk.po.User;

public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	/**
	 * 校验指定登录名会员是否存在
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean validateLoginname(String loginname) throws SQLException {
		String sql = "select count(1) from t_user where loginname=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), loginname);
		return cnt == null ? false : cnt.intValue() > 0;
	}
	
	/**
	 * 校验指定email的会员是否存在
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean validateEmail(String email) throws SQLException {
		String sql = "select count(1) from t_user where email=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), email);
		return cnt == null ? false : cnt.intValue() > 0;
	}
	//所有信息检查没错后注册账号，把账号信息插入数据库t_user表中
	public void add(User user) throws SQLException {
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {user.getUid(), user.getLoginname(), user.getLoginpass(), 
				user.getEmail(), user.isStatus(), user.getActivationCode()};
		qr.update(sql, params);
	}
	//登陆查找账号
	public User findUser(String loginname,String loginpass) throws SQLException {
		String sql = "select * from t_user where status = 1 and loginname=? and loginpass=?";
		User u=(User)qr.query(sql,new BeanHandler<User>(User.class),loginname,loginpass);
		if(u!=null) {
			return u;
		}
		return null;
	}
}
