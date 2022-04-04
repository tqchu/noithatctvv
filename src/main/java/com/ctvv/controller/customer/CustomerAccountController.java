package com.ctvv.controller.customer;

import com.ctvv.dao.AdminDAO;
import com.ctvv.dao.ShippingAddressDAO;
import com.ctvv.model.Customer;
import com.ctvv.model.ShippingAddress;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@WebServlet(name = "CustomerAccountController", value = "/user/account")
public class CustomerAccountController
        extends HttpServlet {

    HttpSession session;
    private ShippingAddressDAO shippingAddressDAO;

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tab = request.getParameter("tab");
        if (tab == null) {
            response.sendRedirect(request.getContextPath() + "/user/account?tab=profile");
        } else {
            request.setAttribute("tab", tab);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/account/manage-account.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Đặt charaterEncoding của request param thành UTF-8
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String action = request.getParameter("action");
        switch (action) {
            case "updateProfile":
                updateProfile(request, response);
            case "changePassword":
                changePassword(request, response);
            case "updateAddress":
                updateAddress(request, response);
        }
    }

    private void updateAddress(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        request.setAttribute("tab", "address");
        session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        ShippingAddress shippingAddress = customer.getAddress();

        // Lấy dữ liệu từ form
        String recipient_name = request.getParameter("recipientName");
        String address = request.getParameter("address");
        String phone_number = request.getParameter("recipientPhoneNumber");

        // Tạo 1 bản sao của shippingAddress (session)
        ShippingAddress updatedShippingAddress = new ShippingAddress(shippingAddress);
        updatedShippingAddress.setRecipientName(recipient_name);
        updatedShippingAddress.setPhoneNumber(phone_number);
        updatedShippingAddress.setAddress(address);

        try {
            updatedShippingAddress = shippingAddressDAO.update(updatedShippingAddress);
            customer.setAddress(updatedShippingAddress);
            //Đặt lời nhắn thành công
            request.setAttribute("successMessage", "Cập nhật thành công");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/account/manage-account.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) {

        session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");


    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Dòng bắt buộc để tạo dataSource
            Context context = new InitialContext();
            // Tạo và gán dataSource cho adminDAO
            DataSource dataSource = (DataSource) context.lookup("java:comp/env/jdbc/ctvv");
            shippingAddressDAO = new ShippingAddressDAO(dataSource);
        } catch (NamingException e) {
            // Chưa tìm ra cách xử lý hợp lý
            e.printStackTrace();
        }
    }
}
