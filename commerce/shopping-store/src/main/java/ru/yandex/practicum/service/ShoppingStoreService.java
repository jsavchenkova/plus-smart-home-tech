package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductState;
import ru.yandex.practicum.dto.QuantityState;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ShoppingStoreRepository repository;

    public ProductDto cteateProduct(ProductDto dto) {
        Product product = ProductMapper.INSTANCE.dtoToProduct(dto);
        Product result = repository.save(product);

        return ProductMapper.INSTANCE.productToDto(result);
    }

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        return repository.findAllByProductCategory(category, pageable)
                .map(ProductMapper.INSTANCE::productToDto);
    }

    public ProductDto getProduct(UUID productId) {
        Optional<Product> product = repository.findById(productId);
        if (product.isEmpty()) return null;

        return ProductMapper.INSTANCE.productToDto(product.get());
    }

    public ProductDto update(ProductDto dto) {
        Optional<Product> product = repository.findById(dto.getProductId());
        if (product.isEmpty()) throw new NotFoundException(String.format("Продукт id: %s  не найден"));
        Product updProduct = ProductMapper.INSTANCE.dtoToProduct(dto);
        Product result = repository.save(updProduct);

        return ProductMapper.INSTANCE.productToDto(result);
    }

    public Boolean removeProduct(UUID productId) {
        Optional<Product> product = repository.findById(productId);
        if (product.isEmpty()) throw new NotFoundException(String.format("Продукт id: %s  не найден", productId));

        Product p = product.get();
        p.setProductState(ProductState.DEACTIVATE);
        repository.save(p);
        return true;
    }

    public Boolean setQuantityState(UUID productId, String state) {
        Optional<Product> product = repository.findById(productId);
        if (product.isEmpty()) throw new NotFoundException(String.format("Продукт id: %s  не найден", productId));

        Product p = product.get();
        p.setQuantityState(QuantityState.valueOf(state));
        repository.save(p);
        return true;

    }

    public long getSize() {
        return repository.count();
    }
}
