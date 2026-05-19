package services;

import entities.Order;
import enums.ItemType;
import enums.OrderStatus;
import enums.PaymentMethod;
import enums.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateOrderItem;
import values.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoffeeShopServiceTest {

    private CoffeeShopService service;

    @BeforeEach
    void setUp() {
        service = new CoffeeShopService();
    }

    @Test
    void registerCustomer_shouldThrow_whenNameIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> service.registerCustomer(null)
        );
    }

    @Test
    void addIngredient_shouldThrow_whenDuplicateIngredient() {
        service.addIngredient("milk", Unit.MILLILITER);

        assertThrows(
                IllegalStateException.class,
                () -> service.addIngredient("milk", Unit.MILLILITER)
        );
    }

    @Test
    void restockIngredient_shouldThrow_whenQuantityIsZero() {
        var milk = service.addIngredient("milk", Unit.MILLILITER);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.restockIngredient(
                        milk,
                        Quantity.ZERO
                )
        );
    }

    @Test
    void restockIngredient_shouldThrow_whenIngredientDoesNotExist() {
        var milk = new Ingredient(
                "milk",
                Unit.MILLILITER
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.restockIngredient(
                        milk,
                        new Quantity(10)
                )
        );
    }

    @Test
    void addMenuItem_shouldThrow_whenRecipeContainsUnknownIngredient() {
        var milk = new Ingredient(
                "milk",
                Unit.MILLILITER
        );

        var recipe = new Recipe(
                Map.of(
                        milk,
                        new Quantity(100)
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.addMenuItem(
                        "latte",
                        ItemType.DRINK,
                        recipe,
                        new Price(450)
                )
        );
    }

    @Test
    void addMenuItem_shouldThrow_whenRecipeIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> service.addMenuItem(
                        "latte",
                        ItemType.DRINK,
                        null,
                        new Price(450)
                )
        );
    }

    @Test
    void addMenuItem_shouldThrow_whenPriceIsNull() {
        var milk = service.addIngredient(
                "milk",
                Unit.MILLILITER
        );

        var recipe = new Recipe(
                Map.of(
                        milk,
                        new Quantity(100)
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> service.addMenuItem(
                        "latte",
                        ItemType.DRINK,
                        recipe,
                        null
                )
        );
    }

    @Test
    void createOrder_shouldThrow_whenCustomerDoesNotExist() {
        assertThrows(
                NoSuchElementException.class,
                () -> service.createOrder(
                        UUID.randomUUID(),
                        List.of()
                )
        );
    }

    @Test
    void createOrder_shouldThrow_whenItemsIsEmpty() {
        var customer = service.registerCustomer("alex");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createOrder(
                        customer.getCustomerId(),
                        List.of()
                )
        );
    }

    @Test
    void createOrder_shouldThrow_whenItemsContainsNull() {
        var customer = service.registerCustomer("alex");

        assertThrows(
                NullPointerException.class,
                () -> service.createOrder(
                        customer.getCustomerId(),
                        List.of((CreateOrderItem) null)
                )
        );
    }

    @Test
    void createOrder_shouldThrow_whenMenuItemDoesNotExist() {
        var customer = service.registerCustomer("alex");

        var item = new CreateOrderItem(
                UUID.randomUUID(),
                new Quantity(1)
        );

        assertThrows(
                NoSuchElementException.class,
                () -> service.createOrder(
                        customer.getCustomerId(),
                        List.of(item)
                )
        );
    }

    @Test
    void payForOrder_shouldThrow_whenOrderDoesNotExist() {
        assertThrows(
                NoSuchElementException.class,
                () -> service.payForOrder(
                        UUID.randomUUID(),
                        PaymentMethod.CARD,
                        new Amount(1000)
                )
        );
    }

    @Test
    void payForOrder_shouldThrow_whenPaidAmountIsZero() {
        var order = createValidOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.payForOrder(
                        order.getOrderId(),
                        PaymentMethod.CARD,
                        Amount.ZERO
                )
        );
    }

    @Test
    void payForOrder_shouldThrow_whenPaidAmountIsInsufficient() {
        var order = createValidOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.payForOrder(
                        order.getOrderId(),
                        PaymentMethod.CARD,
                        new Amount(1)
                )
        );
    }

    @Test
    void payForOrder_shouldThrow_whenStockIsInsufficient() {
        var milk = service.addIngredient(
                "milk",
                Unit.MILLILITER
        );

        var recipe = new Recipe(
                Map.of(
                        milk,
                        new Quantity(100)
                )
        );

        var latte = service.addMenuItem(
                "latte",
                ItemType.DRINK,
                recipe,
                new Price(450)
        );

        var customer = service.registerCustomer("alex");

        var order = service.createOrder(
                customer.getCustomerId(),
                List.of(
                        new CreateOrderItem(
                                latte.getMenuItemId(),
                                new Quantity(1)
                        )
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.payForOrder(
                        order.getOrderId(),
                        PaymentMethod.CARD,
                        new Amount(500)
                )
        );
    }

    @Test
    void payForOrder_shouldChangeOrderStatusToPaid() {
        var order = createValidOrder();

        service.payForOrder(
                order.getOrderId(),
                PaymentMethod.CARD,
                new Amount(500)
        );

        assertEquals(
                OrderStatus.PAID,
                order.getStatus()
        );
    }

    @Test
    void payForOrder_shouldDecreaseIngredientStock() {
        var milk = service.addIngredient(
                "milk",
                Unit.MILLILITER
        );

        service.restockIngredient(
                milk,
                new Quantity(500)
        );

        var recipe = new Recipe(
                Map.of(
                        milk,
                        new Quantity(100)
                )
        );

        var latte = service.addMenuItem(
                "latte",
                ItemType.DRINK,
                recipe,
                new Price(450)
        );

        var customer = service.registerCustomer("alex");

        var order = service.createOrder(
                customer.getCustomerId(),
                List.of(
                        new CreateOrderItem(
                                latte.getMenuItemId(),
                                new Quantity(2)
                        )
                )
        );

        service.payForOrder(
                order.getOrderId(),
                PaymentMethod.CARD,
                new Amount(1000)
        );

        assertEquals(
                new Quantity(300),
                service.getIngredientStock(milk)
        );
    }

    @Test
    void startPreparation_shouldThrow_whenOrderIsNotPaid() {
        var order = createValidOrder();

        assertThrows(
                IllegalStateException.class,
                () -> service.startPreparation(order.getOrderId())
        );
    }

    @Test
    void finishPreparation_shouldThrow_whenOrderIsNotInPreparation() {
        var order = createValidOrder();

        assertThrows(
                IllegalStateException.class,
                () -> service.finishPreparation(order.getOrderId())
        );
    }

    @Test
    void completeOrder_shouldThrow_whenOrderIsNotReady() {
        var order = createValidOrder();

        assertThrows(
                IllegalStateException.class,
                () -> service.completeOrder(order.getOrderId())
        );
    }

    @Test
    void order_shouldPassFullLifecycle() {
        var order = createPaidOrder();

        service.startPreparation(order.getOrderId());
        service.finishPreparation(order.getOrderId());
        service.completeOrder(order.getOrderId());

        assertEquals(
                OrderStatus.COMPLETED,
                order.getStatus()
        );
    }

    @Test
    void cancelOrder_shouldThrow_whenOrderAlreadyPaid() {
        var order = createPaidOrder();

        assertThrows(
                IllegalStateException.class,
                () -> service.cancelOrder(order.getOrderId())
        );
    }

    @Test
    void getMenuSnapshot_shouldBeImmutable() {
        var snapshot = service.getMenuSnapshot();

        assertThrows(
                UnsupportedOperationException.class,
                () -> snapshot.clear()
        );
    }

    private Order createValidOrder() {
        var milk = service.addIngredient(
                "milk",
                Unit.MILLILITER
        );

        service.restockIngredient(
                milk,
                new Quantity(1000)
        );

        var recipe = new Recipe(
                Map.of(
                        milk,
                        new Quantity(100)
                )
        );

        var latte = service.addMenuItem(
                "latte",
                ItemType.DRINK,
                recipe,
                new Price(450)
        );

        var customer = service.registerCustomer("alex");

        return service.createOrder(
                customer.getCustomerId(),
                List.of(
                        new CreateOrderItem(
                                latte.getMenuItemId(),
                                new Quantity(1)
                        )
                )
        );
    }

    private Order createPaidOrder() {
        var order = createValidOrder();

        service.payForOrder(
                order.getOrderId(),
                PaymentMethod.CARD,
                new Amount(500)
        );

        return order;
    }
}