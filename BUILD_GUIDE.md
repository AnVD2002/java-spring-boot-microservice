# Build Guide - Java Spring Boot Microservices

This guide provides step-by-step instructions to build and run this microservices project.

## Prerequisites

### Required Software
- **Java Development Kit (JDK)**: 
  - JDK 21 (for `api-gateway-service`)
  - JDK 17 (for other services)
  - Or use JDK 21 for all services (backward compatible)
- **Maven**: Version 3.6+ (or use Maven Wrapper included in each service)
- **Docker & Docker Compose**: For running PostgreSQL, Redis, and other infrastructure
- **Git**: For version control (if cloning from repository)

### Verify Prerequisites
```bash
# Check Java version
java -version

# Check Maven version (or use ./mvnw in each service directory)
mvn -version

# Check Docker
docker --version
docker compose version
```

## Project Structure

This is a microservices architecture with the following services:

1. **eureka-server** - Service discovery server (port 8761)
2. **bom-service** - Bill of Materials (dependency management)
3. **common-lib-service** - Shared library used by other services
4. **auth-service** - Authentication service (port 8081)
5. **api-gateway-service** - API Gateway (uses Spring Cloud Gateway)
6. **user-service** - User management service
7. **notification-service** - Notification service
8. **bom-service** - Bill of Materials

## Build Order

Services must be built in a specific order due to dependencies:

1. **bom-service** (must be built first - dependency management)
2. **common-lib-service** (shared library used by other services)
3. **eureka-server** (service discovery - should start first)
4. All other services (can be built in any order)

## Step-by-Step Build Instructions

### Step 1: Install Dependencies to Local Maven Repository

Build the BOM service first (Bill of Materials for dependency management):

```bash
cd bom-service
./mvnw clean install
cd ..
```

### Step 2: Build Common Library Service

Build the shared library that other services depend on:

```bash
cd common-lib-service
./mvnw clean install
cd ..
```

### Step 3: Build All Other Services

Now you can build all remaining services. You can build them individually or use a script:

#### Option A: Build Services Individually

```bash
# Build Eureka Server
cd eureka-server
./mvnw clean install
cd ..

# Build Auth Service
cd auth-service
./mvnw clean install
cd ..

# Build API Gateway Service
cd api-gateway-service
./mvnw clean install
cd ..

# Build User Service
cd user-service
./mvnw clean install
cd ..

# Build Notification Service
cd notification-service
./mvnw clean install
cd ..
```

#### Option B: Build All Services with a Script

Create a build script `build-all.sh`:

```bash
#!/bin/bash

echo "Building bom-service..."
cd bom-service && ./mvnw clean install && cd ..

echo "Building common-lib-service..."
cd common-lib-service && ./mvnw clean install && cd ..

echo "Building eureka-server..."
cd eureka-server && ./mvnw clean install && cd ..

echo "Building auth-service..."
cd auth-service && ./mvnw clean install && cd ..

echo "Building api-gateway-service..."
cd api-gateway-service && ./mvnw clean install && cd ..

echo "Building user-service..."
cd user-service && ./mvnw clean install && cd ..

echo "Building notification-service..."
cd notification-service && ./mvnw clean install && cd ..

echo "All services built successfully!"
```

Make it executable and run:
```bash
chmod +x build-all.sh
./build-all.sh
```

### Step 4: Start Infrastructure Services

Before starting the microservices, you need to start the required infrastructure:

#### Start Eureka Server

```bash
cd eureka-server
./mvnw spring-boot:run
# Or: java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
```

Eureka Server will be available at: http://localhost:8761

#### Start Database and Redis (using Docker Compose)

For services that have `compose.yaml` files, you can start their dependencies:

```bash
# Start PostgreSQL and Redis for auth-service
cd auth-service
docker compose up -d

# Or start for api-gateway-service
cd api-gateway-service
docker compose up -d
```

**Note**: You may need to configure database connections in `application.yml` files according to your Docker Compose settings.

### Step 5: Start Microservices

Start services in the following order:

1. **Eureka Server** (already started in Step 4)
2. **Auth Service**
3. **API Gateway Service**
4. **User Service**
5. **Notification Service**

#### Start Auth Service

```bash
cd auth-service
./mvnw spring-boot:run
# Or: java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

#### Start API Gateway Service

```bash
cd api-gateway-service
./mvnw spring-boot:run
# Or: java -jar target/api-gateway-service-0.0.1-SNAPSHOT.jar
```

#### Start Other Services

```bash
# User Service
cd user-service
./mvnw spring-boot:run

# Notification Service
cd notification-service
./mvnw spring-boot:run
```

## Running with Docker Compose (Alternative)

If you prefer to run everything with Docker, you'll need to:

1. Create Dockerfiles for each service
2. Create a root-level `docker-compose.yml` that orchestrates all services
3. Build Docker images for each service

## Verification

### Check Service Registration

1. Open Eureka Dashboard: http://localhost:8761
2. Verify that all services are registered and showing as "UP"

### Test API Gateway

The API Gateway should route requests to the appropriate microservices. Check the gateway configuration in `api-gateway-service/src/main/resources/application.yml`.

## Troubleshooting

### Common Issues

1. **Build fails with "common-lib-service not found"**
   - Solution: Make sure you built `bom-service` and `common-lib-service` first and installed them to your local Maven repository

2. **Port already in use**
   - Solution: Check which process is using the port and stop it, or change the port in `application.yml`

3. **Database connection errors**
   - Solution: Ensure PostgreSQL is running and credentials in `application.yml` match your database configuration

4. **Eureka registration fails**
   - Solution: Ensure Eureka Server is running and accessible at the configured URL

5. **Java version mismatch**
   - Solution: Ensure you're using the correct Java version (JDK 21 for api-gateway-service, JDK 17+ for others)

### Build Commands Reference

```bash
# Clean build (removes target directory and rebuilds)
./mvnw clean install

# Skip tests during build
./mvnw clean install -DskipTests

# Run application
./mvnw spring-boot:run

# Package as JAR
./mvnw clean package

# Run JAR file
java -jar target/<service-name>-0.0.1-SNAPSHOT.jar
```

## Development Tips

1. **Use Maven Wrapper**: Each service includes `mvnw` (Maven Wrapper), so you don't need Maven installed globally
2. **IDE Setup**: Import each service as a separate Maven project in your IDE
3. **Hot Reload**: Spring Boot DevTools is included in some services for automatic restart during development
4. **Configuration**: Check `application.yml` or `application.properties` in each service for configuration

## Next Steps

After successfully building and starting all services:

1. Configure authentication (JWT keys, OAuth2 credentials)
2. Set up proper database schemas (Liquibase is configured in auth-service)
3. Configure service-to-service communication
4. Set up monitoring and logging
5. Configure production environment variables

## Additional Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Cloud Documentation: https://spring.io/projects/spring-cloud
- Maven Documentation: https://maven.apache.org/guides/

