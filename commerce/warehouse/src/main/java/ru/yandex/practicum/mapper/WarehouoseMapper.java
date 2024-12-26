package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.ProductWarehouse;

@Mapper
public interface WarehouoseMapper {
    WarehouoseMapper INSTANCE = Mappers.getMapper(WarehouoseMapper.class);

    ProductWarehouse newProductInWarehouseRequestToProductWarehouse(NewProductInWarehouseRequest request);
}
