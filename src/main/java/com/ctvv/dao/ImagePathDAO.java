package com.ctvv.dao;

import com.ctvv.model.ImagePath;
import com.ctvv.model.ImagePath;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImagePathDAO extends GenericDAO<ImagePath> {

	public ImagePathDAO(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public ImagePath get(int id) {
	return  null;

	}

	@Override
	public List<ImagePath> getAll() {
		return null;
	}

	@Override
	public void create(ImagePath imagePath) {

	}

	@Override
	public ImagePath update(ImagePath imagePath) {
		return null;
	}

	@Override
	public void delete(int id) {

	}
	public List<ImagePath> getGroup(int productId) {
		List<ImagePath> imagePathList = new ArrayList<>();
		String sql = "SELECT * FROM image WHERE product_id=? ";
		try (Connection connection = dataSource.getConnection(); PreparedStatement statement =
				connection.prepareStatement(sql);) {
			statement.setInt(1, productId);
			ResultSet resultSet = statement.executeQuery();
			// loop the result set
			while (resultSet.next()) {
				int id=resultSet.getInt("product_id");
				String path = resultSet.getString("image_path");
				imagePathList.add(new ImagePath(id, path));

			}

		}
		catch (SQLException e){
			e.printStackTrace();
		}
		return imagePathList;
	}
}