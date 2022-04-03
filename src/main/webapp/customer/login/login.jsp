<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%-- set context path--%>
<c:set var="context" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập</title>
    <link  rel="shortcut icon" href="${context}/favicon.ico">

    <link rel="stylesheet" href="${context}/css/base.css">

    <link rel="stylesheet" href="${context}/css/style.css">
    <!-- RESET CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css"
          integrity="sha512-NmLkDIU1C/C88wi324HBc+S2kLhi08PN5GDeUVVVC/BVt/9Izdsc9SVeVfA1UZbY3sHUlDSyRXhCzHfr6hmPPw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <!-- BOOSTRAP CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FONT   -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;600;700&display=swap"
          rel="stylesheet">

</head>
<body>
<jsp:include page="../common/search-header.jsp"/>


<div class="login-box">
    <div class="login__heading-text">Đăng nhập</div>
    <c:if test="${loginMessage!=null}">
        <div class="login__fail-message">${loginMessage}</div>
    </c:if>
    <form action="${context}/" method="post" class="login-form" autocomplete="off">
        <div class="form-group form-floating">
            <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" placeholder="Số điện thoại">
            <label for="phoneNumber" class="form-label">Số điện thoại</label>
        </div>
        <div class="form-group form-floating">
            <input type="password" class="form-control" id="password" name="password" placeholder="Mật khẩu">
            <label for="password" class="form-label">Mật khẩu</label>
        </div>
        <a href="${context}/forgot-password" class="login__forget-password">Quên mật khẩu?</a>
        <button type="submit" class="btn submit-btn">Đăng nhập</button>
    </form>
    <div class="register-suggestion">
        Người dùng mới? <a href="${context}/register" class="register-suggestion-link">Đăng ký</a>
    </div>

</div>
<jsp:include page="../../common/footer.jsp"/>
</body>
</html>