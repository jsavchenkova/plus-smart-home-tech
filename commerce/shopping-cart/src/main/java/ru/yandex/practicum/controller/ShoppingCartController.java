package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService service;

    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam String username, @RequestBody Map<UUID, Integer> dto) {
        return service.addProducts(dto, username);
    }

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        return service.getShoppingCart(username);
    }

    @DeleteMapping
    public void deleteShoppingCart(@RequestParam String username) {
        service.deactivateShoppingCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username, @RequestBody Map<UUID, Integer> dto) {
        return service.removeProducts(username, dto);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantityProducts(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request) {
        return service.changeQuantityProducts(username, request);
    }
}
