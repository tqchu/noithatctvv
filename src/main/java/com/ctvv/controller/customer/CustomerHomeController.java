package com.ctvv.controller.customer;

import com.ctvv.dao.CategoryDAO;
import com.ctvv.dao.CustomerDAO;
import com.ctvv.dao.ProductDAO;
import com.ctvv.model.Category;
import com.ctvv.model.Product;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CustomerHomeController", value = "")
public class CustomerHomeController
		extends HttpServlet {
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
	@Override
	protected void doGet(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        listProductAndCategory(request, response);
	}

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
    private void listProductAndCategory(
            HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                             IOException {
        List<Product> productList = productDAO.getAll();
        List<Category> categoryList = categoryDAO.getAll();
        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", categoryList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/customer/home/home.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    public void init() throws ServletException {
        super.init();

        Context context;
        try {
            context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/ctvv");
            productDAO = new ProductDAO(dataSource);
            categoryDAO = new CategoryDAO(dataSource);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}