# PageKeeper
A Spring Boot web application that allows users to track their personal book collection. Users can add books with cover images uploaded to AWS S3, view their own library, and delete books.

# Features
## User Accounts & Roles

## Regular Users can:

Add books with details and a cover image

View only their own books

Delete their own books
<img width="1481" height="469" alt="image" src="https://github.com/user-attachments/assets/ecada410-854a-4692-8ac6-73530f5e8b07" />

## Admins can:

View all books from all users

Delete any book
<img width="1510" height="425" alt="image" src="https://github.com/user-attachments/assets/ebf6745f-5745-405b-bd42-11bcbab192f8" />

## Book Management
Add book details (title, author, etc.)

Upload and display cover images (stored on Amazon S3)

List and manage books through a web interface

## Security & Authentication

Role-based access (Admin/User)

Users restricted to their own library

# Tech Stack
Backend: Spring Boot (Java)

Frontend: Thymeleaf

Database: MySQL

Authentication: Spring Security

Storage: AWS S3 (for book cover images)
