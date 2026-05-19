package services;

import entities.*;
import enums.ItemType;
import enums.OrderStatus;
import enums.PaymentMethod;
import enums.Unit;
import requests.CreateOrderItem;
import values.*;

import java.time.Instant;
import java.util.*;

public final class CoffeeShopService {

    private final Map<UUID, Customer> customers;
    private final Map<Ingredient, Quantity> ingredientStock;
    private final Map<UUID, MenuItem> menuItems;
    private final Map<UUID, Order> orders;

    public CoffeeShopService() {
        customers = new HashMap<>();
        ingredientStock = new HashMap<>();
        menuItems = new HashMap<>();
        orders = new HashMap<>();
    }

    public Customer registerCustomer(String customerName) {
        var customerId = UUID.randomUUID();
        var customer = new Customer(
                customerId,
                customerName
        );

        customers.put(customerId, customer);
        return customer;
    }

    public Ingredient addIngredient(String name, Unit unit) {
        var ingredient = new Ingredient(
                name,
                unit
        );

        if (hasIngredient(ingredient))
            throw new IllegalStateException(
                    ingredient + " already exists"
            );

        ingredientStock.put(ingredient, Quantity.ZERO);
        return ingredient;
    }

    public void restockIngredient(Ingredient ingredient, Quantity quantity) {
        Objects.requireNonNull(ingredient, "ingredient cannot be null");
        Objects.requireNonNull(quantity, "quantity cannot be null");
        if (quantity.isZero())
            throw new IllegalArgumentException("quantity cannot be 0");
        if (!hasIngredient(ingredient))
            throw new IllegalArgumentException(ingredient + " does not exist");

        ingredientStock.computeIfPresent(
                ingredient,
                (k, curQuantity) -> curQuantity.add(quantity)
        );
    }

    public MenuItem addMenuItem(
            String name,
            ItemType itemType,
            Recipe recipe,
            Price price
    ) {
        validateRecipe(recipe);
        var menuItemId = UUID.randomUUID();
        var menuItem = new MenuItem(
                menuItemId,
                name,
                itemType,
                recipe,
                price
        );

        menuItems.put(menuItemId, menuItem);
        return menuItem;
    }

    public void removeMenuItem(UUID menuItemId) {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        if (!hasMenuItem(menuItemId))
            throw new NoSuchElementException("menuItem " + menuItemId + " does not exist");

        menuItems.remove(menuItemId);
    }

    public Order createOrder(
            UUID customerId,
            List<CreateOrderItem> createOrderItems
    ) {
        if (!hasCustomer(customerId)) {
            throw new NoSuchElementException(
                    "customer-" + customerId + " does not exist"
            );
        }
        var items = getAndValidateCreateItems(createOrderItems);

        var orderId = UUID.randomUUID();
        var totalPrice = getTotalPrice(createOrderItems);
        var order = new Order(
                orderId,
                customerId,
                items,
                totalPrice,
                Instant.now()
        );

        orders.put(orderId, order);
        return order;
    }

    public Receipt payForOrder(
            UUID orderId,
            PaymentMethod paymentMethod,
            Amount paidAmount
    ) {
        var order = getOrder(orderId);
        Objects.requireNonNull(paymentMethod, "paymentMethod cannot be null");
        Objects.requireNonNull(paidAmount, "paidAmount cannot be null");

        if (order.getStatus() != OrderStatus.NEW)
            throw new IllegalStateException("order status must be NEW");
        if (paidAmount.isZero())
            throw new IllegalArgumentException("paidAmount cannot be 0");
        if (paidAmount.value() < order.getTotalPrice().value())
            throw new IllegalArgumentException("paidAmount must be more than or equal to totalPrice: " + order.getTotalPrice().value());

        spendItems(order.getItems());

        var change = paidAmount.subtract(order.getTotalPrice().toAmount());
        var instantNow = Instant.now();
        var receipt = new Receipt(
                UUID.randomUUID(),
                orderId,
                paymentMethod,
                paidAmount,
                change,
                instantNow
        );
        order.markPaid(instantNow);

        return receipt;
    }

    public void startPreparation(UUID orderId) {
        getOrder(orderId).markInPreparation();
    }

    public void finishPreparation(UUID orderId) {
        getOrder(orderId).markReady();
    }

    public void completeOrder(UUID orderId) {
        getOrder(orderId).markCompleted(Instant.now());
    }

    public void cancelOrder(UUID orderId) {
        var order = getOrder(orderId);
        order.cancel();
    }

    public Order getOrder(UUID orderId) {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        if (!hasOrder(orderId))
            throw new NoSuchElementException("order-" + orderId + " does not exist");
        return orders.get(orderId);
    }

    public Customer getCustomer(UUID customerId) {
        if (!hasCustomer(customerId))
            throw new NoSuchElementException("customer-" + customerId + " does not exist");

        return customers.get(customerId);
    }

    public Optional<MenuItem> findMenuItem(UUID menuItemId) {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        return Optional.ofNullable(menuItems.get(menuItemId));
    }

