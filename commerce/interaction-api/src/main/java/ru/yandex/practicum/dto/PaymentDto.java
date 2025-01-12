package ru.yandex.practicum.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentDto {

    private UUID paymentId;
    private Double totalPayment;
    private Double deliveryTotal;
    private Double feeTotal;
}
