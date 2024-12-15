package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.JavaType;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductState;
import ru.yandex.practicum.dto.QuantityState;

import java.util.UUID;

@Entity
@Table (name="product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;
    @Column
    private String productName;
    @Column
    private String description;
    @Column (nullable = true)
    private String imageSrc;
    @Column
    private QuantityState quantityState;
    @Column
    private ProductState productState;
    @Column
    private Double rating;
    @Column
    private ProductCategory productCategory;
    @Min(value = 1)
    @Column
    private double price;
}