    public MenuItem getMenuItem(UUID menuItemId) {
        return findMenuItem(menuItemId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "menuItem-" + menuItemId + " does not exist"
                        ));
    }

    public Optional<Quantity> findIngredientStock(Ingredient ingredient) {
        Objects.requireNonNull(ingredient, "ingredient cannot be null");
        return Optional.ofNullable(ingredientStock.get(ingredient));
    }

    public Quantity getIngredientStock(Ingredient ingredient) {
        return findIngredientStock(ingredient)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                ingredient + " does not exist"
                        ));
    }

    public Map<UUID, MenuItem> getMenuSnapshot() {
        return Map.copyOf(menuItems);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasCustomer(UUID customerId) {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        return customers.containsKey(customerId);
    }

    public boolean hasIngredient(Ingredient ingredient) {
        Objects.requireNonNull(ingredient, "ingredient cannot be null");
        return ingredientStock.containsKey(ingredient);
    }

    public boolean hasMenuItem(UUID menuItemId) {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        return menuItems.containsKey(menuItemId);
    }

    public boolean hasOrder(UUID orderId) {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        return orders.containsKey(orderId);
    }

    public boolean isItemAvailable(UUID menuItemId) {
        var menuItem = getMenuItem(menuItemId);
        return isIngredientsAvailable(menuItem.getRecipe().components());
    }

    private boolean isOrderAvailable(List<CreateOrderItem> createOrderItems) {
        Map<Ingredient, Quantity> ingredients = new HashMap<>();

        for (var createOrderItem : createOrderItems) {
            var menuItemQuantity = createOrderItem.quantity();

            var recipe = getMenuItem(
                    createOrderItem.menuItemId()
            ).getRecipe();

            for (var entry : recipe.components().entrySet()) {
                var ingredient = entry.getKey();
                var ingredientQuantity = entry.getValue().multiply(menuItemQuantity);

                ingredients.merge(
                        ingredient,
                        ingredientQuantity,
                        Quantity::add
                );
            }
        }

        return isIngredientsAvailable(ingredients);
    }

    private List<OrderItem> getAndValidateCreateItems(List<CreateOrderItem> createOrderItems) {
        Objects.requireNonNull(createOrderItems, "createOrderItems cannot be null");
        if (createOrderItems.isEmpty())
            throw new IllegalArgumentException("createOrderItems cannot be empty");

        List<OrderItem> items = new ArrayList<>();

        for (var createOrderItem : createOrderItems) {
            Objects.requireNonNull(createOrderItem, "createOrderItems cannot contains null");

            var menuItemId = createOrderItem.menuItemId();
            var menuItem = getMenuItem(menuItemId);
            var quantity = createOrderItem.quantity();

            var orderItem = new OrderItem(
                    menuItemId,
                    menuItem.getName(),
                    menuItem.getPrice(),
                    quantity
            );

            items.add(orderItem);
        }

        return items;
    }

    private Price getTotalPrice(List<CreateOrderItem> items) {
        Amount totalPrice = Amount.ZERO;

        for (var item : items) {
            if (item == null)
                throw new IllegalArgumentException("items cannot contains null");

            var menuItem = getMenuItem(item.menuItemId());
            totalPrice = totalPrice.add(
                    menuItem.getPrice()
                            .multiply(item.quantity())
                            .toAmount()
            );
        }

        return totalPrice.toPrice();
    }

    private Map<Ingredient, Quantity> calculateTotalRequirements(List<OrderItem> items) {
        Map<Ingredient, Quantity> reqIngredients = new HashMap<>();

        for (var item : items) {
            var menuItem = getMenuItem(item.menuItemId());

            for (var entry : menuItem
                    .getRecipe()
                    .components()
                    .entrySet()) {
                reqIngredients.merge(
                        entry.getKey(),
                        entry.getValue().multiply(item.quantity()),
                        Quantity::add
                );
            }
        }

        return reqIngredients;
    }

    private void validateIngredients(Map<Ingredient, Quantity> ingredients) {
        for (var entry : ingredients.entrySet()) {
            var curQuantity = getIngredientStock(entry.getKey());

            if (curQuantity.value() < entry.getValue().value())
                throw new IllegalStateException(
                        "not enough ingredient: " + entry.getKey().name() +
                        "; required: " + entry.getValue().value() +
                        "; available: " + curQuantity.value()
                );
        }
    }

    private void spendItems(List<OrderItem> items) {
        var reqIngredients = calculateTotalRequirements(items);

        validateIngredients(reqIngredients);

        spendIngredients(reqIngredients);
    }

    private void spendIngredients(Map<Ingredient, Quantity> ingredients) {
        for (var entry : ingredients.entrySet()) {
            var curQuantity = getIngredientStock(entry.getKey());

            ingredientStock.put(
                    entry.getKey(),
                    curQuantity.subtract(entry.getValue())
            );
        }
    }

    private boolean isIngredientAvailable(Ingredient ingredient, Quantity quantity) {
        var quantityStock = ingredientStock.get(ingredient);
        return hasIngredient(ingredient) &&
                quantity.value() <= quantityStock.value();
    }

    private boolean isIngredientsAvailable(Map<Ingredient, Quantity> ingredients) {
        for (var entry : ingredients.entrySet()) {
            var ingredient = entry.getKey();
            var quantity = entry.getValue();

            if (!isIngredientAvailable(ingredient, quantity))
                return false;
        }

        return true;
    }

    private void validateRecipe(Recipe recipe) {
        for (var ingredient : recipe.components().keySet()) {
            if (!hasIngredient(ingredient))
                throw new IllegalStateException(ingredient + " does not exist");
        }
    }
}

