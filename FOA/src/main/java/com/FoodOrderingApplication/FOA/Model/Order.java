package com.FoodOrderingApplication.FOA.Model;

import com.FoodOrderingApplication.FOA.Common.Constants.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;
    private String userId;
    private String restaurantName;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime orderTime;
    private double totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}
