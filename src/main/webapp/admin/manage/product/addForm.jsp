<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%-- set context path--%>
<c:set var="context" value="${pageContext.request.contextPath}"/>
<c:set var="rand"><%= java.lang.Math.round(java.lang.Math.random() * 10000) %>
</c:set>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login</title>
    <link rel="shortcut icon" href="${context}/favicon.ico">
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
    <!-- APP -->
    <link rel="stylesheet" href="${context}/css/base.css?rd=${rand}">
    <link rel="stylesheet" href="${context}/css/style.css?rd=${rand}">
    <link rel="stylesheet" href="${context}/css/admin/product/common.css?rd=${rand}">
    <link rel="stylesheet" href="${context}/css/admin/addForm.css?rd=${rand}">
    <!-- FONT AWESOME -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css"
          integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
</head>
<body>
<jsp:include page="../../common/header.jsp"/>
<div class="content">

    <c:if test="${errorMessage!=null}">
        <div class="toast align-items-center toast-message toast-message--error show" role="alert"
             aria-live="assertive"
             aria-atomic="true" data-bs-autohide="false">
            <div class="d-flex ">
                <div class="toast-body">
                        ${errorMessage}
                </div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"
                        aria-label="Close"></button>
            </div>
        </div>
    </c:if>
    <form action="${context}/admin/products" enctype="multipart/form-data" method="post" class="products__add-form">
        <input type="hidden" name="action" value="create">
        <div class="form-group">
            <span class="form-group__label">T??n s???n ph???m</span>

            <input type="text" name="productName" id="productName">
        </div>
        <div class="form-group">
            <span class="form-group__label">???nh</span>

            <div id="coba"></div>
        </div>

        <div class="form-group">
            <span class="form-group__label">M?? t??? s???n ph???m</span>

            <textarea name="description" id="" cols="60" rows="8"></textarea>
        </div>
        <div class="form-group">
            <span class="form-group__label">K??ch th?????c</span>
            <input type="text" name="dimension">
        </div>
        <div class="form-group">
                             <span class="form-group__label">
                                Ch???t li???u
                             </span>

            <input type="text" name="material" id="material"
            >
        </div>
        <div class="form-group">
            <span class="form-group__label">????n gi??</span>

            <input type="text" name="price">

        </div>


        <div class="form-group">
            <span class="form-group__label">Th???i gian b???o h??nh</span>
            <input type="number" name="warrantyPeriod">
            <span class="warranty-period-unit">th??ng</span>
        </div>
        <div class="form-group">
            <span class="form-group__label">Doanh m???c</span>
            <%-- SELECT ROLE --%>
            <select class="form-select category-select" name="categoryId">
                <option value="" selected>Ch??a ph??n lo???i</option>
                <c:forEach items="${categoryList}" var="category">
                    <option value="${category.categoryId}">${category.categoryName}</option>
                </c:forEach>
            </select>
        </div>


        <button class="btn btn-primary save-btn ">
            Th??m s???n ph???m
        </button>

    </form>
</div>
<%--<jsp:include page="../../common/footer.jsp"/>--%>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${context}/js/admin/product/image-picker.js?rd=${rand}"></script>
<script src="${context}/js/admin/product/common.js?rd=${rand}"></script>
<script src="${context}/js/admin/product/addProduct.js?rd=${rand}"></script>

</body>
</html>
