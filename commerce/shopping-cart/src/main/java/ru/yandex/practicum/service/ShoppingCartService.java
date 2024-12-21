package ru.yandex.practicum.service;

import jdk.jfr.StackTrace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.ShoppingCartStatus;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.ShoppingCartNotFoundException;
import ru.yandex.practicum.mapper.BookedProductMapper;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.BookedProduct;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.BookedProductRepository;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final BookedProductRepository bookedProductRepository;

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

    public ShoppingCartDto getShoppingCart(String userName) {
        Optional<ShoppingCart> cart = repository.findByUserNameAndStatus(userName, ShoppingCartStatus.ACTIVE);
        if (cart.isEmpty()) {
            return null;
        }
        return ShoppingCartMapper.INSTANCE.shoppingCartToDto(cart.get());
    }

    public void deactivateShoppingCart(String userName) {
        Optional<ShoppingCart> cart = repository.findByUserNameAndStatus(userName, ShoppingCartStatus.ACTIVE);
        if (cart.isEmpty()) {
            throw new ShoppingCartNotFoundException("Корзина не найдена");
        }
        cart.get().setStatus(ShoppingCartStatus.DEACTIVATE);
        repository.save(cart.get());
    }

    public ShoppingCartDto removeProducts(String userName, Map<UUID, Integer> products) {
        Optional<ShoppingCart> cart = repository.findByUserNameAndStatus(userName, ShoppingCartStatus.ACTIVE);
        if (cart.isEmpty()) {
            throw new ShoppingCartNotFoundException("Корзина не найдена");
        }
        for (UUID u : products.keySet()) {

            cart.get().products.remove(u);
        }
        return ShoppingCartMapper.INSTANCE.shoppingCartToDto(repository.save(cart.get()));
    }

    public ShoppingCartDto changeQuantityProducts(String userName, ChangeProductQuantityRequest request) {
        Optional<ShoppingCart> cart = repository.findByUserNameAndStatus(userName, ShoppingCartStatus.ACTIVE);
        if (cart.isEmpty()) {
            throw new ShoppingCartNotFoundException("Корзина не найдена");
        }
        if (!cart.get().products.containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Товар не найден", new Throwable());
        }
        cart.get().products.put(request.getProductId(), request.getNewQuantity());

        return ShoppingCartMapper.INSTANCE.shoppingCartToDto(repository.save(cart.get()));
    }

    public BookedProductsDto bookedProducts(String userName) {
        Optional<ShoppingCart> cart = repository.findByUserNameAndStatus(userName, ShoppingCartStatus.ACTIVE);
        if (cart.isEmpty()) {
            throw new ShoppingCartNotFoundException("Корзина не найдена");
        }
        BookedProduct bookedProduct = new BookedProduct();
        bookedProduct.setCart(cart.get());
        bookedProduct.setFragile(true);
        bookedProduct.setDeliveryVolume(0.0);
        bookedProduct.setDeliveryWeight(0.0);
        return BookedProductMapper.INSTANCE.bookedProductsToDto(bookedProductRepository.save(bookedProduct));
    }

}
