# 🛒 E-Commerce Backend – Spring Boot

This is the backend server for an e-commerce application, built using **Spring Boot**, **Spring Security**, **JWT**, **JPA**, and **MySQL**. It handles user authentication, product management, cart, order, review, payment, and admin functionalities.

## 🚀 Features

- ✅ User Registration & Login (JWT-based)
- 🔐 Role-based Authentication (Customer, Seller, Admin)
- 📦 Product CRUD for Sellers
- 🛍 Cart & Order Management
- 💳 Payment Processing (Razorpay-ready)
- 📝 Product Reviews
- 📦 Order Tracking & Delivery Status
- 📈 Admin Dashboard Support

## ⚙️ Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL
- Maven
- Lombok
- JWT Authentication
- Cloudinary (for image uploads)

## 📁 Project Structure

src/
├── controller/ # REST Controllers
├── service/ # Business Logic
├── repository/ # JPA Repositories
├── model/ # Entity Classes
├── config/ # Security & JWT Configs
└── dto/ # Data Transfer Objects

pgsql
Copy
Edit

## 🧪 API Endpoints

| Method | Endpoint                            | Description               |
|--------|-------------------------------------|---------------------------|
| POST   | `/auth/signup`                      | Register a user           |
| POST   | `/auth/signing`                     | Login with email/password |
| POST   | `/auth/sent/login-signup-otp`       | Send OTP via email        |
| GET    | `/api/user/profile`                 | Fetch user profile        |
| POST   | `/products`                         | Add product (Seller)      |
| GET    | `/orders/{id}`                      | Get order details         |

> ✅ Full API documentation available via Postman collection or Swagger if enabled.

## 🛠 Setup Instructions

### 1. Clone the Repo

```bash
git clone https://github.com/RahUlkr23r/ecommerce-server.git
cd ecommerce-server

2. Build and Run
bash
Copy
Edit
mvn clean install
mvn spring-boot:run
The app will start on: http://localhost:8080

🧑‍💻 Author
Sahil Asneh 
📧 sahilasneh12345@gmail.com
🔗 GitHub – @sahil-2511

