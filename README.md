# Описание проекта


Проект «Barsik» – веб-сервис для поиска, заказа и управления услугами pet-sitter’ов: выгул, присмотр, передержка, домашние визиты и дополнительные сервисы (груминг, вакцинация и т. п.). Платформа объединяет владельцев животных и проверенных исполнителей, обеспечивает безопасное бронирование и оплату, простой обмен сообщениями.

Ключевые возможности: профили и портфолио pet-sitter’ов; фильтрация и поиск по локации, типу животного, услуге и рейтингу; мгновенное бронирование и предварительная бронь; система отзывов и рейтингов; безопасные платежи и расчеты через платформу; трекинг прогулок и отчеты; расписание и календарь; встроенный чат.

## Основные функции


- **создание и изменение профиля и портфолио**
- **поиск специалистов с применением фильтров**
- **бронирование специалистов**
- **оплата услуг**
- **система отзывов**
- **встроенный чат**


## Стек используемых технологий

- Java
- Spring Boot

## Роли пользователей
- Неавторизованный пользователь (Guest)
- Администратор (Admin)
- Клиент (Owner)
- Исполнитель (Sitter)

## Схема БД
[Схема БД](./sql/db.svg)



API: Endpoint Overview

General Endpoints (for all users)


1. POST /api/auth/register
     - Request Body
       - ```json
         
           {
              "email": "user@example.com",
              "password": "securepassword123",
              "firstName": "Иван",
              "lastName": "Петров",
              "phone": "+37529234567",
              "role": "client"
           }
      - Response: Status 201 on successful registration.




2. POST /api/auth/login
 
    - Request Body
       - ```json
        
            {
              "email": "user@example.com",
              "password": "password123"
            }

    - Response:
      - ```json {
        
        {
        "token": "jwt-token"
        }

3. GET /api/pets
   - Описание: Возвращает список всех питомцев текущего пользователя.
   - Response:
       - ```json
      
            [
              {
                "id": 1,
                "name": "Барсик",
                "type": "cat",
                "breed": "Мейн-кун",
                "age": 3,
                "weight": 5.5,
                "photoUrl": "/pets/barsik.jpg"
              },
              {
                "id": 2,
                "name": "Шарик",
                "type": "dog", 
                "breed": "Лабрадор",
                "age": 5,
                "weight": 25.0,
                "photoUrl": "/pets/sharik.jpg"
              }
          ]

4. POST /api/pets
   - Добавляет нового питомца для текущего пользователя.
   - Request:
       - ```json
       
           {
              "name": "Мурка",
              "type": "cat",
              "breed": "Дворовая",
              "age": 2,
              "weight": 4.0,
              "description": "Спокойная кошка",
              "photoUrl": "/pets/murka.jpg"
            }
    - Response:
       - ```json
        
           {
            "id": 3,
            "name": "Мурка",
            "type": "cat",
            "breed": "Дворовая",
            "age": 2,
            "weight": 4.0,
            "description": "Спокойная кошка",
            "photoUrl": "/pets/murka.jpg",
            "ownerId": 1
          }
      

5. POST /api/sitters/search
   - Описание: Ищет доступных ситтеров по заданным критериям.
   - REquest:
       - ```json
         
                   {
                      "serviceType": "walking",
                      "date": "2024-01-15",
                      "timeFrom": "10:00",
                      "timeTo": "18:00",
                      "latitude": 55.755826,
                      "longitude": 37.617300,
                      "radiusKm": 5,
                      "minRating": 4.0
                   }
        
   - Response:
       - ```json
          
             {"sitters": [
              {
                "id": 2,
                "firstName": "Мария",
                "lastName": "Сидорова",
                "rating": 4.8,
                "reviewsCount": 24,
                "hourlyRate": 800,
                "distanceKm": 1.2,
                "services": [
                  {
                    "id": 1,
                    "name": "Выгул 30 мин",
                          "price": 400
                  }
                ]
              }
            ]}



6. GET /api/sitters/{id}
   - Описание: Возвращает подробную информацию о ситтере
   - Response:
       - ```json
         
         {
            "id": 2,
            "firstName": "Мария",
            "lastName": "Сидорова",
            "email": "sitter@example.com",
            "phone": "+79167654321",
            "experience": "Опыт работы с животными 5 лет",
            "rating": 4.8,
            "reviewsCount": 24,
            "hourlyRate": 800,
            "isAvailable": true,
            "services": [
              {
                "id": 1,
                "name": "Выгул 30 мин",
                "price": 400
              },
              {
                "id": 2,
                "name": "Выгул 60 мин", 
                "price": 700
              }
            ]
         }


7. POST /api/orders
   - Описание: Создает новый заказ на услуги ситтера
   - REquest:
       - ```json
     
         {
            "sitterId": 2,
            "petId": 1,
            "serviceId": 1,
            "startTime": "2024-01-15T10:00:00Z",
            "endTime": "2024-01-15T11:00:00Z",
            "specialNotes": "Кот боится громких звуков"
          }
    - Response:
       - ```json

         {
            "id": 1,
            "status": "requested",
            "clientId": 1,
            "sitterId": 2,
            "petId": 1,
            "serviceId": 1,
            "startTime": "2024-01-15T10:00:00Z",
            "endTime": "2024-01-15T11:00:00Z",
            "totalPrice": 400,
            "createdAt": "2024-01-10T14:30:00Z"
          }
8. GET /api/orders
   - Описание: Возвращает список заказов текущего пользователя
   - Response:
       - ```json
         [
            {
              "id": 1,
              "status": "completed",
              "sitterName": "Мария Сидорова",
              "petName": "Барсик",
              "serviceName": "Выгул 30 мин",
              "startTime": "2024-01-15T10:00:00Z",
              "totalPrice": 400
            }
         ]
9. POST /api/orders/{id}/review
    - Описание: Добавляет отзыв к завершенному заказу
    - Request:
       - ```json

         {
          "rating": 5,
          "comment": "Отличный ситтер! Кот очень доволен."
        }
    - Response:
       - ```json
  
         {
            "id": 1,
            "orderId": 1,
            "rating": 5,
            "comment": "Отличный ситтер! Кот очень доволен.",
            "createdAt": "2024-01-16T12:00:00Z"
         } 
+сообщения


