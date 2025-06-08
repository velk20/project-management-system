# Project Management System

A full-stack web application designed to streamline project planning, task tracking, and team collaboration.
Built with a modern tech stack, it offers an intuitive interface for managing projects efficiently.

## Features

- **Project Management**: Create, update, and manage multiple projects.
- **Task Tracking**: Assign tasks, set priorities, and monitor progress.
- **User Roles**: Define roles and permissions for team members.
- **Responsive Design**: Optimized for both desktop and mobile devices.

## Tech Stack

- **Frontend**: Angular, TypeScript, HTML, CSS
- **Backend**: Java, Spring Boot
- **Security**: JWT
- **Aditional**: Docker, Redis
- **Version Control**: Git

## Getting Started

### Prerequisites

- Java Development Kit (JDK) installed
- Node.js and npm installed
- Docker engine installed
- MySQL installed
- Git installed

### Starting project

1. Clone the repository:
   ```bash
   git clone https://github.com/velk20/project-management-system.git
   cd project-management-system
   ```

2. Navigate to the backend directory and start the Docker file and the server:
   ```bash
   cd projectmanagement
   docker compose up -d -> This start the Redis container
   mvn spring-boot:run -> Starts the spring boot backed
   ```

3. Navigate to the frontend directory and start the client:
   ```bash
   cd ../projectmanagement-client
   npm install
   npm start
   ```
