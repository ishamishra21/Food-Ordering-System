package com.FoodOrderingApplication.FOA.Repository.Interfaces;

import com.FoodOrderingApplication.FOA.Model.MenuItem;
import com.FoodOrderingApplication.FOA.Model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, String> {
    List<MenuItem> findByRestaurantAndItemName(Restaurant restaurant, String itemName);
    List<MenuItem> findByRestaurant_RestaurantName(String restaurantName);
}
