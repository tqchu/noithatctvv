package com.ctvv.util;

import com.ctvv.dao.ImportDAO;
import com.ctvv.model.Import;
import com.ctvv.model.ImportDetail;
import com.ctvv.model.JasperReportImportDetail;
import com.mysql.cj.jdbc.MysqlDataSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JasperReportUtils {
	public static byte[] createImportReport(Import anImport)  {
		JRBeanCollectionDataSource collectionDataSource = new JRBeanCollectionDataSource(JasperReportImportDetail.getJRImportDetailList(anImport.getImportDetailList()));
		// Map to hold JR Param
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("providerName", anImport.getProviderName());
		parameters.put("providerTaxId", anImport.getProviderTaxId());

		parameters.put("totalPrice", (long)anImport.getTotalPrice());
		parameters.put("day", 3);
		parameters.put("month", 6);
		parameters.put("year", 2022);
		parameters.put("importerName", anImport.getImporterName());
		parameters.put("CollectionBeanDataSet", collectionDataSource);

		try{
			// Read template
			InputStream inputStream = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource(
					"").getPath()+ "/report/ImportDetail.jrxml");

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

			return JasperExportManager.exportReportToPdf(jasperPrint);
		}
		catch (FileNotFoundException | JRException e){
			e.printStackTrace();
		}
		return null;
	}

}
