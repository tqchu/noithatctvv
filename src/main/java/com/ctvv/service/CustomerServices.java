package com.ctvv.service;

import com.ctvv.dao.CustomerDAO;
import com.ctvv.dao.ShippingAddressDAO;
import com.ctvv.model.Customer;
import com.ctvv.model.ShippingAddress;
import com.ctvv.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerServices {
	private final String CUSTOMER_LOGIN_SERVLET = "/login";
	private final String CUSTOMER_LOGIN_PAGE = "/customer/login/login.jsp";
	final int NUMBER_OF_RECORDS_PER_MANAGE_CUSTOMER_PAGE = 10;
	private final String MANAGE_CUSTOMER_PAGE = "/admin/manage/home.jsp";
	private CustomerDAO customerDAO = new CustomerDAO();
	private ShippingAddressDAO shippingAddressDAO = new ShippingAddressDAO();
	private HttpSession session;


	public void listCustomers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyword = request.getParameter("keyword");
		List<Customer> customerList;
		int begin = RequestUtils.getBegin(request,NUMBER_OF_RECORDS_PER_MANAGE_CUSTOMER_PAGE);
		customerList = customerDAO.get(begin, NUMBER_OF_RECORDS_PER_MANAGE_CUSTOMER_PAGE, keyword);
		int numberOfPages = (customerDAO.count(keyword) - 1) / NUMBER_OF_RECORDS_PER_MANAGE_CUSTOMER_PAGE + 1;
		request.setAttribute("numberOfPages", numberOfPages);
		request.setAttribute("customerList", customerList);
		request.setAttribute("tab", "customers");
		RequestDispatcher dispatcher = request.getRequestDispatcher(MANAGE_CUSTOMER_PAGE);
		dispatcher.forward(request, response);
	}

	public void changeCustomerStatus(HttpServletRequest request, HttpServletResponse response) {
		session = request.getSession();
		int customerId = Integer.parseInt(request.getParameter("id"));
		Customer customer = customerDAO.get(customerId);
		String action = request.getParameter("action");
		switch (action) {
			case "activate":
				customer.setActive(true);
				break;
			case "deactivate":
				customer.setActive(false);
				break;

		}
		customerDAO.update(customer);
		session.setAttribute("successMessage", (action.equals("activate")?"B??? kh??a":"Kh??a")+" t??i kho???n kh??ch h??ng " +
				"th??nh " +
				"c??ng");
		try {
			response.sendRedirect(request.getContextPath() + "/admin/customers");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showManageInformationPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tab = request.getParameter("tab");
		if (tab.equals("profile") || tab.equals("address") || tab.equals("password")) {
			request.setAttribute("tab", tab);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/account/manage-account.jsp");
			dispatcher.forward(request, response);
		} else
			response.sendRedirect(request.getContextPath() + "/user/account?tab=profile");
	}

	public void updateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		request.setAttribute("tab", "profile");
		session = request.getSession();

		String fullName = request.getParameter("fullName");
		Customer.Gender gender = Customer.Gender.valueOf(request.getParameter("gender"));
		LocalDate date_of_birth = LocalDate.parse(request.getParameter("dateOfBirth"));

		Customer customer = (Customer) session.getAttribute("customer");
		Customer updatedCustomer = new Customer(customer);
		updatedCustomer.setFullName(fullName);
		updatedCustomer.setGender(gender);
		updatedCustomer.setDateOfBirth(date_of_birth);

		updatedCustomer = customerDAO.update(updatedCustomer);
		session.setAttribute("customer", updatedCustomer);
		request.setAttribute("successMessage", "C???p nh???t th??nh c??ng");
		RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/account/manage-account.jsp");
		try {
			dispatcher.forward(request, response);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void changePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                            IOException {
		request.setAttribute("tab", "password");
		session = request.getSession();
		Customer customer = (Customer) session.getAttribute("customer");
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("password");
		if (PasswordHashingUtil.validatePassword(oldPassword, customer.getPassword())) {
			//?????i m???t kh???u trong database
			customer.setPassword(PasswordHashingUtil.createHash(newPassword));
			customerDAO.update(customer);
			//?????i m???t kh???u cho session hien tai
			request.setAttribute("successMessage", "?????i m???t kh???u th??nh c??ng");
		} else {
			request.setAttribute("wrongOldPasswordMessage", "Sai m???t kh???u c??");
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/account/manage-account.jsp");
		dispatcher.forward(request, response);
	}

	public void updateAddress(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		request.setAttribute("tab", "address");
		session = request.getSession();
		Customer customer = (Customer) session.getAttribute("customer");
		ShippingAddress shippingAddress = customer.getAddress();

		// L???y d??? li???u t??? form
		String recipient_name = request.getParameter("recipientName");
		String address = request.getParameter("address");

		// T???o 1 b???n sao c???a shippingAddress (session)
		ShippingAddress updatedShippingAddress = new ShippingAddress(shippingAddress);
		updatedShippingAddress.setRecipientName(recipient_name);
		updatedShippingAddress.setAddress(address);

		try {
			updatedShippingAddress = shippingAddressDAO.update(updatedShippingAddress);
			customer.setAddress(updatedShippingAddress);
			//?????t l???i nh???n th??nh c??ng
			request.setAttribute("successMessage", "C???p nh???t th??nh c??ng");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/account/manage-account.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showLoginForm(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                           IOException {
		request.setAttribute("headerAction", "????ng nh???p");
		RequestDispatcher dispatcher = request.getRequestDispatcher(CUSTOMER_LOGIN_PAGE);
		dispatcher.forward(request, response);
	}

	public void authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException,
	                                                                                          ServletException {
		session = request.getSession();
		String from = request.getParameter("from");
		if (from.equals("")) {
			from = request.getContextPath();
		}
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		Customer customer = new Customer();
		boolean isPhoneNumber = (account.indexOf('@') == -1);
		if (isPhoneNumber) {
			customer.setPhoneNumber(account);
		} else {
			customer.setEmail(account);
		}
		customer.setPassword(password);

		Customer authenticatedCustomer;
		// TH1: t??i kho???n t???n t???i
		authenticatedCustomer = customerDAO.validate(customer);
		if (authenticatedCustomer != null) {
			// T??i kho???n b??nh th?????ng (active)
			if (authenticatedCustomer.isActive()) {
				session.setAttribute("customer", authenticatedCustomer);
				String postData = (String) session.getAttribute("postData");
				if (postData != null) {
					response.setStatus(307);
					response.setHeader("Location", from);
				} else {
					response.sendRedirect(from);
				}
			}
			// T??i kho???n b??? kh??a
			else {
				session.setAttribute("loginMessage", "T??i kho???n c???a b???n ???? b??? kh??a v?? vi ph???m ch??nh s??ch c???a ch??ng " +
						"t??i");
				response.sendRedirect(request.getContextPath() + CUSTOMER_LOGIN_SERVLET);
			}

		} else {
			request.setAttribute("loginMessage", "Sai t??i kho???n ho???c m???t kh???u");
			showLoginForm(request, response);
		}
	}

	public void sendSMSOTP(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = request.getParameter("phoneNumber");

		phoneNumber = StringUtils.regionPhoneNumber(phoneNumber);
		session.setAttribute("phoneNumber", StringUtils.phoneNumberBeginWithZero(phoneNumber));
		// T???o otp
		String otp = UniqueStringUtils.otp();
		SMSUtils.send(phoneNumber, otp);
		session.setAttribute("otp", otp);
		session.setAttribute("expireTime", LocalDateTime.now().plusSeconds(SMSUtils.otpTime));
		session.setAttribute("registerPhase", "phone-otp");
	}

	public void verifyPhoneOTP(HttpServletRequest request, HttpServletResponse response) {
		String otpParam = request.getParameter("otp");
		LocalDateTime expireTime = (LocalDateTime) session.getAttribute("expireTime");
		// Tr?????ng h???p otp c??n hi???u l???c
		if (!expireTime.isBefore(LocalDateTime.now())) {
			// Success
			if (otpParam.equals(session.getAttribute("otp"))) {

				Customer customer = customerDAO.findCustomerByPhoneNumber((String) session.getAttribute("phoneNumber"
				));
				if (customer != null) {
					session.setAttribute("oldCustomer", customer);
					session.setAttribute("duplicatePhoneNumberMessage", "S??? ??i???n tho???i n??y ???? ???????c ????ng k??. " +
							" \n" +
							" B???n " +
							"mu???n" +
							" ????ng nh???p hay l???y l???i t??i kho???n?");
					session.setAttribute("substituteCustomer", customer);
					session.setAttribute("registerPhase", "phone-otp");
				} else
					// Chuy???n t???i trang email
					session.setAttribute("registerPhase", "email");
			}
			// Sai otp
			else {
				session.setAttribute("registerPhase", "phone-otp");
				session.setAttribute("errorMessage", "Sai OTP");
			}
		}
		// Tr?????ng h???p otp ???? h???t h???n
		else {
			session.setAttribute("registerPhase", "phone-otp");
			session.setAttribute("errorMessage", "OTP ???? h???t h???n");
		}
	}

	public void sendMailOTP(HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("email");
		Customer customer = null;
		customer = customerDAO.findCustomerByEmail(email);
		if (customer != null) {
			session.setAttribute("registerPhase", "email");
			session.setAttribute("errorMessage", "Email ???? ???????c s??? d???ng");
		} else {
			session.setAttribute("email", email);
			// T???o otp
			String otp = UniqueStringUtils.otp();
			EmailUtils.sendOTP(email, otp);
			session.setAttribute("otp", otp);
			session.setAttribute("expireTime", LocalDateTime.now().plusSeconds(SMSUtils.otpTime));
			session.setAttribute("registerPhase", "email-otp");
		}
	}

	public void verifyMailOTP(HttpServletRequest request, HttpServletResponse response) {
		String otpParam = request.getParameter("otp");
		LocalDateTime expireTime = (LocalDateTime) session.getAttribute("expireTime");
		// Tr?????ng h???p otp c??n hi???u l???c
		if (!expireTime.isBefore(LocalDateTime.now())) {
			// Success
			if (otpParam.equals(session.getAttribute("otp"))) {
				// Chuy???n t???i trang thi???t l???p t??i kho???n
				session.setAttribute("registerPhase", "set-up");
			}
			// Sai otp
			else {
				session.setAttribute("registerPhase", "email-otp");
				session.setAttribute("errorMessage", "Sai OTP");
			}
		}
		// Tr?????ng h???p otp ???? h???t h???n
		else {
			session.setAttribute("registerPhase", "email-otp");
			session.setAttribute("errorMessage", "OTP ???? h???t h???n");
		}
	}

	public void register(HttpServletRequest request, HttpServletResponse response) {
		String phoneNumber = (String) session.getAttribute("phoneNumber");
		session.removeAttribute("phoneNumber");
		String fullName = request.getParameter("fullName");
		String password = request.getParameter("password");
		String dateOfBirthParam = request.getParameter("dateOfBirth");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dateOfBirth = LocalDate.parse(dateOfBirthParam, formatter);
		String genderParam = request.getParameter("gender");
		Customer.Gender gender = null;
		switch (genderParam) {
			case "male":
				gender = Customer.Gender.MALE;
				break;
			case "female":
				gender = Customer.Gender.FEMALE;
				break;
			case "undefined":
				gender = Customer.Gender.OTHER;
				break;

		}
		String address = request.getParameter("address");
		ShippingAddress shippingAddress = new ShippingAddress(fullName, phoneNumber, address);
		String email = (String) session.getAttribute("email");
		session.removeAttribute("email");
		Customer customer = new Customer(password, gender, fullName, phoneNumber, dateOfBirth, email, shippingAddress);
		session.setAttribute("customer", customerDAO.create(customer));
	}

	public void takeBackAccount(HttpServletRequest request, HttpServletResponse response) {
		// ?????i sdt ng?????i d??ng c?? sang null
		Customer oldCustomer = (Customer) session.getAttribute("oldCustomer");
		oldCustomer.setPhoneNumber(null);
		CustomerDAO customerDAO = new CustomerDAO();
		customerDAO.update(oldCustomer);
		// remove c??c attribute k c???n thi???t
		session.removeAttribute("oldCustomer");
		session.removeAttribute("substituteCustomer");

		session.setAttribute("registerPhase", "email");
	}
}
