package com.ctvv.dao;

import com.ctvv.model.Admin;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.*;

public class AdminDAO {

	private final DataSource dataSource;

	public AdminDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Admin validate(Admin admin) throws SQLException {
		// Tạo connection
		Admin authenticatedAdmin = null;
		String username = admin.getUsername();
		String password = admin.getPassword();
		String sql = "SELECT * FROM admin WHERE (username=?) and (password=?) LIMIT 1";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			statement.setString(2, password);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int userId = resultSet.getInt("user_id");
				String fullName = resultSet.getString("fullname");
				String userName = resultSet.getString("username");
				String passWord = resultSet.getString("password");
				String role = resultSet.getString("role");
				authenticatedAdmin = new Admin(userId, username, passWord, fullName, role);
			}
		} finally {
			if (resultSet != null) resultSet.close();
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}
		return authenticatedAdmin;
	}

	public Admin update(Admin admin) throws SQLException {

		String fullName = admin.getFullName();
		String username = admin.getUsername();
		String password = admin.getPassword();
		int id = admin.getUserId();

		String sql = "UPDATE admin SET fullname=?, username=?, password=? WHERE user_id=?";
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, fullName);
			statement.setString(2, username);
			statement.setString(3, password);
			statement.setInt(4, id);
			statement.execute();
		}
		finally {
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}
		return admin;
	}
	public void createAdmin(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String fullname = request.getParameter("fullName");
		Connection connection = null;
		String sql = "INSERT INTO admin(username, password, fullname)  VALUES(?, ?, ?)";
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1,username);
			statement.setString(2,password);
			statement.setString(3,fullname);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}
	}

}
