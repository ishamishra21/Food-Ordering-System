package com.FoodOrderingApplication.FOA.Service;

import com.FoodOrderingApplication.FOA.Model.Order;
import com.FoodOrderingApplication.FOA.Model.Restaurant;
import com.FoodOrderingApplication.FOA.Model.User;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
public class DataStore {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Restaurant> restaurants = new HashMap<>();
    private final Map<String, Order> orders = new HashMap<>();

}