package com.aura.photography.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey jwtKey = Keys.hmacShaKeyFor("aura-photography-secret-key-for-jwt-tokens-2024".getBytes());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("Auth header: " + authHeader);
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query String: " + request.getQueryString());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token from header: " + token.substring(0, Math.min(20, token.length())) + "...");
        } else {
            // Check for token in query parameter (for iframe/image viewing)
            token = request.getParameter("token");
            if (token != null) {
                System.out.println("Token from query param: " + token.substring(0, Math.min(20, token.length())) + "...");
            } else {
                System.out.println("No token found in header or query param");
            }
        }

        if (token != null) {
            try {
                username = extractUsername(token);
                System.out.println("Extracted username: " + username);
            } catch (Exception e) {
                System.out.println("Token extraction failed: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (validateToken(token, username)) {
                System.out.println("Token valid, setting authentication");
                // Extract role from token
                String role = Jwts.parser()
                    .verifyWith(jwtKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role", String.class);
                
                System.out.println("User role: " + role);
                
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, 
                        java.util.Collections.singletonList(() -> "ROLE_" + role));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("Token validation failed");
            }
        }

        filterChain.doFilter(request, response);
    }
    
    private String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(jwtKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
    
    private boolean validateToken(String token, String username) {
        try {
            String tokenUsername = extractUsername(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
            .verifyWith(jwtKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();
        return expiration.before(new Date());
    }
}