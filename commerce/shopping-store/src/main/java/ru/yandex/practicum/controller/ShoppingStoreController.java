package ru.yandex.practicum.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService service;

    @PutMapping
    public ProductDto createProduct (@RequestBody ProductDto productDto){
        return service.cteateProduct(productDto);
    }

    @GetMapping
    public List<ProductDto> getProducts (@RequestParam ProductCategory category, @RequestParam Integer page,
                                         @RequestParam Integer size, @RequestParam String sort){
        return service.getProducts(category, page, size, sort);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId){
        return service.getProduct(productId);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto){

    }
}
