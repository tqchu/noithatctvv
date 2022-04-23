<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="tab__content">
    <div class="search">
        <form action="" class="search__form">
            <input type="search" class="search__bar form-control">
            <button type="submit" class="btn btn-primary btn-search">
                <i class="las la-search"></i>
            </button>
        </form>
        <a href="${context}/admin/products?action=create" class="search__add-btn btn btn-primary"
           title="Thêm sản phẩm"><i
                class="las la-plus"></i></a>

    </div>
    <c:if test="${successMessage!=null}">
        <div class="toast align-items-center toast-message toast-message--success fade show" role="alert"
             aria-live="assertive"
             aria-atomic="true" data-bs-autohide="false">
            <div class="d-flex ">
                <div class="toast-body">
                        ${successMessage}
                </div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"
                        aria-label="Close"></button>
            </div>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <div class="list">
        <table class="data-table  table table-hover table-bordered">
            <thead>
            <tr>
                <th>STT</th>
                <th>Tên sản phẩm</th>
                <th>Ảnh sản phẩm</th>
                <th>Kích thước</th>
                <th>Vật liệu</th>
                <th>Giá</th>
                <th>Số lượng</th>
                <th>Doanh mục</th>
                <th>Đánh giá</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty list}">
                <c:forEach items="${list}" var="product" varStatus="loop">
                    <tr>
                        <td>${loop.count}</td>
                        <td class="product__name">${product.name}</td>
                        <td class="product__image">
                            <div id="product-image-slide${product.productId}" class="carousel slide"
                                 data-bs-ride="carousel">
                                <div class="carousel-inner">
                                    <c:forEach items="${product.imagePathList}" var="path"
                                               varStatus="loop">
                                        <div class="carousel-item ${loop.count==1?'active':''}">
                                            <img src="${context}/${path.path}"
                                                 class="d-block product__main-image">
                                        </div>
                                    </c:forEach>
                                </div>
                                <button class="carousel-control-prev" type="button"
                                        data-bs-target="#product-image-slide${product.productId}"
                                        data-bs-slide="prev">
                                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                    <span class="visually-hidden">Previous</span>
                                </button>
                                <button class="carousel-control-next" type="button"
                                        data-bs-target="#product-image-slide${product.productId}"
                                        data-bs-slide="next">
                                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                    <span class="visually-hidden">Next</span>
                                </button>
                            </div>

                        </td>
                        <td class="product__dimension">
                            <ul>
                                <c:forEach items="${product.dimensionList}" var="dimension">
                                    <li class="product__dimension-item">
                                            ${dimension.length}D x ${dimension.width}R x
                                            ${dimension.height}C (cm)
                                    </li>
                                </c:forEach>

                            </ul>
                        </td>
                        <td class="product__material">
                            <ul>
                                <c:forEach items="${product.materialList}" var="material">
                                    <li class="product__material-item">
                                            ${material.materialName}
                                    </li>
                                </c:forEach>

                            </ul>
                        </td>
                        <td class="product__price">
                            <fmt:formatNumber value="${product.price}"
                                              currencySymbol="đ" type="CURRENCY" maxFractionDigits="0"/>
                        </td>
                        <td class="product__quantity">${product.quantity}</td>
                        <td class="product__category">
                            <c:choose>
                                <c:when test="${product.category==null}">
                                <span class="product__no-category">
                                    Chưa phân loại
                                </span>
                                </c:when>
                                <c:otherwise>
                                    ${product.category.categoryName}
                                </c:otherwise>
                            </c:choose>

                        </td>
                        <td class="product__rating">-</td>
                        <td>
                            <form action="${context}/admin/products"  method="get">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" value="${product.productId}" name="id">
                                <button type="submit" class="btn-edit btn btn-primary">Chỉnh sửa</button>
                            </form>


                        </td>
                        <td>
                            <button class="btn btn-delete" title="Xoá sản phẩm"
                                    data-bs-toggle="modal"
                                    data-bs-target="#deleteProductModal${product.productId}">
                                Xoá
                            </button>
                            <!-- MODAL CONTENT -->
                            <div class="modal fade"
                                 id="deleteProductModal${product.productId}"
                                 tabindex="-1"
                                 aria-labelledby="deleteProductModal${product.productId}Label"
                                 aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">

                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title"
                                                id="deleteProductModal${product.productId}Label">
                                                Xóa doanh mục
                                            </h5>
                                            <button type="button" class="btn-close"
                                                    data-bs-dismiss="modal"
                                                    aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <form action="${context}/admin/products"
                                                  class="delete-form" method="post">
                                                Vì lý do về lịch sử đơn hàng, bạn không thể xóa sản phẩm này.
                                                Bạn có muốn ngừng kinh doanh sản phẩm <strong> ${product.name} </strong>?
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="productId"
                                                       value="${product.productId}">
                                                <button
                                                        class="confirm-delete-btn btn">
                                                    Xác nhận
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            </tbody>
        </table>
    </div>
</div>