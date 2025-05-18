package com.youngjong.kafkajsonapp.model;

import java.time.LocalDateTime;

public class OrderEvent {
    private String orderId;
    private String ProductId;
    private int quantity;
    private LocalDateTime orderedAt;

    public OrderEvent() {
    }

    public OrderEvent(String orderId, String productId, int quantity, LocalDateTime orderedAt) {
        this.orderId = orderId;
        ProductId = productId;
        this.quantity = quantity;
        this.orderedAt = orderedAt;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "orderId='" + orderId + '\'' +
                ", ProductId='" + ProductId + '\'' +
                ", quantity=" + quantity +
                ", orderedAt=" + orderedAt +
                '}';
    }
}
