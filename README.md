This API is developed in Spring Boot and will be used for a budget control react app, at this moment all the API Contract works but login and permissions is not implemented yet.

The database is a Docker PostgreSQL so feel free to try yourself, all you will be needing to use this API is inside the Contract, it's a API RESTfull, so no confusing URI or methods.

----------BackEnd - Spring Boot----------

Database: PostgreSQL - Docker

Spring Boot:
Project Maven;
Language - Java;
Version Spring - 3.4.1;
Group - com.budget.control;
Artifact - backend;
Packge name - com.budget.control.backend;
Java - 21;

Dependencies:
Spring Web - Used to build API REST for web.
Lombok - For easy annotations for dev side.
Spring Boot DevTools - Dev tools.
Spring Security - Authentication and access-control.
OAuth2 Client - Integration for Spring Security.
Spring Data JPA - JPA Persistence API SQL.
PosgreSQL Driver - Driver for using PostgreSQL.
MapStruct - to Map DTO.
Jakarta Validation - to validate.
