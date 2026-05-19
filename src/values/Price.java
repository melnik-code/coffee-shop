package values;

public record Price (
        long value
) {
    public Price {
        if (value <= 0)
            throw new IllegalArgumentException("value must be positive");
    }

    public Price add(Price price) {
        return new Price(value + price.value);
    }

    public Price subtract(Price price) {
        if (price.value >= value)
            throw new IllegalArgumentException(price + " must be less than " + this);

        return new Price(value - price.value);
    }

    public Price multiply(Quantity multiplier) {
        return new Price(value * multiplier.value());
    }

    public Amount toAmount() {
        return new Amount(value);
    }



}
