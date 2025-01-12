package ru.yandex.practicum.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.BookedProductMapper;
import ru.yandex.practicum.mapper.WarehouoseMapper;
import ru.yandex.practicum.model.BookedProduct;
import ru.yandex.practicum.model.ProductWarehouse;
import ru.yandex.practicum.repository.BookedProductRepository;
import ru.yandex.practicum.repository.ProductWarehouseRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final ProductWarehouseRepository repository;
    private final BookedProductRepository bookedProductRepository;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];


    public void createProduct(NewProductInWarehouseRequest request) {
        ProductWarehouse product = WarehouoseMapper.INSTANCE.newProductInWarehouseRequestToProductWarehouse(request);
        Optional<ProductWarehouse> wp = repository.findByProductId(request.getProductId());
        if (wp.isPresent()) throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже добавлен на склад");
        repository.save(product);
    }

    public BookedProductsDto checkQuantity(ShoppingCartDto dto) {
        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        for (UUID id : dto.getProducts().keySet()) {
            Optional<ProductWarehouse> pw = repository.findByProductId(id);
            if (pw.isEmpty()) {
                throw new NoSpecifiedProductInWarehouseException(HttpStatus.BAD_REQUEST, "Продукт не найден на скаде");
            }
            if (pw.get().getQuantity() < dto.getProducts().get(id)) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(HttpStatus.BAD_REQUEST,
                        "Товар из корзины не находится в требуемом количестве на складе");
            }
            bookedProductsDto.setDeliveryVolume(bookedProductsDto.getDeliveryVolume() +
                    pw.get().getDimension().getHeight() *
                            pw.get().getDimension().getDepth() *
                            pw.get().getDimension().getWidth());
            bookedProductsDto.setDeliveryWeight(bookedProductsDto.getDeliveryWeight() +
                    pw.get().getWeight());
            if (pw.get().getFragile()) {
                bookedProductsDto.setFragile(true);
            }

        }
        return bookedProductsDto;
    }

    public void addProduct(AddProductToWarehouseRequest request) {
        Optional<ProductWarehouse> pw = repository.findByProductId(request.getProductId());
        if (pw.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException(HttpStatus.BAD_REQUEST,"Продукт не найден на скаде");
        }
        pw.get().setQuantity(pw.get().getQuantity() + request.getQuantity());

        repository.save(pw.get());
    }

    public AddressDto getaddres() {
        return AddressDto.builder()
                .city(CURRENT_ADDRESS)
                .country(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .build();
    }

    public void shipped(ShippedToDeliveryRequest request) {
        Optional<BookedProduct> bookedProduct = bookedProductRepository.findByOrderId(request.getOrderId());
        bookedProduct.get().setDeliveryId(request.getDeliveryId());
        bookedProductRepository.save(bookedProduct.get());
    }

    public void returnProducts(Map<UUID, Integer> products) {
        for (UUID key: products.keySet()){
            Optional<ProductWarehouse> productWarehouse = repository.findByProductId(key);
            if(productWarehouse.isEmpty()){
                throw new NoSpecifiedProductInWarehouseException(HttpStatus.NOT_FOUND, "Товар не найден");
            }
            productWarehouse.get().setQuantity(productWarehouse.get().getQuantity() +
                    products.get(key));
            repository.save(productWarehouse.get());
        }
    }

    public BookedProductsDto assembly(AssemblyProductsForOrderRequest request) {
        BookedProduct bookedProduct = new BookedProduct();
        bookedProduct.setOrderId(request.getOrderId());
        bookedProduct.setDeliveryVolume(0);
        bookedProduct.setDeliveryWeight(0);
        for(UUID key: request.getProducts().keySet()){
            Optional<ProductWarehouse> productWarehouse = repository.findByProductId(key);
            if(productWarehouse.isEmpty()){
                throw new NoSpecifiedProductInWarehouseException(HttpStatus.NOT_FOUND, "Товар не найден");
            }
            if(productWarehouse.get().getQuantity() < request.getProducts().get(key)){
                throw new ProductInShoppingCartLowQuantityInWarehouse(HttpStatus.NOT_FOUND, "Товар не находится в требуемом количестве на складе");
            }
            productWarehouse.get().setQuantity(productWarehouse.get().getQuantity()-request.getProducts().get(key));
            repository.save(productWarehouse.get());
            bookedProduct.setDeliveryWeight(bookedProduct.getDeliveryWeight() + productWarehouse.get().getWeight()*request.getProducts().get(key));
            Double volume = productWarehouse.get().getDimension().getDepth() * productWarehouse.get().getDimension().getHeight() * productWarehouse.get().getDimension().getWidth();
            bookedProduct.setDeliveryVolume(volume);
            if(productWarehouse.get().getFragile()){
                bookedProduct.setFragile(true);
            }
        }
        return BookedProductMapper.INSTANCE.bookedProductToDto(bookedProductRepository.save(bookedProduct));
    }
}
