import entities.Customer;
import entities.MenuItem;
import entities.Order;
import entities.Receipt;
import enums.ItemType;
import enums.PaymentMethod;
import enums.Unit;
import requests.CreateOrderItem;
import services.CoffeeShopService;
import values.Price;
import values.Ingredient;
import values.Quantity;
import values.Recipe;
import values.Amount;

import java.util.List;
import java.util.Map;

public class Main {

    static void main(String[] args) {

        var coffeeShop = new CoffeeShopService();

        // Ingredients

        Ingredient milk = coffeeShop.addIngredient(
                "milk",
                Unit.MILLILITER
        );

        Ingredient coffee = coffeeShop.addIngredient(
                "coffee",
                Unit.GRAM
        );

        Ingredient sugar = coffeeShop.addIngredient(
                "sugar",
                Unit.GRAM
        );

        // Restock

        coffeeShop.restockIngredient(
                milk,
                new Quantity(5000)
        );

        coffeeShop.restockIngredient(
                coffee,
                new Quantity(1000)
        );

        coffeeShop.restockIngredient(
                sugar,
                new Quantity(500)
        );

        // Recipe

        Recipe latteRecipe = new Recipe(
                Map.of(
                        milk, new Quantity(250),
                        coffee, new Quantity(18),
                        sugar, new Quantity(5)
                )
        );

        // Menu item

        MenuItem latte = coffeeShop.addMenuItem(
                "latte",
                ItemType.DRINK,
                latteRecipe,
                new Price(450)
        );

        // Customer

        Customer customer = coffeeShop.registerCustomer(
                "alex"
        );

        // Order

        Order order = coffeeShop.createOrder(
                customer.getCustomerId(),
                List.of(
                        new CreateOrderItem(
                                latte.getMenuItemId(),
                                new Quantity(2)
                        )
                )
        );

        System.out.println("Order created:");
        System.out.println(order);

        // Payment

        Receipt receipt = coffeeShop.payForOrder(
                order.getOrderId(),
                PaymentMethod.CASH,
                new Amount(1000)
        );

        System.out.println();
        System.out.println("Receipt:");
        System.out.println(receipt);

        // Workflow

        coffeeShop.startPreparation(
                order.getOrderId()
        );

        coffeeShop.finishPreparation(
                order.getOrderId()
        );

        coffeeShop.completeOrder(
                order.getOrderId()
        );

        System.out.println();
        System.out.println("Final order status:");
        System.out.println(
                coffeeShop.getOrder(order.getOrderId())
                        .getStatus()
        );
    }
}