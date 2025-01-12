package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository repository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = DeliveryMapper.INSTANCE.dtoToDelivery(deliveryDto);
        return DeliveryMapper.INSTANCE.DeliveryToDto(repository.save(delivery));
    }

    public void successDelivery(UUID orderId) {
        Optional<Delivery> delivery = repository.findByOrderId(orderId);
        if(delivery.isEmpty()){
            throw new NoDeliveryFoundException(HttpStatus.NOT_FOUND, "Доставка не найдена");
        }

        delivery.get().setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery.get());
        orderClient.completedOrder(orderId);
    }

    public void pickDelivery(UUID orderId) {
        Optional<Delivery> delivery = repository.findByOrderId(orderId);
        if(delivery.isEmpty()){
            throw new NoDeliveryFoundException(HttpStatus.NOT_FOUND, "Доставка не найдена");
        }

        delivery.get().setDeliveryState(DeliveryState.IN_PROGRESS);
        repository.save(delivery.get());
        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(orderId);
        request.setDeliveryId(delivery.get().getDeliveryId());
        warehouseClient.shipped(request);
    }

    public void failDelivery(UUID orderId) {
        Optional<Delivery> delivery = repository.findByOrderId(orderId);
        if(delivery.isEmpty()){
            throw new NoDeliveryFoundException(HttpStatus.NOT_FOUND, "Доставка не найдена");
        }

        delivery.get().setDeliveryState(DeliveryState.FAILED);
        repository.save(delivery.get());
        orderClient.faildeDeliveryOrder(orderId);
    }

    public Double costDelivery(OrderDto orderDto) {
        Double cost = 5.0;
        AddressDto address = warehouseClient.getaddres();
        Double result = cost;
        switch (address.getCity()){
            case "ADDRESS_1":
                result = result + cost;
                break;
            case "ADDRESS_2":
                result = result + cost*2;
                break;
        }
        if(orderDto.getFragile()){
            result = result + cost*0.2;
        }
        result = result + orderDto.getDeliveryWeight()*0.3;
        result = result + orderDto.getDeliveryVolume()*0.2;
        if(!address.getStreet().equals(CURRENT_ADDRESS)){
            result = result + cost*0.2;
        }
        return result;
    }
}
