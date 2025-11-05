package com.FoodOrderingApplication.FOA.Service;

import com.FoodOrderingApplication.FOA.Common.Constants.OrderStatus;
import com.FoodOrderingApplication.FOA.Common.Exceptions.OrderException;
import com.FoodOrderingApplication.FOA.Common.Exceptions.RegistrationException;
import com.FoodOrderingApplication.FOA.Common.Exceptions.ResourceNotFoundException;
import com.FoodOrderingApplication.FOA.Model.*;
import com.FoodOrderingApplication.FOA.Repository.Interfaces.*;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FoodOrderingService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public FoodOrderingService(UserRepository userRepository, RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public String userRegistration(String userName, String email, String phoneNumber) {
        if (userRepository.findByEmail(email) != null) {
            throw new RegistrationException("User with this email already exists.");
        }
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        return user.getUserId();
    }

    @Transactional
    public String restaurantRegistration(String restaurantName, String gstNumber, String emailId, String phoneNumber) {
        if (restaurantRepository.findByRestaurantName(restaurantName) != null) {
            throw new RegistrationException("Restaurant with this name already exists.");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(restaurantName);
        restaurant.setGstNumber(gstNumber);
        restaurant.setEmailId(emailId);
        restaurant.setPhoneNumber(phoneNumber);
        restaurantRepository.save(restaurant);
        return restaurant.getRestaurantId();
    }

    @Transactional
    public void addItemsInCatalog(String restaurantName, String itemName, double price, int quantity) {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName);
        if (restaurant == null) {
            throw new ResourceNotFoundException("Restaurant not found: " + restaurantName);
        }
        MenuItem item = new MenuItem();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setRestaurant(restaurant);
        menuItemRepository.save(item);
    }

    public List<MenuItem> searchItem(String restaurantName, String itemName) {
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName);
        if (restaurant == null) {
            throw new ResourceNotFoundException("Restaurant not found: " + restaurantName);
        }
        List<MenuItem> items = menuItemRepository.findByRestaurantAndItemName(restaurant, itemName);
        return items.stream()
                .sorted(Comparator.comparing(MenuItem::getPrice))
                .collect(Collectors.toList());
    }

    @Transactional
    public String placeOrder(String userId, String restaurantName, String itemName, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Restaurant restaurant = restaurantRepository.findByRestaurantName(restaurantName);
        if (restaurant == null) {
            throw new ResourceNotFoundException("Restaurant not found");
        }
        MenuItem menuItem = menuItemRepository.findByRestaurantAndItemName(restaurant, itemName).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in restaurant catalog"));

        if (menuItem.getQuantity() < quantity) {
            throw new OrderException("Requested quantity not available. Available: " + menuItem.getQuantity());
        }

        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setRestaurantName(restaurantName);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderTime(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setItemName(itemName);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(menuItem.getPrice());
        orderItem.setOrder(order);

        order.setItems(List.of(orderItem));
        order.setTotalAmount(menuItem.getPrice() * quantity);

        menuItem.setQuantity(menuItem.getQuantity() - quantity);
        menuItemRepository.save(menuItem);

        orderRepository.save(order);
        orderItemRepository.save(orderItem);

        return order.getOrderId();
    }

    public List<Order> getOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderException("Order is already cancelled.");
        }

        for (OrderItem item : order.getItems()) {
            Restaurant restaurant = restaurantRepository.findByRestaurantName(order.getRestaurantName());
            if (restaurant != null) {
                List<MenuItem> catalogItems = menuItemRepository.findByRestaurantAndItemName(restaurant, item.getItemName());
                if (!catalogItems.isEmpty()) {
                    MenuItem catalogItem = catalogItems.get(0);
                    catalogItem.setQuantity(catalogItem.getQuantity() + item.getQuantity());
                    menuItemRepository.save(catalogItem);
                }
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}