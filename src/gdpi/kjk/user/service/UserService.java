package gdpi.kjk.user.service;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Session;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import gdpi.kjk.po.User;
import gdpi.kjk.user.dao.UserDao;

public class UserService {
	private UserDao userDao = new UserDao();
	public boolean validateLoginname(String loginname) {
		try {
			return userDao.validateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 校验指定Email的会员是否存在
	 * @param loginname
	 * @return
	 */
	public boolean validateEmail(String email) {
		try {
			return userDao.validateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public void regist(User user) {
		try {
			/*
			 * 1. 对user进行数据补全
			 */
			user.setUid(CommonUtils.uuid());
			user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
			user.setStatus(false);
			
			/*
			 * 2. 向数据库添加记录
			 */
			userDao.add(user);
			
			/*
			 * 3. 向用户注册邮箱地址发送“激活”邮件
			 */
			
			// 读取email模板中的数据
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader()
					.getResourceAsStream("email_template.properties"));
			String host = props.getProperty("host");//获取邮件服务器地址
			String username = props.getProperty("username");//获取用户名
			String password = props.getProperty("password");//获取密码
			String from = props.getProperty("from");//获取发件人地址
			String to = user.getEmail();//获取收件人地址
			String subject = props.getProperty("subject");//获取主题
			//获取内容模板，替换其中的激活码
			String content = MessageFormat.format(props.getProperty("content"), 
					user.getActivationCode());
			
			// 发送邮件
			Session session = MailUtils.createSession(host, username, password);
			Mail mail = new Mail(from, to, subject, content);
			MailUtils.send(session, mail);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}

	//登陆
	public User findUser(String loginname,String loginpass) {
		try {
			return userDao.findUser(loginname, loginpass);
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
