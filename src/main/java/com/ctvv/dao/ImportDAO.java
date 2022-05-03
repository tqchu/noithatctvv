package com.ctvv.dao;

import com.ctvv.model.Category;
import com.ctvv.model.Import;
import com.ctvv.model.ImportDetail;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ImportDAO
		extends GenericDAO<Import> {
	private  ImportDetailDAO importDetailDAO;
	public ImportDAO(DataSource dataSource) {

		super(dataSource);
		importDetailDAO  = new ImportDetailDAO(dataSource);
	}

	@Override
	public Import get(int id) {
		String sql = "SELECT * FROM import WHERE import_id=?";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return map(resultSet);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Import> get(int begin, int numberOfRec, String keyword, String sortBy, String order) {
		if (order==null) order="ASC";
		List<Import> importList = new ArrayList<>();
		String sql =
				"SELECT * FROM import " +
						(keyword != null ? " WHERE provider_name LIKE '%" + keyword + "%' " : "") +
						(sortBy != null ? "ORDER BY " + sortBy +" " + order: "") +
						" LIMIT " + begin + "," + numberOfRec;
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				importList.add(map(resultSet));
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return importList;
	}

	@Override
	public List<Import> getAll() {
		return null;
	}
	@Override
	public Import create(Import pImport) {
		return null;
	}

	@Override
	public Import update(Import anImport) {
		return null;
	}

	@Override
	public void delete(int id) {
		/*String sql = "DELETE  FROM import WHERE import_id=?";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public Import map(ResultSet resultSet) {
		try {
			int importId = resultSet.getInt("import_id");
			String importerName = resultSet.getString("importer_name");
			int providerId = resultSet.getInt("provider_id");
			String providerName = resultSet.getString("provider_name");
			LocalDate importDate = resultSet.getDate("import_date").toLocalDate();
			List<ImportDetail> importDetailList = importDetailDAO.getGroup(importId);
			int totalPrice = totalPrice(importDetailList);

			return new Import(importId, importerName, providerId, providerName, importDate, totalPrice, importDetailList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}
	public int count(String keyword){
		int count = 0;
		String sql =
				"SELECT COUNT(import_id) AS no FROM import " +
						(keyword != null ? " WHERE import.provider_name LIKE '%" + keyword + "%' " : "") ;
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			count = resultSet.getInt("no");
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  count;
	}
	private int totalPrice(List<ImportDetail> importDetailList){
		int totalPrice = 0;
		for (ImportDetail importDetail :importDetailList) {
			totalPrice+= importDetail.getPrice()* importDetail.getQuantity()*(1-importDetail.getTax());
		}
		return totalPrice;
	}
}
