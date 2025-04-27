package com.aryan.userservice.dto;
import com.aryan.userservice.enums.OrderStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Long userId;
    private Long amount;
    private Long totalAmount;
    private Long discount;
    private OrderStatus orderStatus;
}
