package com.FoodOrderingApplication.FOA.Repository.Interfaces;

import com.FoodOrderingApplication.FOA.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

}
