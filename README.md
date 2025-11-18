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
- MySQL

## Роли пользователей
- **Неавторизованный пользователь**: 
   Может просматривать публичные профили и объявления, посмотреть информацию о сервисе.
- **Владелец питомца**: 
   Регистрируется, создает профиль питомца, публикует запросы, выбирает и бронирует ситтера, оплачивает услуги, оставляет отзывы.
- **Пет-ситтер**: 
   Регистрируется, создает профиль и портфолио, указывает доступность, отвечает на запросы, принимает бронирования, получает оплату.
- **Администратор**: 
   Управляет пользователями, модерацией контента, просматривает транзакции, решает спорные вопросы.


## Сценарии
[use-case diagrams](./UseCases-diagram.png)


## Схема БД
[Схема БД](./sql/db_v3.svg)

## Сьорка проекта
(пока в ветке дев)
необходим докер
из директории проекта
cd ./backend
docker compose up --build

## API: Endpoint Overview


### 1. Аутентификация и управление профилем


- `POST /auth/register`  
   - **Описание**: Регистрация нового пользователя. В теле запроса передается `role` ('owner' или 'sitter'), на основе которого создается запись в соответствующей таблице (`owners` или `sitters`).
   -  Request Body
       - ```json
         
           {
              "email": "user@example.com",
              "password": "securepassword123",
              "phoneNumber": "+1987654321",
              "firstName": "grzegorz",
              "lastName": "bily",
              "role": "OWNER"
           }
      - Response: Status 201 on successful registration.   
- `POST /auth/login`  
   - **Описание**: Аутентификация пользователя по email и паролю. Возвращает JWT токен для последующих запросов.
   - Request Body
       - ```json
        
            {
              "email": "user@example.com",
              "password": "password123"
            }

-  `GET /api/auth/logout`

- `GET /api/profile`
- `DELETE /api/profile/user`
- `PUT /api/profile/owner`
- `DELETE /api/profile/owner`
- `GET /api/profile/owner/pets`
- `POST /api/profile/owner/pets`
- `PUT /api/profile/owner/pets/{slug}`
- `DELETE /api/profile/owner/pets/{slug}`
- `PUT /api/profile/user`
- `PUT /api/profile/sitter`
- `DELETE /api/profile/sitter`
- `PUT /api/profile/sitter/avaliability`
- `GET /api/profile/sitter/avaliability`


### 2. Sitters

- `GET /api/sitters/search`

### 3. Chat

- `GET /api/chats/{chatId}/messages(page, size)`
- `GET /api/chats/{userId}`

  Подключение к WebSocket
- `/ws`
Протокол: SockJS + STOMP

Эндпоинты для отправки сообщений:
- ` /app/chat.sendMessage`
- `/app/chat.confirmReceived`
- `/app/chat.confirmDelivered`
- `/app/chat.confirmRead`
- `/app/chat.broadcast`
- `/app/chat.broadcastToOnline`
  Эндпоинты для подписки:
1. /user/queue/messages
Назначение: Получение личных сообщений

2. /user/queue/read
Назначение: Уведомления о прочтении сообщений

3. /topic/global
Назначение: Получение broadcast сообщений

4. /topic/onlineUsers
Назначение: Получение сообщений для онлайн пользователей


----------дальше неправда
  
### 3. Owners and Pets

- `GET /owners/me/pets`  
    **Описание**: Владелец получает список своих питомцев.
    
