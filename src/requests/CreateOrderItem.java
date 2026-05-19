package requests;

import values.Quantity;

import java.util.Objects;
import java.util.UUID;

public record CreateOrderItem(
        UUID menuItemId,
        Quantity quantity
) {

    public CreateOrderItem {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        Objects.requireNonNull(quantity, "quantity cannot be null");
    }
}
