package com.aura.photography.repository;

import com.aura.photography.model.Order;
import com.aura.photography.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findByUserOrderByOrderDateDesc(User user);
}