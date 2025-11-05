package com.FoodOrderingApplication.FOA.Repository.Interfaces;

import com.FoodOrderingApplication.FOA.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}
