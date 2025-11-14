



package com.ecommerce.ecommerce.service.impl;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.ecommerce.domain.OrderStatus;
import com.ecommerce.ecommerce.model.Address;
import com.ecommerce.ecommerce.model.Cart;
import com.ecommerce.ecommerce.model.CartItem;
import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.OrderItem;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.AddressRepository;
import com.ecommerce.ecommerce.repository.OrderRepository;
import com.ecommerce.ecommerce.repository.Orderitemrepository;
import com.ecommerce.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final Orderitemrepository orderItemRepository;

@Override
@Transactional
public Set<Order> CreateOrder(User user, Address shippingAddress, Cart cart) {
    if (!user.getAddresses().contains(shippingAddress)) {
        user.getAddresses().add(shippingAddress);
    }
    Address address = addressRepository.save(shippingAddress);

    // Group cart items by seller
    Map<Long, List<CartItem>> itemsBySeller = cart.getCartItems().stream()
        .collect(Collectors.groupingBy(item -> item.getProduct().getSeller().getId()));

    Set<Order> orders = new HashSet<>();

    // Total selling price of entire cart
    BigDecimal cartTotalSellingPrice = cart.getCartItems().stream()
        .map(CartItem::getSellingPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal cartDiscount = Optional.ofNullable(cart.getDiscount())
        .map(BigDecimal::valueOf)
        .orElse(BigDecimal.ZERO);

    for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {
        Long sellerId = entry.getKey();
        List<CartItem> items = entry.getValue();

        BigDecimal totalSellingPrice = items.stream()
            .map(CartItem::getSellingPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMrpPrice = items.stream()
            .map(CartItem::getMrpPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();

        // Calculate proportional discount for this seller
        BigDecimal sellerShareRatio = cartTotalSellingPrice.compareTo(BigDecimal.ZERO) > 0
            ? totalSellingPrice.divide(cartTotalSellingPrice, 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal discount = cartDiscount.multiply(sellerShareRatio).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPrice = totalSellingPrice.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        // Create and save order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setSellerId(sellerId);
        order.setTotalItem(totalItems);
        order.setTotalMrpPrice(totalMrpPrice);
        order.setTotalSellingPrice(totalSellingPrice);
        order.setDiscount(discount);
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        orders.add(savedOrder);

        // Create and save order items
        for (CartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMrpPrice(item.getMrpPrice());
            orderItem.setSellingPrice(item.getSellingPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSizes(item.getSize());
            orderItem.setUserId(item.getUserId());
            orderItem.setProduct(item.getProduct());

            orderItemRepository.save(orderItem);
        }
    }

    return orders;
}

    @Override
    public Order findOrderBy(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    @Override
    public List<Order> userOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    @Override
    public List<Order> sellerOrder(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }
    
    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = findOrderBy(orderId);
        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }
    @Override
    @Transactional
    public Order cancelOrder(Long orderId, User user) {
        Order order = findOrderBy(orderId);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User not authorized to cancel this order");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
    @Override
    public  OrderItem getOrderItemById (Long id) throws Exception {

        return orderItemRepository.findById(id).orElseThrow(()->
        new Exception("oder item not exist"));
    }
}
