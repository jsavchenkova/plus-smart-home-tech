package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductDto {
    private UUID productId;
    private String productName;
    private String description;
    private String imageSrc;
    private QuantityState quantityState;
    private ProductState productState;
    private ProductCategory productCategory;
    @Min(value = 1)
    private double price;
}
