package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    void createProduct(@RequestBody NewProductInWarehouseRequest product);


    @PostMapping("/check")
    BookedProductsDto checkQuantity(@RequestBody ShoppingCartDto cart);


    @PostMapping("/add")
    void addProduct(@RequestBody AddProductToWarehouseRequest product);


    @GetMapping("/address")
    AddressDto getaddres();

    @PostMapping("/shipped")
    void shipped(ShippedToDeliveryRequest request);

    @PostMapping("/return")
    void returnProducts(Map<UUID, Integer> products);

    @PostMapping("/assembly")
    BookedProductsDto assembly(AssemblyProductsForOrderRequest request);

}
