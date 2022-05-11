package com.ctvv.dao;

import com.ctvv.model.OrderDetail;
import com.ctvv.model.OrderDetail;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO
		extends GenericDAO<OrderDetail> {

	public OrderDetailDAO(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public OrderDetail get(int id) {
		return null;
	}
	public List<OrderDetail> getGroup(int orderId){
			List<OrderDetail> orderDetailList = new ArrayList<>();
			String sql = "SELECT * FROM order_detail WHERE order_id=? ";
			try (Connection connection = dataSource.getConnection(); PreparedStatement statement =
					connection.prepareStatement(sql);) {
				statement.setInt(1, orderId);
				ResultSet resultSet = statement.executeQuery();
				// loop the result set
				while (resultSet.next()) {
					orderDetailList.add(map(resultSet));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			return orderDetailList;
	}
	@Override
	public List<OrderDetail> getAll() {
		return null;
	}

	@Override
	public OrderDetail create(OrderDetail orderDetail) {
		return null;
	}

	@Override
	public OrderDetail update(OrderDetail orderDetail) {
		return null;
	}

	@Override
	public void delete(int id) {

	}

	@Override
	public OrderDetail map(ResultSet resultSet) {
		try {
			int orderId = resultSet.getInt("order_id");
			int productId = resultSet.getInt("product_id");
			String productName = resultSet.getString("product_name");
			int quantity = resultSet.getInt("quantity");
			int price = resultSet.getInt("price");
			return new OrderDetail(orderId, productId, productName, quantity, price);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Insert 1 danh sách orderDetail của 1 order <br> (Sử dụng batch)
	 */
	public void create(List<OrderDetail> orderDetailList) {
		String sql = "INSERT INTO order_detail VALUES (?, ?, ?,?,?)";
		Connection connection;
		PreparedStatement statement;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql);
			// Loop qua từng orderDetail
			for (OrderDetail orderDetail : orderDetailList) {
				statement.setInt(1, orderDetail.getOrderId());
				statement.setInt(2, orderDetail.getProductId());
				statement.setString(3, orderDetail.getProductName());
				statement.setInt(4, orderDetail.getQuantity());
				statement.setInt(5, orderDetail.getPrice());
				statement.addBatch();
			}
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}