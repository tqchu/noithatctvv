<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<thead>
<tr>
    <th>Mã đơn hàng</th>
    <th>Tên khách hàng</th>
    <th>Thời gian đặt</th>
    <th>Tổng giá</th>
    <th></th>
    <th></th>

</tr>
</thead>
<tbody>
<c:forEach items="${orderList}" var="order" varStatus="loop">
    <tr>
        <td>${order.orderId}</td>
        <td>${order.customerName}</td>
        <td>
            <fmt:parseDate value="${order.orderTime}" type="time" var="orderTime"
                           pattern="yyyy-MM-dd'T'HH:mm:ss"/>
            <fmt:formatDate value="${orderTime}" type="TIME" pattern="dd/MM/yyyy HH:mm:ss"/>
        </td>
        <td>${order.totalPrice}</td>
        <td class="column__action">
            <a href="" class="btn-primary btn btn-edit">Xem chi tiết</a>
        </td>
        <td class="column__action">
            <a href="" class="btn-edit btn btn-primary"
               data-bs-toggle="modal"
               data-bs-target="#confirmModal${loop.count}">Xác nhận</a>
            <!-- MODAL CONTENT -->
            <div class="modal fade" id="confirmModal${loop.count}" tabindex="-1"
                 aria-labelledby="confirmModal${loop.count}Label"
                 aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">

                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="confirmModal${loop.count}Label">
                                Xác nhận
                            </h5>
                            <button type="button" class="btn-close"
                                    data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form action="${context}/admin/orders" method="post"
                                  class="d-flex align-items-center flex-column">
                                <input type="hidden" value="to-receive" name="action">
                                <input type="hidden" value="${order.orderId}" name="id">
                                Bạn có chắc chắn xác nhận đơn hàng ${order.orderId}
                                <button type="submit"
                                        class="btn btn-primary save-btn">
                                    Lưu
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </td>

    </tr>
</c:forEach>
</tbody>