- `POST /owners/me/pets`
   - **Описание**: Владелец добавляет нового питомца в свой профиль.
   - Request
      - ```json {
        {
           "name": "Барсик",
           "type": "cat",
           "breed": "Британская короткошерстная",
           "age": 5,
           "gender": "male",
           "description": "Спокойный и ласковый кот, любит спать. Приучен к лотку.",
           "photo_url": "https://example.com/pets/barsik.jpg"
         }
    - Response: Status 201 on successful adding pet.  
- `GET /owners/me/pets/{petId}`  
    **Описание**: Получение информации о конкретном питомце.
    
- `PUT /owners/me/pets/{petId}`  
    **Описание**: Владелец обновляет информацию о своем питомце.
    
- `DELETE /owners/me/pets/{petId}`  
    **Описание**: Владелец удаляет питомца из своего профиля.
    

### 4. Bookings and orders


- `POST /bookings`
   - **Описание**: Владелец создает новый запрос на бронирование. В теле запроса указывается `sitter_id`, `pet_id`, `service_id`, `start_time`, `end_time`. Статус по умолчанию — `pending`.
   - Request
      - ```json {
        {
           "sitter_user_id": 42,
           "pet_id": 15,
           "service_id": 3,
           "start_time": "2025-10-15T09:00:00Z",
           "end_time": "2025-10-15T18:00:00Z",
           "special_notes": "У кота аллергия на все, пожалуйста, используйте только специальный корм."
         }
    - Response: Status 201 on successful booking.  
    
- `GET /bookings`  
    **Описание**: Получение списка своих бронирований (как для владельца, так и для ситтера). Можно фильтровать по статусу (`pending`, `confirmed`, `completed` и т.д.).
    
- `GET /bookings/{bookingId}`  
    **Описание**: Получение детальной информации о конкретном бронировании.
    
- `PATCH /bookings/{bookingId}/confirm`  
    **Описание**: Ситтер подтверждает бронирование. Статус меняется на `confirmed`.
    
- `PATCH /bookings/{bookingId}/cancel`  
    **Описание**: Отмена бронирования (может быть инициирована как владельцем, так и ситтером). Статус меняется на `cancelled_by_owner` или `cancelled_by_sitter`.
    

### 5. Payments and reviews


- `POST /bookings/{bookingId}/payments`  
    **Описание**: Инициация процесса оплаты для подтвержденного бронирования. Интегрируется с платежной системой (например, Stripe).
    
- `POST /webhooks/payments`  
    **Описание**: Вебхук для получения уведомлений от платежной системы об успешной оплате или ошибке. Обновляет `payment_status` в таблице `bookings`.
    
- `POST /bookings/{bookingId}/reviews`
   - **Описание**: Владелец оставляет отзыв и выставляет рейтинг после завершения бронирования (`completed`).
   - Request
      - ```json {
        {
           "rating": 5,
           "comment": "Все прошло отлично! Мария прекрасно поладила с нашим псом, регулярно присылала фотоотчеты. Очень рекомендую!"
        }
    - Response: Status 201 on successful rewiew.  
- `GET /sitters/{userId}/reviews`  
    **Описание**: Публичный эндпоинт для просмотра всех отзывов о конкретном ситтере.
    

### 6. Взаимодействие во время заказа


- `POST /bookings/{bookingId}/tracking`  
    **Описание**: Ситтер начинает, обновляет или завершает трекинг прогулки, отправляя координаты.
    
- `GET /bookings/{bookingId}/tracking`  
    **Описание**: Владелец получает данные о маршруте прогулки в реальном времени или после ее завершения.
    
- `GET /conversations`  
    **Описание**: Получение списка всех диалогов пользователя.
    
- `GET /conversations/{userId}`  
    **Описание**: Получение истории сообщений с конкретным пользователем.
    
- `POST /conversations/{userId}/messages`
   - **Описание**: Отправка сообщения в диалог. Можно привязать к конкретному бронированию, передав `bookingId`.
   - Request
      - ```json {
         {
           "content": "Здравствуйте! Хотел бы уточнить, свободен ли у вас вечер пятницы?",
           "booking_id": 112 
         }
   - Response
      - ```json {
           {
                 "status_code": 201,
                 "description": "Сообщение успешно отправлено",
                 "body":
                 {
                      "id": 987,
                      "conversation_id": 1234,
                      "sender_user_id": 55,
                      "recipient_user_id": 60,
                      "booking_id": 112,
                      "content": "Здравствуйте! Хотел бы уточнить, свободен ли у вас вечер пятницы?",
                      "is_read": false,
                      "created_at": "2025-09-28T16:00:00Z"
                 }
           }
 
   
    

### 7. Справочники и администрирование

- `GET /services`  
    **Описание**: Получение списка всех услуг и их категорий, доступных на платформе.
    
- `POST /admin/services`  
    **Описание**: (Только для администратора) Создание новой услуги.
    
- `PATCH /admin/sitters/{userId}/verify`  
    **Описание**: (Только для администратора) Верификация профиля ситтера.

## Документация API (Swagger / OpenAPI)

В проекте используется **springdoc-openapi** для автоматической генерации OpenAPI (Swagger) документации.

### Чтобы открыть документацию локально

**1. Локальный запуск (Maven):**
```bash
cd backend
mvn clean spring-boot:run
```
и откройте в браузере:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- raw OpenAPI JSON: http://localhost:8080/v3/api-docs

**2. Через Docker Compose:**
```bash
cd backend
docker compose up --build
```
и откройте http://localhost:8080/swagger-ui/index.html
