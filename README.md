# Kotlin JavaFX CRUD Project

Este proyecto es una app de escritorio en Kotlin que permite gestionar clientes mediante operaciones CRUD (Crear, Leer, Actualizar, Eliminar) en una base de datos PostgreSQL.

## Requisitos

- IntelliJ IDEA
- JDK 17+
- PostgreSQL corriendo en localhost: 
  - Debes especificar los datos de la conexión en Database.kt
    - Base de datos: ds2 
    - Usuario: ds2 ()
    - Contraseña: ds2

## Cómo ejecutar

1. Abre el proyecto en IntelliJ IDEA (`File > Open...`).
2. Espera a que Gradle sincronice.
3. Abre `Main.kt` y haz clic en ▶️ para ejecutar la app.

## Funcionalidad
- Desde la GUIApp puedes seleccionar la operación CRUD a realizar:
  - Insertar clientes
  - Listar clientes (en bloques de 10)
  - Actualizar email
  - Eliminar clientes