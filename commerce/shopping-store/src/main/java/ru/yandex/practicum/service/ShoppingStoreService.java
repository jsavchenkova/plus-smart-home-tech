package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ProductCategory;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ShoppingStoreRepository repository;

    public ProductDto cteateProduct(ProductDto dto){
        Product product = ProductMapper.INSTANCE.dtoToProduct(dto);
        Product result = repository.save(product);

        return ProductMapper.INSTANCE.productToDto(result);
    }

    public List<ProductDto> getProducts(ProductCategory category, int page, int size, String sort){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        List<Product> products = repository.findByProductCategory(category, pageable);
        return products.stream()
                .map(ProductMapper.INSTANCE::productToDto)
                .toList();
    }

    public ProductDto getProduct(UUID productId){
        Optional<Product> product = repository.findById(productId);
        if(product.isEmpty()) return null;

        return ProductMapper.INSTANCE.productToDto(product.get());
    }

    public ProductDto update(ProductDto dto){
        
    }
}
