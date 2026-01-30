package com.aura.photography.controller;

import com.aura.photography.model.Cart;
import com.aura.photography.model.Gear;
import com.aura.photography.model.User;
import com.aura.photography.repository.CartRepository;
import com.aura.photography.repository.GearRepository;
import com.aura.photography.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private GearRepository gearRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            System.out.println("=============");
            String userEmail = authentication.getName();
            System.out.println(userEmail);
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            Long gearId = Long.valueOf(request.get("gearId").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());
            String duration = request.get("duration").toString();
            double totalPrice = Double.parseDouble(request.get("totalPrice").toString());
            
            Gear gear = gearRepository.findById(gearId).orElse(null);
            if (gear == null) {
                return ResponseEntity.badRequest().body("Gear not found");
            }
            
            Cart cartItem = new Cart(user, gear, quantity, duration, totalPrice);
            cartRepository.save(cartItem);
            
            return ResponseEntity.ok().body(Map.of("message", "Item added to cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add item to cart");
        }
    }
}
