-- Create the employee database if it doesn't exist
CREATE DATABASE IF NOT EXISTS employe;
USE employe;

-- Drop existing tables to start fresh
DROP TABLE IF EXISTS `employee_salary`;
DROP TABLE IF EXISTS `employe`;
DROP TABLE IF EXISTS `admin`;

-- Create the admin table
CREATE TABLE `admin` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Insert a default admin user (admin/admin123)
INSERT INTO `admin` (username, password) VALUES ('admin', 'admin123');

-- Create the main employee table
CREATE TABLE `employee` (
  `employee_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `fullName` VARCHAR(100) NOT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `phoneNum` VARCHAR(20) DEFAULT NULL,
  `position` VARCHAR(100) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `image` VARCHAR(255) DEFAULT NULL,
  `date` DATE DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `qualification` VARCHAR(100) DEFAULT NULL,
  `specialization` VARCHAR(100) DEFAULT NULL,
  `department` VARCHAR(100) DEFAULT NULL,
  `employee_type` VARCHAR(50) DEFAULT NULL,
  `date_of_birth` DATE DEFAULT NULL,
  `emergency_contact` VARCHAR(100) DEFAULT NULL,
  `hire_date` DATE DEFAULT NULL,
  `salary_grade` VARCHAR(50) DEFAULT NULL,
  `bank_account` VARCHAR(100) DEFAULT NULL,
  `tax_number` VARCHAR(100) DEFAULT NULL,
  `insurance_number` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the employee_salary table for salary details
CREATE TABLE `employee_salary` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `employee_id` INT NOT NULL,
  `fullName` VARCHAR(100) DEFAULT NULL,
  `position` VARCHAR(100) DEFAULT NULL,
  `salary` DOUBLE DEFAULT '0',
  `date` DATE DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `employee_id_fk_idx` (`employee_id`),
  CONSTRAINT `employee_id_fk` FOREIGN KEY (`employee_id`)
  REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

select *from employee_salary;
select *from employee;
select *from admin;