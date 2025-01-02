package ru.yandex.practicum.client;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

@Component
public class ShoppingCartFallback implements ShoppingCartClient {
    @Override
    public ShoppingCartDto addProducts(String userName, Map<UUID, Integer> dto) {
        return null;
    }

    @Override
    public ShoppingCartDto getShoppingCart(String userName) {
        return null;
    }

    @Override
    public void deleteShoppingCart(String userName) {

    }

    @Override
    public ShoppingCartDto removeProducts(String userName, Map<UUID, Integer> dto) {
        return null;
    }

    @Override
    public ShoppingCartDto changeQuantityProducts(String userName, ChangeProductQuantityRequest request) {
        return null;
    }
}
