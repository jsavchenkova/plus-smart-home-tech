package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.*;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    public void createProduct(@RequestBody NewProductInWarehouseRequest product);


    @PostMapping("/check")
    public BookedProductsDto checkQuantity(@RequestBody ShoppingCartDto cart);


    @PostMapping("/add")
    public void addProduct(@RequestBody AddProductToWarehouseRequest product);


    @GetMapping("/address")
    public AddressDto getaddres();

}
