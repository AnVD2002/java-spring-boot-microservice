#!/bin/bash

# Build script for Java Spring Boot Microservices
# This script builds all services in the correct dependency order

set -e  # Exit on any error

echo "=========================================="
echo "Building Java Spring Boot Microservices"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to build a service
build_service() {
    local service_name=$1
    echo -e "${BLUE}Building ${service_name}...${NC}"
    cd "$service_name"
    if ./mvnw clean install -DskipTests; then
        echo -e "${GREEN}✓ ${service_name} built successfully${NC}"
        cd ..
        echo ""
        return 0
    else
        echo -e "\033[0;31m✗ ${service_name} build failed${NC}"
        cd ..
        return 1
    fi
}

# Step 1: Build BOM Service (must be first)
build_service "bom-service"

# Step 2: Build Common Library Service (must be second)
build_service "common-lib-service"

# Step 3: Build Eureka Server
build_service "eureka-server"

# Step 4: Build Auth Service
build_service "auth-service"

# Step 5: Build API Gateway Service
build_service "api-gateway-service"

# Step 6: Build User Service
build_service "user-service"

# Step 7: Build Notification Service
build_service "notification-service"

echo "=========================================="
echo -e "${GREEN}All services built successfully!${NC}"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Start Eureka Server: cd eureka-server && ./mvnw spring-boot:run"
echo "2. Start infrastructure (PostgreSQL, Redis): docker compose up -d"
echo "3. Start other services in order: auth-service, api-gateway-service, etc."
echo ""
echo "See BUILD_GUIDE.md for detailed instructions."

