package entities;

import values.Amount;

import java.util.Objects;
import java.util.UUID;

import static utilities.Validator.validateAndNormalizeName;

public final class Customer {

    private final UUID customerId;
    private final String name;
    private Amount loyaltyPoints;

    public Customer(UUID customerId, String name) {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        var normalized = validateAndNormalizeName(name);

        this.customerId = customerId;
        this.name = normalized;
        this.loyaltyPoints = new Amount(0);
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public Amount getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addLoyaltyPoints(Amount points) {
        if (points.isZero())
            throw new IllegalArgumentException("points cannot be 0");

        loyaltyPoints = loyaltyPoints.add(points);
    }

    public void spendLoyaltyPoints(Amount points) {
        if (points.isZero())
            throw new IllegalArgumentException("points cannot be 0");

        loyaltyPoints = loyaltyPoints.subtract(points);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Customer customer = (Customer) object;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerId);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                '}';
    }
}
