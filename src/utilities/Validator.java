package utilities;

import java.util.Objects;

public class Validator {
    public static String validateAndNormalizeName(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        if (name.isBlank())
            throw new IllegalArgumentException("name cannot be blank");

        var normalized = name.strip();

        if (normalized.length() < 2 ||
                normalized.length() > 15)
            throw new IllegalArgumentException(
                    "name length must be from 1 to 15 chars"
            );

        return normalized.toLowerCase();
    }
}
