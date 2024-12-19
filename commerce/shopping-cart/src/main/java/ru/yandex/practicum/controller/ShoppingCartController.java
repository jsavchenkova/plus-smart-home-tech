package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
}
