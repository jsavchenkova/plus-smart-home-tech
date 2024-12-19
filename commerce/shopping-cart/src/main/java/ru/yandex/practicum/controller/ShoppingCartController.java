package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService service;

    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam String userName, @RequestBody Map<UUID, Integer> dto) {
        return service.addProducts(dto, userName);
    }

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String userName) {
        return service.getShoppingCart(userName);
    }

    @DeleteMapping
    public void deleteShoppingCart(@RequestParam String userName) {
        service.deactivateShoppingCart(userName);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String userName, @RequestBody Map<UUID, Integer> dto) {
        return service.removeProducts(userName, dto);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantityProducts(@RequestParam String userName, @RequestBody ChangeProductQuantityRequest request) {
        return service.changeQuantityProducts(userName, request);
    }
}
