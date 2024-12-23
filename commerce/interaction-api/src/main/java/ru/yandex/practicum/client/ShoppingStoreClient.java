package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {
    @PutMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto);

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId);

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@RequestParam UUID productId, @RequestParam String quantityState);
}
