package entities;

import enums.ItemType;
import values.Price;
import values.Recipe;

import java.util.Objects;
import java.util.UUID;

import static utilities.Validator.validateAndNormalizeName;

public final class MenuItem {

    private final UUID menuItemId;
    private final String name;
    private final ItemType itemType;
    private Recipe recipe;
    private Price price;

    public MenuItem(
            UUID menuItemId,
            String name,
            ItemType itemType,
            Recipe recipe,
            Price price
    ) {

        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        var normalizedName = validateAndNormalizeName(name);
        Objects.requireNonNull(itemType, "itemType cannot be null");
        Objects.requireNonNull(recipe, "recipe cannot be null");
        Objects.requireNonNull(price, "price cannot be null");

        this.menuItemId = menuItemId;
        this.name = normalizedName;
        this.itemType = itemType;
        this.recipe = recipe;
        this.price = price;
    }

    public void changePrice(Price newPrice) {
        Objects.requireNonNull(newPrice, "newPrice cannot be null");
        this.price = newPrice;
    }

    public void updateRecipe(Recipe newRecipe) {
        Objects.requireNonNull(newRecipe, "recipe cannot be null");
        this.recipe = newRecipe;
    }

    public UUID getMenuItemId() {
        return menuItemId;
    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        MenuItem menuItem = (MenuItem) object;
        return Objects.equals(menuItemId, menuItem.menuItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(menuItemId);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "menuItemId=" + menuItemId +
                ", name='" + name + '\'' +
                '}';
    }
}
