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
- Often corrupted by database driven design, services don't focus on domain logic but on how to utilize the database
- Layers can be violated increasing effort vastly for testing
- Use cases become invisible in code, domain services tend to have many responsibilities

## Inspired by the following talks
- https://www.youtube.com/watch?v=VGhg6Tfxb60&t=2871s - Implementing Domain Driven Design with Spring by Maciej Walkowiak @ Spring I/O 2024 
- https://www.youtube.com/watch?v=cPH5AiqLQTo - Clean Architecture with Spring by Tom Hombergs @ Spring I/O 2019
