package com.ctvv.controller.admin;

import com.ctvv.dao.*;
import com.ctvv.model.*;
import com.ctvv.util.JasperReportUtils;
import org.bouncycastle.util.encoders.UTF8;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ManageInventoryController", value = "/admin/inventory/*")
public class ManageInventoryController
		extends HttpServlet {
	final int NUMBER_OF_RECORDS_PER_PAGE = 10;
	final String HOME_PAGE = "/admin/manage/home.jsp";
	final String INVENTORY_SERVLET = "/admin/inventory";
	final String HISTORY_SERVLET = "/admin/inventory/history";
	private HttpSession httpSession;
	private ImportDAO importDAO;
	private ProductDAO productDAO;
	private ProviderDAO providerDAO;
	private ImportDetailDAO importDetailDAO;
	private StockItemDAO stockItemDAO;

	@Override
	protected void doGet(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		httpSession = request.getSession();

		// VIEW HISTORY DETAIL
		if (request.getRequestURI().equals(request.getContextPath() + "/admin/inventory/history/view")) {
			viewHistoryDetail(request, response);
		}
		// INVENTORY HISTORY HOME

		else if (request.getRequestURI().startsWith(request.getContextPath() + "/admin/inventory/history")) {
			listImport(request, response);
		}
		// INVENTORY HOME
		else {
			String action = request.getParameter("action");
			if (action != null) {
				switch (action) {
					case "create":
						List<Product> productList = productDAO.getAll("product_name", "ASC");
						List<Provider> providerList = providerDAO.getAll("provider_name", "ASC");
						request.setAttribute("providerList", providerList);
						request.setAttribute("productList", productList);
						RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/manage/inventory/addForm" +
								".jsp");
						dispatcher.forward(request, response);
						break;
				}
			} else {
				listStockItems(request, response);
			}
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		httpSession = request.getSession();
		String path = request.getPathInfo();
		if (path.equals("/history/download")) {
			downloadImportReport(request, response);
		} else {
			String action = request.getParameter("action");
			switch (action) {
				case "create":
					create(request, response);
					break;

			}
		}
	}

	private void downloadImportReport(HttpServletRequest request, HttpServletResponse response) {



		// gets MIME type of the file
		String mimeType = "application/pdf";

		// modifies response
		response.setContentType(mimeType);

		// forces download
		String headerKey = "Content-Disposition";
		String headerValue = null;
		try {
			headerValue = String.format("attachment; filename=\"%s\"", URLEncoder.encode("Chi-tiết-đơn-nhập.pdf",
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setHeader(headerKey, headerValue);

		// obtains response's output stream

		int id = Integer.parseInt(request.getParameter("id"));
		Import anImport = importDAO.get(id);
		byte[] bytes = JasperReportUtils.createImportReport(anImport);
		response.setContentLength(bytes.length);
		OutputStream os ;
		try {
			os = response.getOutputStream();
			os.write(bytes);
			os.flush();
			os.close();


		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int providerId = Integer.parseInt(request.getParameter("providerId"));
		Provider provider = providerDAO.get(providerId);
		String providerTaxId = provider.getTaxId();
		String importerName = request.getParameter("importerName");

		LocalDateTime importDate = LocalDateTime.now();
		String[] productIdParams = request.getParameterValues("productId");
		String[] quantityParams = request.getParameterValues("quantity");
		String[] priceParams = request.getParameterValues("price");
		String[] taxParams = request.getParameterValues("tax");

		List<ImportDetail> importDetailList = new ArrayList<>();
		int numberOfProducts = productIdParams.length;
		for (int i = 0; i < numberOfProducts; i++) {
			int productId = Integer.parseInt(productIdParams[i]);
			Product product = productDAO.get(productId);
			int quantity = Integer.parseInt(quantityParams[i]);
			int price = Integer.parseInt(priceParams[i]);
			double tax = Integer.parseInt(taxParams[i]) / 100.0;
			importDetailList.add(new ImportDetail(productId, product.getName(), quantity, price, tax));
		}
		Import anImport = new Import(importerName, providerId, providerTaxId,provider.getProviderName(), importDate, 0,
				importDetailList);
		importDAO.create(anImport);

		httpSession.setAttribute("successMessage", "Thêm đơn thành công");
		response.sendRedirect(request.getContextPath() + HISTORY_SERVLET);

	}

	private void viewHistoryDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                                IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		Import anImport = importDAO.get(id);
		request.setAttribute("anImport", anImport);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/manage/inventory/historyDetail.jsp");
		dispatcher.forward(request, response);
	}

	private void listImport(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyword = request.getParameter("keyword");
		LocalDateTime from = request.getParameter("from") != null ?
				LocalDate.parse(request.getParameter("from")).atStartOfDay()
				: null;
		LocalDateTime to = request.getParameter("to") != null ?
				LocalDate.parse(request.getParameter("to")).atTime(23, 59, 59) : null;
		String orderBy = getOrder(request);
		List<Import> importList;
		int begin = getBegin(request);
		importList = importDAO.get(begin, NUMBER_OF_RECORDS_PER_PAGE, keyword, from, to, orderBy, null);
		int numberOfPages = (importDAO.count(keyword, from, to) - 1) / NUMBER_OF_RECORDS_PER_PAGE + 1;
		request.setAttribute("numberOfPages", numberOfPages);
		request.setAttribute("importList", importList);
		goHistoryHome(request, response);
	}

	private void listStockItems(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyword = request.getParameter("keyword");
		String sortBy = request.getParameter("sortBy");
		if (sortBy == null || sortBy.equals("name")) {
			sortBy = "product_name";
		}
		int begin = getBegin(request);
		int numberOfPage = (productDAO.count(keyword, "product_name") - 1) / NUMBER_OF_RECORDS_PER_PAGE + 1;
		List<StockItem> stockItemList = stockItemDAO.get(begin, NUMBER_OF_RECORDS_PER_PAGE, keyword, sortBy, "ASC");
		request.setAttribute("list", stockItemList);
		request.setAttribute("numberOfPages", numberOfPage);
		goInventoryHome(request, response);
	}

	public String getOrder(HttpServletRequest request) {
		String orderBy = request.getParameter("orderBy");
		if (orderBy != null) {
			switch (orderBy) {
				case "default":
					orderBy = null;
					break;
				case "name":
					orderBy = "provider_name";
					break;
			}
		}
		return orderBy;
	}

	public int getBegin(HttpServletRequest request) {
		String pageParam = request.getParameter("page");
		int page;
		if (pageParam == null) {
			page = 1;
		} else {
			page = Integer.parseInt(pageParam);
		}
		return NUMBER_OF_RECORDS_PER_PAGE * (page - 1);
	}

	private void goHistoryHome(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                            IOException {
		request.setAttribute("tab", "inventoryHistory");
		RequestDispatcher dispatcher = request.getRequestDispatcher(HOME_PAGE);
		dispatcher.forward(request, response);
	}

	private void goInventoryHome(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                              IOException {
		request.setAttribute("tab", "inventory");
		RequestDispatcher dispatcher = request.getRequestDispatcher(HOME_PAGE);
		dispatcher.forward(request, response);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Context context = new InitialContext();
			DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/ctvv");
			importDAO = new ImportDAO(dataSource);
			productDAO = new ProductDAO(dataSource);
			providerDAO = new ProviderDAO(dataSource);
			importDetailDAO = new ImportDetailDAO(dataSource);
			stockItemDAO = new StockItemDAO(dataSource);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
