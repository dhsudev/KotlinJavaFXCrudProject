# Kotlin JavaFX CRUD Project

This project is a desktop app in Kotlin that allows managing clients through CRUD operations (Create, Read, Update, Delete) in a PostgreSQL database.

## 🏗 Requirements

- IntelliJ IDEA
- JDK 17+
- PostgreSQL running on localhost:
  - You must specify the connection data in `Database.kt`
    - Database: ds2
    - User: ds2
    - Password: ds2
  - You must execute the SQL script in `db/cemed.sql` to create the needed tables and inserts some example data.

> This is the database schema used in the project:
> ![Database Schema](db/squema.png)


## 🛠 How to run

1. Open the project in IntelliJ IDEA (`File > Open...`).
2. Wait for Gradle to sync.
3. Open `Main.kt` and click ▶️ to run the app.

## 🚀 Functionality
- From the `GUIApp` you can select the CRUD operation to perform:
  - Insert clients
  - List clients (in blocks of 10)
  - Update email
  - Delete clients

