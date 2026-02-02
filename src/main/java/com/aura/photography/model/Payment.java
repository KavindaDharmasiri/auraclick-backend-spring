package com.aura.photography.model;

import com.aura.photography.util.enums.PaymentStatus;
import com.aura.photography.util.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "card_last4")
    private String cardLast4;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "payment_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime paymentDate;
}