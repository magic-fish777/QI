# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

QiQiPlay is a Java web application based on the RuoYi framework, featuring a SpringBoot backend with Vue.js frontend. It's a full-stack enterprise management system with role-based access control, code generation, and administrative features.

## Architecture

### Backend (Java/Maven)
- **Multi-module Maven project** with the following structure:
  - `qiqiplay-admin`: Main web application entry point (Spring Boot app)
  - `qiqiplay-framework`: Core framework and security components
  - `qiqiplay-system`: System management modules (users, roles, menus, etc.)
  - `qiqiplay-common`: Shared utilities and common components
  - `qiqiplay-quartz`: Scheduled task management
  - `qiqiplay-generator`: Code generation functionality

### Frontend (Vue.js)
- **Vue 2.6.12** with Element UI
- Located in `qiqiplay-ui/` directory
- Uses Vue Router, Vuex for state management
- Build tool: Vue CLI

### Technology Stack
- **Backend**: Spring Boot 2.5.15, Spring Security, MyBatis, MySQL
- **Frontend**: Vue.js 2.6, Element UI, Axios
- **Database**: MySQL with Druid connection pool
- **Security**: JWT authentication
- **Build**: Maven for backend, npm for frontend

## Development Commands

### Backend
```bash
# Build entire project
mvn clean package

# Run development server (admin module)
cd qiqiplay-admin
mvn spring-boot:run

# Build specific module
cd qiqiplay-admin
mvn clean package

# Run packaged application
java -jar qiqiplay-admin/target/qiqiplay-admin.jar
```

### Frontend
```bash
cd qiqiplay-ui

# Install dependencies
npm install

# Development server (http://localhost:80)
npm run dev

# Build for production
npm run build:prod

# Build for staging
npm run build:stage
```

### Production Deployment
- Use `ry.sh` (Linux/Mac) or `ry.bat` (Windows) for application lifecycle management
- Scripts support: start, stop, restart, status operations
- JAR file: `qiqiplay-admin.jar` (generated in admin module)

## Key Configuration Files

- **Root POM**: `pom.xml` - Defines dependencies and module structure
- **Admin POM**: `qiqiplay-admin/pom.xml` - Main application configuration
- **Frontend**: `qiqiplay-ui/package.json` - Vue.js dependencies and scripts
- **Application Properties**: Located in each module's `src/main/resources/`

## Code Organization

### Java Package Structure
All modules follow `com.qiqiplay.*` package naming:
- Controllers: Handle HTTP requests and responses
- Services: Business logic implementation
- Mappers: MyBatis data access layer
- Domain/Entity: Database entity classes

### Vue.js Structure
- `src/api/`: API service calls
- `src/components/`: Reusable Vue components
- `src/views/`: Page components
- `src/router/`: Route definitions
- `src/store/`: Vuex state management

## Database
- Uses MySQL database
- Schema and sample data available in `sql/` directory
- Druid connection pooling configured in application properties

## Notable Features
- **Role-based permission system** with menu-level access control
- **Code generator** for CRUD operations (frontend + backend)
- **Scheduled task management** with Quartz
- **System monitoring** (CPU, memory, cache, database connections)
- **Multi-tenant data permissions** by department/organization
- **API documentation** with Swagger 3