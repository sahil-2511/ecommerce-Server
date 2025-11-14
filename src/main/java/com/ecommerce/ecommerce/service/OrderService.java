package com.ecommerce.ecommerce.service;

import java.util.List;
import java.util.Set;

import com.ecommerce.ecommerce.domain.OrderStatus;
import com.ecommerce.ecommerce.model.Address;
import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.OrderItem;
import com.ecommerce.ecommerce.model.User;

public interface OrderService {

    Set<Order>CreateOrder(User user ,Address shippingAddress,Cart cart);
    Order findOrderBy(Long id);
    List <Order>userOrderHistory(Long  userId);
    List<Order>sellerOrder(Long sellerId);
    Order updateOrderStatus(Long orderId, OrderStatus orderStatus);
    Order cancelOrder(Long orderId,  User user);
    OrderItem getOrderItemById(Long id) throws Exception;
    
    
}
