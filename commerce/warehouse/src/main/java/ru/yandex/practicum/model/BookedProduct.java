package ru.yandex.practicum.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(schema = "warehouse", name = "order_booking")
public class BookedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;

    @Column
    private double deliveryWeight;
    @Column
    private double deliveryVolume;
    @Column
    private boolean fragile;
    @Column
    private UUID orderId;
    @Column
    private UUID deliveryId;
}
