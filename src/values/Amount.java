package values;

public record Amount(
        long value
) {

    public static final Amount ZERO = new Amount(0);

    public Amount {
        if (value < 0)
            throw new IllegalArgumentException(
                    "value cannot be negative"
            );
    }

    public boolean isZero() {
        return value == 0;
    }

    public Amount add(Amount Amount) {
        return new Amount(value + Amount.value);
    }

    public Amount subtract(Amount amount) {
        if (amount.value > value)
            throw new IllegalArgumentException(amount + " cannot be more than " + this);
        return new Amount(value - amount.value);
    }

    public Amount multiply(Quantity multiplier) {
        return new Amount(value * multiplier.value());
    }

    public Price toPrice() {
        return new Price(value);
    }
}
