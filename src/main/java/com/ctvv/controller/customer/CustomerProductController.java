package com.ctvv.controller.customer;

import com.ctvv.dao.CategoryDAO;
import com.ctvv.dao.ProductDAO;
import com.ctvv.model.CartItem;
import com.ctvv.model.Category;
import com.ctvv.model.Product;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CustomerProductController", value = "/products/*")
public class CustomerProductController
		extends HttpServlet {
	private final int NUMBER_OF_RECORDS_PER_PAGE = 20;
	private ProductDAO productDAO;
	private CategoryDAO categoryDAO;
	private HttpSession session;


	@Override
	protected void doGet(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession();
		List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
		if (cart != null) {
			for (CartItem cartItem : cart) {
				cartItem.setProduct(productDAO.get(cartItem.getProduct().getProductId()));
			}

		}
		// Search
		if (request.getRequestURI().equals(request.getContextPath() + "/products/search")) search(request, response);
		else {
			String pageParam = request.getParameter("page");
			String categoryName = request.getParameter("category");
			//
			if (pageParam == null && categoryName == null) {
				if (request.getRequestURI().equals(request.getContextPath() + "/products")) {
					response.sendRedirect(request.getContextPath());

				} else {
					viewProductDetail(request, response);
				}
			} else {
				Integer categoryId;
				if (categoryName != null) {
					Category category = categoryDAO.find(categoryName);
					if (category != null)
						categoryId = category.getCategoryId();
					else categoryId = null;
				} else categoryId = null;
				List<Product> productList;
				// Lấy sortBy, order
				String sortBy = getSortBy(request);
				String order = request.getParameter("order");
				// Lấy page (lấy phần tử bắt đầu)
				int begin = getBegin(request);
				productList = productDAO.get(begin, NUMBER_OF_RECORDS_PER_PAGE, categoryId, sortBy, order);

				request.setAttribute("productList", productList);
				List<Category> categoryList = categoryDAO.getAll();
				request.setAttribute("categoryList", categoryList);
				int numberOfPages = (productDAO.count(categoryId) - 1) / NUMBER_OF_RECORDS_PER_PAGE + 1;
				request.setAttribute("numberOfPages", numberOfPages);
				RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/home/home.jsp");
				dispatcher.forward(request, response);
			}
		}

	}

	private void search(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                     IOException {
		String keyword = request.getParameter("keyword");
		String sortBy = getSortBy(request);
		String order = request.getParameter("order");
		int begin = getBegin(request);
		int minPrice;
		int maxPrice;
		if (request.getParameter("minPrice") != null) minPrice = Integer.parseInt(request.getParameter("minPrice"));
		else minPrice = 0;
		if (request.getParameter("maxPrice") != null) maxPrice = Integer.parseInt(request.getParameter("maxPrice"));
		else maxPrice = Integer.MAX_VALUE;

		List<Product> productList;
		productList = productDAO.get(begin, NUMBER_OF_RECORDS_PER_PAGE, keyword, minPrice, maxPrice, sortBy, order);

		int numberOfPages = (productDAO.count(keyword, minPrice, maxPrice) - 1) / NUMBER_OF_RECORDS_PER_PAGE + 1;
		request.setAttribute("numberOfPages", numberOfPages);
		request.setAttribute("search", true);
		request.setAttribute("productList", productList);
		goHome(request, response);
	}

	private void viewProductDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                                IOException {

		String requestURI = request.getPathInfo(); // chuỗi kq sẽ là như này "/ten-san-pham"
		String productURI = requestURI.substring(1);
		Product product = productDAO.get(productURI);
		request.setAttribute("product", product);
		if (product != null) {
			Category category = product.getCategory();
			Integer categoryId;
			if (category != null) {
				categoryId = category.getCategoryId();
			} else categoryId = null;
			List<Product> similarProducts = productDAO.get(0, 6, categoryId, null, null);
			similarProducts.remove(product);
			request.setAttribute("similarProducts", similarProducts);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/home/product.jsp");
		dispatcher.forward(request, response);
	}

	public String getSortBy(HttpServletRequest request) {
		String sortBy = request.getParameter("sortBy");
		if (sortBy != null) {
			switch (sortBy) {
				case "price":
					sortBy = "price";
					break;
				default:
					sortBy = null;
					break;
			}
		}
		return sortBy;
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

	private void goHome(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                     IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/home/home.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	@Override
	public void init() throws ServletException {
		super.init();
		productDAO = new ProductDAO();
		categoryDAO = new CategoryDAO();
	}
}
