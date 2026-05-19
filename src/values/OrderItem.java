package values;

import java.util.Objects;
import java.util.UUID;

import static utilities.Validator.validateAndNormalizeName;

public record OrderItem(
        UUID menuItemId,
        String menuItemName,
        Price unitPrice,
        Quantity quantity
) {
    public OrderItem {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        menuItemName = validateAndNormalizeName(menuItemName);
        Objects.requireNonNull(unitPrice, "unitPrice cannot be null");
        Objects.requireNonNull(quantity, "quantity cannot be null");
    }

    public Price totalPrice() {
        return unitPrice.multiply(quantity);
    }
}