package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProductDto {
    private String productId;
    private String productName;
    private String description;
    private String imageSrc;
    private QuantityState quantityState;
    private ProductState productState;
    @Max(value = 5)
    @Min(value = 1)
    private double rating;
    private ProductCategory productCategory;
    @Min(value = 1)
    private double price;
}
