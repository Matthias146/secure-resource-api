# Secure Resource API

## 1. Goal

Build a small REST API focused on secure software development.

The project will be used to practice:

- REST API design
- Spring Boot
- Spring Security
- authentication and authorization
- PostgreSQL and SQL
- automated testing
- application security
- penetration testing fundamentals
- architecture and system design
- technical data analysis

## 2. Core Domain

The system contains two main concepts:

### User

A user can authenticate and own resources.

Initial properties:

- id
- email
- password
- role

### Resource

A resource belongs to exactly one user.

Initial properties:

- id
- title
- content
- owner
- createdAt
- updatedAt

## 3. Initial Roles

For the first version:

- USER
- ADMIN

A USER may only access resources they own.

An ADMIN may later receive additional permissions.

The exact administrator permissions will be decided when they are required.

## 4. Initial API

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`

### Resources

- `GET /api/resources`
- `GET /api/resources/{id}`
- `POST /api/resources`
- `PUT /api/resources/{id}`
- `DELETE /api/resources/{id}`

## 5. Basic Security Requirements

The application must ensure that:

- passwords are never stored in plain text
- unauthenticated users cannot access protected resources
- users cannot access resources belonging to another user
- input is validated
- sensitive internal data is not exposed through API responses
- authorization is enforced by the backend
- client-provided ownership information is not trusted

## 6. Initial Security Questions

During development we will investigate questions such as:

- Can a user manipulate a resource ID to access another user's data?
- Can ownership be changed through a request?
- What happens when invalid or unexpected input is submitted?
- What information should authentication errors reveal?
- Which events should be logged?
- Which information must never be logged?
- How do we protect authentication endpoints from abuse?

## 7. Architecture v1

```text
Client
   |
   v
Spring Boot REST API
   |
   +-- Authentication / Authorization
   |
   +-- Business Logic
   |
   +-- Persistence
          |
          v
      PostgreSQL
```

The first version will remain a monolithic application.

Additional components will only be introduced when there is a concrete technical reason for them.

## 8. Development Principle

For larger changes we follow this workflow:

Problem  
→ Requirements  
→ Security risks  
→ Possible solutions  
→ Decision  
→ Implementation  
→ Tests  
→ Security review  
→ Documentation

## 9. Definition of Done for v1

The first version is complete when:

- a user can register
- a user can authenticate
- authenticated users can create resources
- users can retrieve their own resources
- users cannot access resources belonging to another user
- users can update and delete their own resources
- important behavior is covered by automated tests
- basic security tests have been performed
- architectural and security decisions are documented