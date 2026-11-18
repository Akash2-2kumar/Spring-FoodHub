package com.foodhub.foodhub.Service;

import com.foodhub.foodhub.Dto.Request.OrderItemRequest;
import com.foodhub.foodhub.Dto.Request.OrderRequest;
import com.foodhub.foodhub.Dto.Response.OrderItemResponse;
import com.foodhub.foodhub.Dto.Response.OrderResponse;
import com.foodhub.foodhub.Entity.*;
import com.foodhub.foodhub.Enum.DeliveryBoyStatus;
import com.foodhub.foodhub.Enum.OrderStatus;
import com.foodhub.foodhub.Exception.FoodDoesNotExistException;
import com.foodhub.foodhub.Exception.InsufficientStockException;
import com.foodhub.foodhub.Exception.OrderDoesNotExistException;
import com.foodhub.foodhub.Exception.UserDoesNotExistException;
import com.foodhub.foodhub.Mapper.OrderMapper;
import com.foodhub.foodhub.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final DeliveryBoyApplicationRepository deliveryBoyApplicationRepository;

    @Transactional
    public OrderResponse placeOrder(Authentication authentication, OrderRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserDoesNotExistException("User not found with email: " + email));
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .totalAmount(BigDecimal.ZERO)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            Food food = foodRepository.findById(itemRequest.getFoodId()).orElseThrow(() -> new FoodDoesNotExistException("Food not found with id: "+itemRequest.getFoodId()));
            Restaurant restaurant = food.getRestaurant();

            if (!Boolean.TRUE.equals(restaurant.getActive())) {
                throw new RuntimeException(
                        "Restaurant is currently unavailable"
                );
            }
            if (food.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for food: "+food.getName());
            }
                    BigDecimal subtotal = food.getPrice()
                    .multiply(
                            BigDecimal.valueOf(
                                    itemRequest.getQuantity()
                            )
                    );
            totalAmount = totalAmount.add(subtotal);
            food.setStock(
                    food.getStock() - itemRequest.getQuantity()
            );
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .food(food)
                    .quantity(itemRequest.getQuantity())
                    .price(food.getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        return orderMapper.toResponse(
                savedOrder,
                toItemResponses(orderItems)
        );
    }

    public OrderResponse getOrderById(Long orderId,Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found with email: "+email));
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderDoesNotExistException("Order not found with id: " + orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to access this order");
        }
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderMapper.toResponse(
                order,
                toItemResponses(orderItems)
        );
    }

    public List<OrderResponse> getUserOrders(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UserDoesNotExistException("User not found with email: " + authentication.getName()));
        List<Order> orders =orderRepository.findByUserId(user.getId());
        return orders.stream()
                .map(order->{
                    List<OrderItem> orderItems=orderItemRepository.findByOrderId(order.getId());
                    return orderMapper.toResponse(order, toItemResponses(orderItems));}).toList();
    }
    private List<OrderItemResponse> toItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderMapper::toItemResponse)
                .toList();
    }
    @Transactional
    public OrderResponse cancelOrder(Long orderId,Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found with email: "+email));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderDoesNotExistException("Order not found with id: "+orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to cancel this order"
            );
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            Food food = item.getFood();
            food.setStock(food.getStock() + item.getQuantity());}
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(
                savedOrder,
                toItemResponses(orderItems)
        );
    }
    @Transactional
    public OrderResponse assignDeliveryBoy(Long orderId, Long deliveryBoyId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderDoesNotExistException("Order not found"));
        DeliveryBoyApplication application=deliveryBoyApplicationRepository.findById(deliveryBoyId).orElseThrow(()->new RuntimeException("Delivery boy not found"));
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("This order cannot be assigned");
        }
        if (order.getDeliveryBoy() != null) {
            throw new RuntimeException("Delivery boy is already assigned");
        }
        if (application.getStatus() != DeliveryBoyStatus.APPROVED) {
            throw new RuntimeException("Delivery boy is not approved");
        }
        if (!application.getAvailable()) {
            throw new RuntimeException("Delivery boy is not available");
        }
        order.setDeliveryBoy(application.getUser());
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        application.setAvailable(false);
        deliveryBoyApplicationRepository.save(application);
        Order savedOrder = orderRepository.save(order);
        List<OrderItemResponse> items = orderItemRepository
                .findByOrderId(orderId)
                .stream()
                .map(orderMapper::toItemResponse)
                .toList();
        return orderMapper.toResponse(savedOrder, items);
    }
    public List<OrderResponse> getDeliveryBoyOrders(Authentication authentication){
        String email = authentication.getName();
        User deliveryBoy = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));
        return orderRepository.findByDeliveryBoyId(deliveryBoy.getId())
                .stream()
                .map(order -> {
                    List<OrderItemResponse> items = orderItemRepository
                            .findByOrderId(order.getId())
                            .stream()
                            .map(orderMapper::toItemResponse)
                            .toList();
                    return orderMapper.toResponse(order, items);}).toList();
    }

    public OrderResponse markOrderDelivered(Long orderId, Authentication authentication){
        String email = authentication.getName();
        User deliveryBoy = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderDoesNotExistException("Order not found"));
        if (order.getDeliveryBoy() == null || !order.getDeliveryBoy().getId().equals(deliveryBoy.getId())) {
            throw new RuntimeException("This order is not assigned to you");
        }
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("Order is not out for delivery");
        }
        order.setStatus(OrderStatus.DELIVERED);
        DeliveryBoyApplication application=
                deliveryBoyApplicationRepository.findById(deliveryBoy.getId()).orElseThrow(()->new RuntimeException("Delivery boy application not found"));
        application.setAvailable(true);

        deliveryBoyApplicationRepository.save(application);
        Order savedOrder = orderRepository.save(order);
        List<OrderItemResponse> items = orderItemRepository
                .findByOrderId(orderId)
                .stream()
                .map(orderMapper::toItemResponse)
                .toList();
        return orderMapper.toResponse(savedOrder, items);
    }
    @Transactional
    public OrderResponse confirmOrder(Long orderId,Authentication authentication){
        String email = authentication.getName();
        User owner = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new OrderDoesNotExistException("Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        boolean isOwner = items.stream()
                .allMatch(item ->
                        item.getFood()
                                .getRestaurant()
                                .getOwner()
                                .getId()
                                .equals(owner.getId()));
        if (!isOwner) {
            throw new RuntimeException("You are not allowed to confirm this order");
        }
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new RuntimeException("Only placed orders can be confirmed");
        }
        order.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);
        List<OrderItemResponse> itemResponses = items.stream()
                .map(orderMapper::toItemResponse)
                .toList();
        return orderMapper.toResponse(savedOrder, itemResponses);
    }
    @Transactional
    public OrderResponse prepareOrder(Long orderId,Authentication authentication) {
        String email = authentication.getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(()->new UserDoesNotExistException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new OrderDoesNotExistException("Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        boolean isOwner = items.stream()
                .allMatch(item ->
                        item.getFood()
                                .getRestaurant()
                                .getOwner()
                                .getId()
                                .equals(owner.getId()));
        if (!isOwner) {
            throw new RuntimeException("You are not allowed to prepare this order");
        }
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed orders can be prepared");
        }
        order.setStatus(OrderStatus.PREPARING);
        Order savedOrder = orderRepository.save(order);
        List<OrderItemResponse> itemResponses = items.stream()
                .map(orderMapper::toItemResponse)
                .toList();
        return orderMapper.toResponse(savedOrder, itemResponses);
    }
    public List<OrderResponse> getRestaurantOrders(Authentication authentication){
        String email = authentication.getName();
        User owner = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));
        return orderRepository.findOrdersByRestaurantOwner(owner.getId())
                .stream()
                .map(order -> {
                    List<OrderItemResponse> items =
                            orderItemRepository.findByOrderId(order.getId())
                                    .stream()
                                    .map(orderMapper::toItemResponse)
                                    .toList();
                    return orderMapper.toResponse(order, items);}).toList();
    }
}