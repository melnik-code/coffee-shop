package values;

import java.util.*;

public record Recipe(
        Map<Ingredient, Quantity> components
) {
    public Recipe {
        validateComponents(components);
        components = Map.copyOf(components);
    }

    public boolean isRequired(Ingredient ingredient) {
        Objects.requireNonNull(ingredient, "ingredient cannot be null");
        return components.containsKey(ingredient);
    }

    public Quantity requiredQuantity(Ingredient ingredient) {
        if (!isRequired(ingredient))
            throw new IllegalArgumentException(ingredient + " does not required");

        return components.get(ingredient);
    }

    public Recipe scale(Quantity multiplier) {
        Objects.requireNonNull(multiplier, "multiplier cannot be null");

        Map<Ingredient, Quantity> newComponents = new HashMap<>();

        for (var entry : components.entrySet()) {
            newComponents.put(
                    entry.getKey(),
                    entry.getValue().multiply(multiplier)
            );
        }

        return new Recipe(newComponents);
    }

    private static void validateComponents(Map<Ingredient, Quantity> components) {
        Objects.requireNonNull(components, "components cannot be null");

        if (components.isEmpty())
            throw new IllegalArgumentException("components cannot be empty");

        for (var entry : components.entrySet()) {
            var ingredient = entry.getKey();
            var quantity = entry.getValue();

            Objects.requireNonNull(ingredient, "components cannot contains null");
            Objects.requireNonNull(quantity, ingredient + " quantity cannot be null");
            if (quantity.isZero())
                throw new IllegalArgumentException(ingredient + "quantity cannot be 0");
        }
    }
}
