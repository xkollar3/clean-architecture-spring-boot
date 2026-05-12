# Clean architecture implementation in Spring Boot

- Demo showcases clean architecture using Spring Boot and demonstrates advantages over layered architecture
- The goal of software architecture is to minimize the lifetime cost of the software
- Layers are a solid architecture pattern that works, but without restrictions they are prone to design flaws

## Outline
- Traditional layered architecture
- Clean architecture in Spring Boot
- Spring Modulith for decoupling and module separation
- Easy transition from Modular Monolith to Microservices using Spring Modulith

## Layered architecture
- Architecture that organizes code into layers
- Layers are chosen depending on technical responsibilties for example: Api, Facade, Service, Repository
- Layered architecture works very well in some scenarios most devs know it since its widely popular
- Layered architecture is easy to test
- Often corrupted by database driven design, services don't focus on domain logic but on how to utilize the database
- Layers can be violated increasing effort vastly for testing
- Use cases become invisible in code, domain services tend to have many responsibilities

## Clean architecture
- Architecture aims to separate out business logic
- Applications are split into different rings where dependencies flow inward to entities
- Inner most entity layer contains purely domain models and business logic
- Use case layer orchestrates entities
- Dependency inversion is used when communicating with layers above, preventing dependency on specific infrastructure
- Clean architecture can be pragmatically combined with vertical slice architecture, this way only entity core is shared among features
- Otherwise implementation of different features is separated
- Code may be shared among slices pragmatically where it makes sense

## Billing module
- Demo showcase is done on a billing module working with subscription model
- Customers can subscribe to plans and are automatically invoiced each time the plan renews

## Architecture Diagram
![Architecture Diagram](assets/diagram.png)

## Inspired by the following talks
- https://www.youtube.com/watch?v=VGhg6Tfxb60&t=2871s - Implementing Domain Driven Design with Spring by Maciej Walkowiak @ Spring I/O 2024
- https://www.youtube.com/watch?v=cPH5AiqLQTo - Clean Architecture with Spring by Tom Hombergs @ Spring I/O 2019
