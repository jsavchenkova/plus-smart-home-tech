package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequest;

import java.util.UUID;

@FeignClient(name = "order-client")
public interface OrderClient {
    @GetMapping
    public OrderDto getOrderByUser(@RequestParam String userName);

    @PutMapping
    public OrderDto createOrder(@RequestParam String userName, CreateNewOrderRequest request);

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    public OrderDto paymentOrder (@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    public OrderDto failedPaymentOrder (@RequestBody UUID orderId);

    @PostMapping("/delivery")
    public OrderDto deliveryOrder (@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    public OrderDto faildeDeliveryOrder (@RequestBody UUID orderId);

    @PostMapping("/completed")
    public OrderDto completedOrder (@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalOrder (@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryOrder (@RequestBody UUID orderId);

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    public OrderDto failedAssemblyOrder (@RequestBody UUID orderId);

}
