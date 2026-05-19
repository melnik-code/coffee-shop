package values;

import enums.Unit;

import java.util.Objects;

import static utilities.Validator.validateAndNormalizeName;

public record Ingredient(
        String name,
        Unit unit
) {
    public Ingredient {
        Objects.requireNonNull(unit, "unit cannot be null");
        name = validateAndNormalizeName(name);
    }
}
