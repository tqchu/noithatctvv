package com.ctvv.dao;

import com.ctvv.model.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO
		extends GenericDAO<Product> {
	DimensionDAO dimensionDAO;
	MaterialDAO materialDAO;
	ImagePathDAO imagePathDAO;
	private CategoryDAO categoryDAO;

	public ProductDAO(DataSource dataSource) {
		super(dataSource);
		categoryDAO = new CategoryDAO(dataSource);
		dimensionDAO = new DimensionDAO(dataSource);
		materialDAO = new MaterialDAO(dataSource);
		imagePathDAO = new ImagePathDAO(dataSource);
	}

	@Override
	public Product get(int id) {
		String sql = "SELECT * FROM product WHERE product_id=?";
		try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement =
				connection.prepareStatement(sql);) {
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				boolean status = resultSet.getBoolean("status");
				// Nếu sản phẩm còn sử dụng
				if (status) {
					return map(resultSet);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Product> getAll() {
		List<Product> productList = new ArrayList<>();
		String sql = "SELECT * FROM product";
		try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement =
				connection.prepareStatement(sql);) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				boolean status = resultSet.getBoolean("status");
				// Sản phẩm còn kinh doanh
				if (status) {
					productList.add(map(resultSet));
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productList;
	}

	@Override
	public void create(Product product) {

	}

	@Override
	public Product update(Product product) {
		return null;
	}

	@Override
	public void delete(int id) {

		}

	@Override
	public Product map(ResultSet resultSet) {
		try {
			int productId = resultSet.getInt("product_id");
			String productName = resultSet.getString("product_name");
			boolean status = resultSet.getBoolean("status");
			int warrantyPeriod = resultSet.getInt("warranty_period");
			int quantity = resultSet.getInt("quantity");
			int price = resultSet.getInt("price");
			String description = resultSet.getString("description");
			int categoryId = resultSet.getInt("category_id");


			Category category = categoryDAO.get(categoryId);
			List<Dimension> dimensionList = dimensionDAO.getGroup(productId);
			List<Material> materialList = materialDAO.getGroup(productId);
			List<ImagePath> imagePathList = imagePathDAO.getGroup(productId);
			return  new Product(productId, productName, warrantyPeriod, quantity, description, price, status,
					category ,dimensionList ,materialList ,imagePathList );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}


	public List<Product> search(int categoryId) {
		List<Product> productList = new ArrayList<>();
		String sql = "SELECT * FROM product WHERE category_id=?";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1,  categoryId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				productList.add(map(resultSet));
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productList;
	}
}
