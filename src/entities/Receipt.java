package entities;

import enums.PaymentMethod;
import values.Amount;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Receipt(
        UUID receiptId,
        UUID orderId,
        PaymentMethod paymentMethod,
        Amount paidAmount,
        Amount change,
        Instant paidAt
) {

    public Receipt {
        Objects.requireNonNull(receiptId, "receiptId cannot be null");
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Objects.requireNonNull(paymentMethod, "paymentMethod cannot be null");
        Objects.requireNonNull(paidAmount, "paidAmount cannot be null");
        Objects.requireNonNull(change, "change cannot be null");
        Objects.requireNonNull(paidAt, "paidAt cannot be null");
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Receipt receipt = (Receipt) object;
        return Objects.equals(receiptId, receipt.receiptId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(receiptId);
    }
}