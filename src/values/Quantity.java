package values;

public record Quantity(
        int value
) {

    public static final Quantity ZERO = new Quantity(0);

    public Quantity {
        if (value < 0)
            throw new IllegalArgumentException(
                    "value cannot be negative"
            );
    }

    public boolean isZero() {
        return value == 0;
    }

    public Quantity add(Quantity quantity) {
        return new Quantity(value + quantity.value);
    }

    public Quantity subtract(Quantity quantity) {
        if (quantity.value > value)
            throw new IllegalArgumentException(quantity + " cannot be more than " + this);

        return new Quantity(value - quantity.value);
    }

    public Quantity multiply(Quantity multiplier) {
        return new Quantity(value * multiplier.value);
    }
}
