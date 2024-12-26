package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.model.BookedProduct;

@Mapper
public interface BookedProductMapper {
    BookedProductMapper INSTANCE = Mappers.getMapper(BookedProductMapper.class);

    BookedProductsDto bookedProductsToDto(BookedProduct bookedProduct);
}
