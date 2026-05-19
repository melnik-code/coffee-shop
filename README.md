# Coffee Shop Domain Model ☕️

A Java backend domain-modeling project focused on rich entities, value objects, invariants, and fail-fast validation.

This project simulates the core business logic of a coffee shop:
- ingredient stock management
- menu management
- order lifecycle
- payment processing
- recipe validation
- inventory deduction

The main goal of the project is not building a UI or database layer, but designing a clean and safe domain model.

---

# Features

## Customers
- Customer registration
- Loyalty points support
- Name normalization and validation

## Ingredients & Inventory
- Ingredient registration
- Unit-based inventory management
- Stock replenishment
- Inventory validation

## Menu
- Menu item creation
- Recipe updates
- Price updates

## Orders
- Order creation
- Total price calculation
- Status transitions:
    - NEW
    - PAID
    - IN_PREPARATION
    - READY
    - COMPLETED
    - CANCELLED

## Payments
- Receipt generation
- Change calculation
- Payment validation

---

# Domain Modeling

The project follows a DDD-lite approach.

## Entities
Objects with identity and lifecycle:
- Customer
- MenuItem
- Order
- Receipt

## Value Objects
Immutable objects without identity:
- Ingredient
- Price
- Amount
- Quantity
- Recipe
- OrderItem

---

# Design Principles

## Fail Fast
Invalid state is rejected immediately.

Examples:
- null checks
- empty collections
- invalid status transitions
- negative values
- missing ingredients
- invalid recipes

---

## Immutable Value Objects
Value objects are immutable and validated during construction.

```java
new Price(450);

new Quantity(2);

new Recipe(...);
```

---

## No Primitive Obsession
Raw primitives are replaced with domain types.

Instead of:

- long
- int

the model uses:
- Price
- Amount
- Quantity

---

## Defensive Copying
Mutable collections are never exposed directly.

```java
Map.copyOf(...)

List.copyOf(...)
```

---

## Explicit Business Rules
Business rules are enforced inside the domain model instead of relying on external validation.

Examples:
- cannot pay an already paid order
- cannot skip order statuses
- recipe quantities must be positive
- stock cannot become negative

---

# Project Structure

```
src
├── entities
│   ├── Customer
│   ├── MenuItem
│   ├── Order
│   └── Receipt
│
├── values
│   ├── Ingredient
│   ├── Price
│   ├── Amount
│   ├── Quantity
│   ├── Recipe
│   └── OrderItem
│
├── enums
│   ├── ItemType
│   ├── OrderStatus
│   ├── PaymentMethod
│   └── Unit
│
├── requests
│   └── CreateOrderItem
│
├── services
│   └── CoffeeShopService
│
└── utilities
└── Validator
```

---

# Testing

The project includes validation and edge-case tests for:
- null handling
- invalid quantities
- invalid prices
- duplicate ingredients
- order status transitions
- insufficient stock
- invalid recipes
- payment edge cases

---

# Example

```java
var milk = service.addIngredient(
    "milk",
    Unit.MILLILITER
);

service.restockIngredient(
    milk,
    new Quantity(5000)
);

var latte = service.addMenuItem(
    "latte",
    ItemType.DRINK,
    recipe,
    new Price(450)
);
```

---

# Tech Stack

- Java 26
- JUnit 6
- IntelliJ IDEA

---

# Purpose of the Project

This project was built to practice:
- backend architecture
- domain modeling
- value objects
- entity lifecycle management
- invariant protection
- fail-fast validation
- clean code principles

It intentionally avoids frameworks in the core domain layer to keep business logic isolated and explicit.

---

# Future Improvements

Possible next steps:
- Spring Boot REST API
- persistence layer
- database integration
- DTO mapping
- transaction handling
- concurrency support
- authentication
- order history
- loyalty system expansion

