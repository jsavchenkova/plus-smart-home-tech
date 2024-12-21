package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    @PutMapping
    public void createProduct(@RequestBody NewProductInWarehouseRequest product) {

    }

    @PostMapping("/check")
    public BookedProductsDto checkQuantity(@RequestBody ShoppingCartDto cart) {
        return null;
    }

    @PostMapping("/add")
    public void addProduct(@RequestBody AddProductToWarehouseRequest product) {

    }

    @GetMapping("/address")
    public AddressDto getaddres() {
        return null;
    }
}
