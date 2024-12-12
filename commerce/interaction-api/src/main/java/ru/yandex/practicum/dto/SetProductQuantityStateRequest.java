package ru.yandex.practicum.dto;

import lombok.Data;

@Data
public class SetProductQuantityStateRequest {
    private String productId;
    private String quantityState;
}
