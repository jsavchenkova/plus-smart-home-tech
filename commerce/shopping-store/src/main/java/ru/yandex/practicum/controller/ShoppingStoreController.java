package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService service;

    @PutMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return service.cteateProduct(productDto);
    }

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {

        return service.getProducts(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProduct(productId);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return service.update(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody UUID productId) {
        return service.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam UUID productId, @RequestParam String quantityState) {
        return service.setQuantityState(productId, quantityState);
    }
}
