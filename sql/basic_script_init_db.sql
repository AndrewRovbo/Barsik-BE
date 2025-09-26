CREATE TABLE `users` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `email` varchar(255) UNIQUE,
  `password_hash` varchar(255),
  `phone_number` varchar(255) UNIQUE,
  `first_name` varchar(255),
  `last_name` varchar(255),
  `avatar_url` varchar(255),
  `address` text,
  `created_at` datetime,
  `updated_at` datetime
);

CREATE TABLE `owners` (
  `user_id` int PRIMARY KEY,
  `about_me` text,
  `is_verified` boolean DEFAULT false
);

CREATE TABLE `sitters` (
  `user_id` int PRIMARY KEY,
  `experience_summary` text,
  `hourly_rate` decimal,
  `average_rating` decimal DEFAULT 0,
  `reviews_count` int DEFAULT 0,
  `is_verified` boolean DEFAULT false,
  `is_available` boolean DEFAULT true
);

CREATE TABLE `services` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `description` text,
  `category` varchar(255)
);

CREATE TABLE `sitter_services` (
  `sitter_user_id` int,
  `service_id` int,
  `price` decimal,
  PRIMARY KEY (`sitter_user_id`, `service_id`)
);

CREATE TABLE `availability` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `sitter_user_id` int,
  `start_time` datetime,
  `end_time` datetime
);

CREATE TABLE `pets` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `owner_user_id` int,
  `name` varchar(255),
  `type` varchar(255),
  `breed` varchar(255),
  `age` tinyint,
  `gender` varchar(255),
  `description` text,
  `photo_url` varchar(255)
);

CREATE TABLE `bookings` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `owner_user_id` int,
  `sitter_user_id` int,
  `pet_id` int,
  `service_id` int,
  `booking_status` varchar(255) COMMENT 'pending, confirmed, cancelled_by_owner, cancelled_by_sitter, completed',
  `payment_status` varchar(255) COMMENT 'unpaid, paid, refunded',
  `start_time` datetime,
  `end_time` datetime,
  `total_price` decimal,
  `special_notes` text,
  `created_at` datetime
);

CREATE TABLE `reviews` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int UNIQUE,
  `owner_user_id` int,
  `sitter_user_id` int,
  `rating` tinyint,
  `comment` text,
  `created_at` datetime
);

CREATE TABLE `payments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int,
  `amount` decimal,
  `status` varchar(255) COMMENT 'pending, succeeded, failed',
  `payment_method` varchar(255),
  `transaction_id` varchar(255),
  `created_at` datetime
);

CREATE TABLE `walk_trackings` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int,
  `start_time` datetime,
  `end_time` datetime,
  `path_geojson` json,
  `distance_meters` int
);

CREATE TABLE `messages` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `sender_user_id` int,
  `recipient_user_id` int,
  `booking_id` int,
  `content` text,
  `is_read` boolean DEFAULT false,
  `created_at` datetime
);

ALTER TABLE `owners` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `sitters` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `sitter_services` ADD FOREIGN KEY (`sitter_user_id`) REFERENCES `sitters` (`user_id`);

ALTER TABLE `sitter_services` ADD FOREIGN KEY (`service_id`) REFERENCES `services` (`id`);

ALTER TABLE `availability` ADD FOREIGN KEY (`sitter_user_id`) REFERENCES `sitters` (`user_id`);

ALTER TABLE `pets` ADD FOREIGN KEY (`owner_user_id`) REFERENCES `owners` (`user_id`);

ALTER TABLE `bookings` ADD FOREIGN KEY (`owner_user_id`) REFERENCES `owners` (`user_id`);

ALTER TABLE `bookings` ADD FOREIGN KEY (`sitter_user_id`) REFERENCES `sitters` (`user_id`);

ALTER TABLE `bookings` ADD FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`);

ALTER TABLE `bookings` ADD FOREIGN KEY (`service_id`) REFERENCES `services` (`id`);

ALTER TABLE `reviews` ADD FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `reviews` ADD FOREIGN KEY (`owner_user_id`) REFERENCES `owners` (`user_id`);

ALTER TABLE `reviews` ADD FOREIGN KEY (`sitter_user_id`) REFERENCES `sitters` (`user_id`);

ALTER TABLE `payments` ADD FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `walk_trackings` ADD FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `messages` ADD FOREIGN KEY (`sender_user_id`) REFERENCES `users` (`id`);

ALTER TABLE `messages` ADD FOREIGN KEY (`recipient_user_id`) REFERENCES `users` (`id`);

ALTER TABLE `messages` ADD FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);
