package entities;

import enums.OrderStatus;
import values.OrderItem;
import values.Price;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static enums.OrderStatus.*;

public final class Order {

    private final UUID orderId;
    private final UUID customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Price totalPrice;
    private final Instant createdAt;
    private Instant paidAt;
    private Instant completedAt;

    public Order(
            UUID orderId,
            UUID customerId,
            List<OrderItem> items,
            Price totalPrice,
            Instant createdAt
    ) {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Objects.requireNonNull(customerId, "customerId  cannot be null");
        validateItems(items);
        Objects.requireNonNull(totalPrice, "totalPrice cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");

        this.orderId = orderId;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.status = NEW;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Price getTotalPrice() {
        return totalPrice;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void markPaid(Instant paidAt) {
        validateNewStatus(PAID);
        validatePaidAt(paidAt);

        this.paidAt = paidAt;
        status = PAID;
    }

    public void markInPreparation() {
        validateNewStatus(IN_PREPARATION);
        status = IN_PREPARATION;
    }

    public void markReady() {
        validateNewStatus(READY);
        status = READY;
    }

    public void markCompleted(Instant completedAt) {
        validateNewStatus(COMPLETED);
        validateCompletedAt(completedAt);

        this.completedAt = completedAt;
        status = COMPLETED;
    }

    public void cancel() {
        validateNewStatus(CANCELLED);
        status = CANCELLED;
    }

    private void validateItems(List<OrderItem> items) {
        Objects.requireNonNull(items, "items cannot be null");
        if (items.isEmpty())
            throw new IllegalArgumentException("items cannot be empty");
        if (items.contains(null))
            throw new IllegalArgumentException("items cannot contains null");
    }

    private void validateNewStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus cannot be null");

        switch (newStatus) {
            case NEW -> throw new IllegalStateException("cannot change status to NEW");
            case PAID, CANCELLED -> {
                if (status != NEW)
                    throw new IllegalStateException("current status must be NEW");
            }
            case IN_PREPARATION -> {
                if (status != PAID)
                    throw new IllegalStateException("current status must be PAID");
            }
            case READY -> {
                if (status != IN_PREPARATION)
                    throw new IllegalStateException("current status must be IN_PREPARATION");
            }
            case COMPLETED -> {
                if (status != READY)
                    throw new IllegalStateException("current status must be READY");
            }
        }
    }

    private void validatePaidAt(Instant paidAt) {
        Objects.requireNonNull(paidAt, "paidAt cannot be null");
        if (paidAt.isBefore(createdAt))
            throw new IllegalStateException("paidAt cannot be before createdAt");
    }

    private void validateCompletedAt(Instant completedAt) {
        Objects.requireNonNull(completedAt, "completedAt cannot be null");
        Objects.requireNonNull(paidAt, "paidAt cannot be null");
        if (completedAt.isBefore(paidAt))
            throw new IllegalStateException("completedAt cannot be before paidAt");
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Order order = (Order) object;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", status=" + status +
                '}';
    }
}
