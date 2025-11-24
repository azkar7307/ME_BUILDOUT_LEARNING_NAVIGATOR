# 🎓 Learning Navigator API Documentation

A **Spring Boot RESTful API** for managing **students, subjects, and exam registrations** in a Learning Management System (LMS).
Built with **MySQL** for persistence, **layered architecture**, and includes **Easter egg** feature for fun.

## 🌟 Features

- ✅ Full CRUD operations for **Students, Subjects, and Exams**
- ✅ Enroll students in **Subjects**
- ✅ Enroll students in **Exams** (only after subject enrollment)
- ✅ **Foreign Key relationships** enforced between entities
- ✅ **Global Exception Handling** with meaningful HTTP codes (404, 409, etc.)
- ✅ Layered architecture: **Controller → Service → Repository → Entity**
- ✅ **Unit Tests** with `MockMvc` and `Mockito` (minimum 15)
- ✅ **Hidden Easter Egg** endpoint using Numbers API
- ✅ Detailed API documentation in **Postman collection** with example requests/responses

## 📂 Postman Collection
### Option - 1
Access the Learning Navigator API collection using the link below. 
**[Importer Link to Postman Collection](https://www.postman.com/navigation-participant-9941289/workspace/collections/collection/37160902-94213a99-9d46-4008-a5b5-0b11b1f638e7?action=share&creator=37160902)**

### Option - 2
Postman collection file included in docs/Learning Navigator API.postman_collection.json

Import the collection to test all endpoints quickly

## 🏗️ Architecture

```
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
Database (MySQL)
```

- **Controller**: Handles API requests
- **Service**: Business logic
- **Repository**: Database access
- **Entity**: Database model representation

## 🛠️ Tech Stack

- Spring Boot 3.4.1
- Java 17
- MySQL 14.14
- Gradle 8.11.1
- JUnit 5, Mockito
- Postman

## 🌐 Base URL

```
http://localhost:8081
```

### 📚 Endpoints

#### Students

| Endpoint                                       | Method | Description                  | Request Body        | Response Code |
| ---------------------------------------------  | ------ | ---------------------------- | ------------------- | ------------- |
| `/students`                                    | POST   | Create a new student         | { "name": "Akash" } | 201           |
| `/students`                                    | GET    | Get all students             |         -           | 200           |
| `/students/{student_id}`                       | GET    | Get student by ID            |         -           | 200           |
| `/students/{student_id}`                       | PUT    | Update student by ID         | { "name": "Surya" } | 200           |
| `/students/{student_id}`                       | DELETE | Delete student by ID         |         -           | 200           |
| `/students/{student_id}/subjects/{subject_id}` | POST   | Enroll student in a subject  |         -           | 200           |
| `/students/{student_id}/exams/{exam_id}`       | POST   | Enroll student in an exam    |         -           | 200           |


#### Subjects

| Endpoint                 | Method | Description                                        | Request Body           | Response Code |
|--------------------------|--------|--------------------------------------------------- |----------------------- |---------------|
| /subjects                | POST   | Create a subject                                   | `{ "name": "DSA" }`    | 201           |
| /subjects                | GET    | Get all subjects                                   | -                      | 200           |
| /subjects/{subject_id}   | GET    | Get subject by ID                                  | -                      | 200           |
| /subjects/{subject_id}   | PUT    | Update subject by ID                               | { "name": "JAVA 301" } | 200           |
| /subjects/{subject_id}   | DELETE | Delete subject by ID (also delete its exam if any) | -                      | 200           |


#### Exams

| Endpoint                      | Method | Description               | Request Body | Response Code |
| ----------------------------- | ------ | ------------------------- | ------------ | ------------- |
| `/exam/subjects/{subject_id}` | POST   | Create exam for a subject | `{}`         | 201           |
| `/exams`                      | GET    | Get all exams             | -            | 200           |
| `/exams/{exam_id}`            | GET    | Get exam by ID            | -            | 200           |
| `/exams/{exam_id}`            | DELETE | Delete exam by ID         | -            | 200           |

#### Easter Egg

| Endpoint                              | Method | Description                                   | Request Body | Response Code |
| ------------------------------------- | ------ | --------------------------------------------- | ------------ | ------------- |
| `/easter-egg/hidden-feature/{number}` | GET    | Returns a random number fact from Numbers API | -            | 200           |


## ⚠️ Error Handling

- **404 Not Found →** Resource does not exist

- **409 Conflict →** Resource already exists

- All exceptions handled globally using `@RestControllerAdvice`

## 🧪 Unit Tests

- `StudentServiceTest` → Testing student enrollment logic

- `SubjectServiceTest` → Testing subject creation & retrieval

- `ExamServiceTest` → Testing exam enrollment & creation


Uses **MockMvc** and **Mockito** for mock testing



## 📝 Commit & Versioning

Meaningful commits using conventional commits:

- `feat: add student enrollment endpoint`

- `fix: handle student not found exception`

- `test: add unit tests for exam service`

- `refactor:	update exam entity fields visibility from default to private`

- `chore:	update .gitignore to stop tracking logs and *.log file`

- `docs:	Documentation added`


## ✨ Easter Egg Hidden Fun

- Trigger: `GET /easter-egg/hidden-feature/{number}`

- Response: **Random number fact** using Numbers API

- Example: `GET /easter-egg/hidden-feature/7`

Fun feature for curious users exploring the API

