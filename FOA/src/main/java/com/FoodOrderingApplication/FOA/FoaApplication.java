package com.FoodOrderingApplication.FOA;

import com.FoodOrderingApplication.FOA.Common.Exceptions.OrderException;
import com.FoodOrderingApplication.FOA.Common.Exceptions.RegistrationException;
import com.FoodOrderingApplication.FOA.Common.Exceptions.ResourceNotFoundException;
import com.FoodOrderingApplication.FOA.Model.MenuItem;
import com.FoodOrderingApplication.FOA.Model.Order;
import com.FoodOrderingApplication.FOA.Service.FoodOrderingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class FoaApplication {
	private static final Logger logger = LoggerFactory.getLogger(FoaApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(FoaApplication.class, args);
		FoodOrderingService service = context.getBean(FoodOrderingService.class);

		String donaldId = null;
		try {
			donaldId = service.restaurantRegistration("Donald", "GST10905804580", "Donald@mail.com", "1234567890");
			logger.info("Registered!!! RestaurantId: {}", donaldId);
		} catch (RegistrationException e) {
			logger.error("Restaurant registration failed: {}", e.getMessage());
		}

		try {
			service.addItemsInCatalog("Donald", "Sandwich", 100.00, 4);
			service.addItemsInCatalog("Donald", "Burger", 250.00, 2);
			service.addItemsInCatalog("Donald", "Pizza", 500.00, 10);
			service.addItemsInCatalog("Donald", "Fries", 80.00, 5);
		} catch (ResourceNotFoundException e) {
			logger.error("Failed to add items to catalog: {}", e.getMessage());
		}


		String userId = null;
		try {
			userId = service.userRegistration("User1", "user@mail.com", "1234567890");
			logger.info("Log: User Registered!!! UserId: {}", userId);
		} catch (RegistrationException e) {
			logger.error("User registration failed: {}", e.getMessage());
		}

		try {
			logger.info("Searching for 'Sandwich' at Donald's...");
			List<MenuItem> searchResults = service.searchItem("Donald", "Sandwich");
			searchResults.forEach(item -> logger.info("Item: {}, Price: {}, Quantity: {}", item.getItemName(), item.getPrice(), item.getQuantity()));
		} catch (ResourceNotFoundException e) {
			logger.error("Search failed: {}", e.getMessage());
		}

		String orderId1 = null;
		String orderId2 = null;
		try {
			orderId1 = service.placeOrder(userId, "Donald", "Sandwich", 2);
			logger.info("Order placed successfully, orderId: {}", orderId1);
			orderId2 = service.placeOrder(userId, "Donald", "Pizza", 1);
			logger.info("Order placed successfully, orderId: {}", orderId2);
		} catch (ResourceNotFoundException | OrderException e) {
			logger.error("Order placement failed: {}", e.getMessage());
		}


		try {
			logger.info("Getting all orders for User: {}", userId);
			List<Order> userOrders = service.getOrders(userId);
			userOrders.forEach(order -> logger.info("orderId: {}, restaurant: {}, status: {}", order.getOrderId(), order.getRestaurantName(), order.getStatus()));
		} catch (Exception e) {
			logger.error("Failed to fetch orders: {}", e.getMessage());
		}


		try {
			logger.info("Cancel Order Operation");
			if (orderId1 != null) {
				service.cancelOrder(orderId1);
				logger.info("Order {} cancelled successfully.", orderId1);
			}
		} catch (ResourceNotFoundException | OrderException e) {
			logger.error("Order cancellation failed: {}", e.getMessage());
		}

		try {
			logger.info("Getting orders after cancellation:");
			List<Order> updatedOrders = service.getOrders(userId);
			updatedOrders.forEach(order -> logger.info("orderId: {}, restaurant: {}, status: {}", order.getOrderId(), order.getRestaurantName(), order.getStatus()));
		} catch (Exception e) {
			logger.error("Failed to fetch updated orders: {}", e.getMessage());
		}
	}
}