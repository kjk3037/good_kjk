package gdpi.kjk.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.jdbc.JdbcUtils;
import gdpi.kjk.cart.dao.CartItemDao;
import gdpi.kjk.po.CartItem;

public class CartItemService {
	CartItemDao cartItemDao=new CartItemDao();
	public List<CartItem> myCart(String uid) {
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public void add(CartItem cartItem) {
		try {
			/*
			 * 查询这个条目是否已经存在，如果存在，那么就合并条目，而不是添加条目
			 */
			JdbcUtils.beginTransaction();
			CartItem _cartItem = cartItemDao.findByUserAndBook(
					cartItem.getOwner().getUid(), cartItem.getBook().getBid());
			if(_cartItem == null) {//如果原来不存在这一条目，那么添加条目
				cartItemDao.add(cartItem);
			} else {//如果原来存在这一条目，那么把原条目和新条目的数量合并，然后修改条目数量
				int quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				cartItemDao.updateQuantity(_cartItem.getCartItemId(), quantity);
			}
			JdbcUtils.commitTransaction();
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
	}
	public void deleteBatch(String cartItemIds) {
		try {
			cartItemDao.deleteBatch(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public void updateQuantity(String cartItemId, int quantity) {
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public CartItem load(String cartItemId) {
		try {
			return cartItemDao.load(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public List<CartItem> loadCartItems(String cartItemIds) {
		try {
			return cartItemDao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
