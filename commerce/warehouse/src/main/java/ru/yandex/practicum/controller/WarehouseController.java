package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.Service.WarehouseService;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService service;

    @PutMapping
    public void createProduct(@RequestBody NewProductInWarehouseRequest product) {
        service.createProduct(product);
    }

    @PostMapping("/check")
    public BookedProductsDto checkQuantity(@RequestBody ShoppingCartDto cart) {
        return service.checkQuantity(cart);
    }

    @PostMapping("/add")
    public void addProduct(@RequestBody AddProductToWarehouseRequest product) {
        service.addProduct(product);
    }

    @GetMapping("/address")
    public AddressDto getaddres() {
        return service.getaddres();
    }

    @Override
    public void shipped(ShippedToDeliveryRequest request) {
        service.shipped(request);
    }

    @Override
    public void returnProducts(Map<UUID, Integer> products) {
        service.returnProducts(products);
    }

    @Override
    public BookedProductsDto assembly(AssemblyProductsForOrderRequest request) {
        return service.assembly(request);
    }
}
