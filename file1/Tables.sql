-- 1. TABLO: USERS
CREATE TABLE `Users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `signup_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABLO: CAFE
CREATE TABLE `Cafe` (
    `cafe_id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `address` TEXT NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `latitude` DECIMAL(10,6),
    `longitude` DECIMAL(10,6),
    `phone_number` VARCHAR(15),
    `opening_hours` VARCHAR(100),
    `has_dessert` BOOLEAN DEFAULT FALSE,
    `total_rating` DECIMAL(3,2) DEFAULT 0,
    `review_count` INT DEFAULT 0,
    `menu_count` INT DEFAULT 0
);

-- 3. TABLO: EMPLOYEE
CREATE TABLE `Employee` (
    `employee_id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `experience_years` INT,
    `role` VARCHAR(10),
    `managed_cafe` INT,
    CHECK (`experience_years` >= 0),
    CHECK (`role` IN ('barista', 'manager')),
    FOREIGN KEY (`managed_cafe`) REFERENCES `Cafe`(`cafe_id`)
);

-- 4. TABLO: MENU_ITEM
CREATE TABLE `Menu_Item` (
    `menu_id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(6,2),
    `category` VARCHAR(20),
    `availability_status` BOOLEAN DEFAULT TRUE,
    CHECK (`price` > 0),
    CHECK (`category` IN ('drink', 'food', 'dessert'))
);

-- 5. TABLO: REVIEW
CREATE TABLE `Review` (
    `review_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT,
    `cafe_id` INT,
    `employee_id` INT,
    `menu_id` INT,
    `rating_overall` DECIMAL(3,2),
    `rating_service` DECIMAL(3,2),
    `rating_coffee` DECIMAL(3,2),
    `rating_atmosphere` DECIMAL(3,2),
    `comment` TEXT,
    `review_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `helpful_count` INT DEFAULT 0,
    `reported` BOOLEAN DEFAULT FALSE,
    `review_type` VARCHAR(10),
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`user_id`),
    FOREIGN KEY (`cafe_id`) REFERENCES `Cafe`(`cafe_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `Employee`(`employee_id`),
    FOREIGN KEY (`menu_id`) REFERENCES `Menu_Item`(`menu_id`),
    CHECK (`rating_overall` BETWEEN 1.00 AND 5.00),
    CHECK (`review_type` IN ('cafe', 'employee', 'menu'))
);

-- 6. TABLO: FAVORITE
CREATE TABLE `Favorite` (
    `favorite_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT,
    `cafe_id` INT,
    `added_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `favorite_category` VARCHAR(50),
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`user_id`),
    FOREIGN KEY (`cafe_id`) REFERENCES `Cafe`(`cafe_id`),
    UNIQUE(`user_id`, `cafe_id`)
);

-- 7. TABLO: FOLLOW
CREATE TABLE `Follow` (
    `follow_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT,
    `employee_id` INT,
    `cafe_id` INT,
    `follow_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`user_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `Employee`(`employee_id`),
    FOREIGN KEY (`cafe_id`) REFERENCES `Cafe`(`cafe_id`)
);

-- 8. TABLO: VISIT
CREATE TABLE `Visit` (
    `visit_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT,
    `cafe_id` INT,
    `visit_date` DATE NOT NULL,
    `visit_time` TIME,
    `location_latitude` DECIMAL(10,6),
    `location_longitude` DECIMAL(10,6),
    `menu_items_consumed` TEXT,
    `visit_note` TEXT,
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`user_id`),
    FOREIGN KEY (`cafe_id`) REFERENCES `Cafe`(`cafe_id`)
);

-- 9. ARA TABLOLAR
CREATE TABLE `Employee_Cafe` (
    `employee_id` INT,
    `cafe_id` INT,
    PRIMARY KEY (`employee_id`, `cafe_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `Employee`(`employee_id`),
    FOREIGN KEY (`cafe_id`) REFERENCES `Cafe`(`cafe_id`)
);

CREATE TABLE `Cafe_Menu` (
    `cafe_id` INT,
    `menu_id` INT,
    PRIMARY KEY (`cafe_id`, `menu_id`),
    FOREIGN KEY (`cafe_id`) REFERENCES `Cafe`(`cafe_id`),
    FOREIGN KEY (`menu_id`) REFERENCES `Menu_Item`(`menu_id`)
);
