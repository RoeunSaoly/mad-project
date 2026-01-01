# Clothes Shop App (Android – Java)

## 📘 Project Proposal Documentation

---

## A. Project Title & Group Information

### 📌 Project Title

**Clothes Shop App – Android-Based Online Clothing Store**

### 👥 Group Members

| No  | Name / GitHub                                                                                      | Role                                |
| --- | -------------------------------------------------------------------------------------------------- | ----------------------------------- |
| 1   | **Roeun Saoly** – [https://github.com/RoeunSaoly](https://github.com/RoeunSaoly) | **Team Leader / Android Developer** |
| 2   | Phan Rathanak – [https://github.com/Rathanak-Phan](https://github.com/Rathanak-Phan)                       | Firebase & Backend Integration      |
| 3   | Phen Norak – [https://github.com/Norak-PNG](https://github.com/Norak-PNG)                               | Data Management & Testing           |
| 4   | Young Soklong – [https://github.com/Longsok](https://github.com/Longsok)                                | Documentation & Support             |
| 5   | Soeurn Broseth - [https://github.com/gegacy]                                                                                             | UX / UI Designer                    |



👨‍🏫 **Submitted to Lecturer:**  
[**Hang Sopheak**](https://github.com/hangsopheak)


---

## 1️⃣ Problem Statement

Many customers find it inconvenient to purchase clothes using traditional methods such as visiting physical stores or ordering through social media chats. These methods often lack clear product information, organized categories, and efficient order tracking. Customers waste time asking for prices, sizes, and availability.

At the same time, shop owners face challenges managing products and orders manually. Without a digital system, it becomes difficult to update product information, track customer orders, and manage sales efficiently. This project aims to solve these problems by providing an Android-based Clothes Shop App that simplifies shopping for customers and supports order management through an admin system.

---

## 2️⃣ User Stories

### 👤 Customer (Android App)

- As a customer, I want to register and log in so that I can use the app securely.
- As a customer, I want to browse clothes by category so that I can find products easily.
- As a customer, I want to view product details (image, price, size) so that I can decide before buying.
- As a customer, I want to place orders so that I can purchase clothes online.

### 🛠️ Admin (Web – Vue)

- As an admin, I want to add, update, and delete products so that the shop inventory stays updated.
- As an admin, I want to view customer orders so that I can manage and process them efficiently.

🔗 **Admin Web App:**
[https://rathanak-admin.vercel.app/](https://rathanak-admin.vercel.app/)

---

## 3️⃣ UI Mockup / Wireframe

### 🎨 Design Tool

- **Figma**

### 📱 Screens (Minimum 6)

1. Splash / Login Screen
2. Register Screen
3. Home Screen (Product List)
4. Category Screen
5. Product Detail Screen
6. Order / Checkout Screen

🔗 **Figma Design Link:**

> _([click here to views prototype](https://www.figma.com/design/LMjHtlj1PXdv5qWi1iEQ0K/Shop?node-id=0-1&p=f&t=dwGyZo1nItvvWBQW-0))_

---

## 4️⃣ Features

### ✅ Customer (Android App)

- User Authentication (Firebase Auth)
- Product Browsing by Category
- Product Detail View
- Order Placement
- Simple & User-Friendly UI

### ✅ Admin (Web – Vue)

- Admin Login
- Product CRUD (Create, Read, Update, Delete)
- View Customer Orders

Each feature is directly derived from the user stories and meets the project scope.

---

## 5️⃣ Source Code Management (GitHub)

### 📂 GitHub Repository

🔗 **Repository URL:**

> _[Github repository](https://github.com/RoeunSaoly/mad-project)_

### 📌 Repository Rules

- Initial commit includes this `README.md`
- Regular commits during development
- Repository accessible to lecturer and team members

---

## 6️⃣ Backend Technology & Data Model

### 🔧 Technologies Used

- **Frontend (Mobile):** Android (Java, Android Studio)
- **Backend / Cloud Service:** Firebase
- **Authentication:** Firebase Authentication
- **Database:** Firebase Firestore

---

### 📊 Data Model (Firebase – NoSQL)

#### User

```json
{
  "userId": "string",
  "name": "string",
  "email": "string",
  "role": "customer"
}
```

#### Product

```json
{
  "productId": "string",
  "name": "string",
  "price": "number",
  "category": "string",
  "imageUrl": "string",
  "description": "string"
}
```

#### Order

```json
{
  "orderId": "string",
  "userId": "string",
  "products": [],
  "totalPrice": "number",
  "orderDate": "timestamp"
}
```

---

## ✅ Conclusion

The **Clothes Shop App** is an Android-based application developed using Java and Firebase to provide a simple and efficient online clothing shopping experience. The system supports customers through a mobile app and administrators through a web-based dashboard, making it suitable for learning purposes and real-world application.

---
