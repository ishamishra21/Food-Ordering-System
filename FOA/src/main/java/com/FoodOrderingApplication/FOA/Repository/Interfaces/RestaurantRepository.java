package com.FoodOrderingApplication.FOA.Repository.Interfaces;

import com.FoodOrderingApplication.FOA.Model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    Restaurant findByRestaurantName(String restaurantName);
}
