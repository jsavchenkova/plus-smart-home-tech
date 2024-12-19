package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.ShoppingCartStatus;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository repository;

    public ShoppingCartDto addProducts(Map<UUID, Integer> dto, String userName) {
        Optional<ShoppingCart> cart = repository.findByUserNameAndStatus(userName, ShoppingCartStatus.ACTIVE);
        ShoppingCart curCart;
        if (cart.isEmpty()) {
            curCart = new ShoppingCart();
            curCart.setStatus(ShoppingCartStatus.ACTIVE);
            curCart.setUserName(userName);
            repository.save(curCart);
        } else {
            curCart = cart.get();
        }

        curCart.products = dto;
        curCart = repository.save(curCart);

        return ShoppingCartMapper.INSTANCE.shoppingCartToDto(curCart);
    }
}
