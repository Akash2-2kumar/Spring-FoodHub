**FoodHub – Food Ordering Backend Application**

*Java | Spring Boot | Spring Security | Spring Data JPA | Hibernate | MySQL | REST APIs | Swagger*

Developed a food ordering backend application using Spring Boot and RESTful APIs, supporting customer, restaurant owner, delivery boy, and admin workflows.

- Implemented role-based authentication and authorization using Spring Security, `Authentication`, `@PreAuthorize`, BCrypt password encryption, and custom `UserDetailsService`.
- Designed JPA/Hibernate entities and unidirectional relationships for users, restaurants, foods, orders, and order items.
- Implemented complete order lifecycle management, including order placement, cancellation, restaurant confirmation, food preparation, delivery-boy assignment, and delivery completion.
- Developed DTOs, mappers, input validation, and centralized custom exception handling.
- Implemented transaction management using `@Transactional`, along with JPQL and native SQL queries.
- Integrated Swagger/OpenAPI for REST API documentation and testing.