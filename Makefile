# Books API - Makefile
# Convenient commands for local development and deployment

.PHONY: help build up down logs restart clean test deploy status

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "Books API - Available Commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Build Docker images
	docker-compose build

up: ## Start all services
	docker-compose up -d
	@echo "Services started. API available at http://localhost:7070/api/v1"

up-logs: ## Start all services with logs
	docker-compose up

down: ## Stop all services
	docker-compose down

logs: ## View logs
	docker-compose logs -f

logs-api: ## View API logs only
	docker-compose logs -f books-api

logs-db: ## View database logs only
	docker-compose logs -f postgres

restart: ## Restart all services
	docker-compose restart

restart-api: ## Restart API service only
	docker-compose restart books-api

ps: ## Show running containers
	docker-compose ps

status: ## Show service status and health
	@docker-compose ps
	@echo ""
	@echo "API Routes: http://localhost:7070/api/v1/routes"

clean: ## Stop services and remove volumes
	docker-compose down -v

clean-all: ## Remove everything including images
	docker-compose down -v --rmi all

rebuild: ## Rebuild and restart services
	docker-compose down
	docker-compose up -d --build

test: ## Run tests with Maven
	mvn clean test

package: ## Build JAR file
	mvn clean package

install: ## Install dependencies
	mvn clean install

dev-up: ## Start only database for local development
	docker-compose up -d postgres

shell-api: ## Open shell in API container
	docker-compose exec books-api sh

shell-db: ## Open PostgreSQL shell
	docker-compose exec postgres psql -U postgres -d books_db

backup-db: ## Backup database
	@mkdir -p backups
	docker-compose exec postgres pg_dump -U postgres books_db > backups/backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Database backed up to backups/"

restore-db: ## Restore database from backup (use BACKUP_FILE=filename)
	@if [ -z "$(BACKUP_FILE)" ]; then \
		echo "Error: Please specify BACKUP_FILE=filename"; \
		echo "Example: make restore-db BACKUP_FILE=backups/backup_20231026_120000.sql"; \
		exit 1; \
	fi
	docker-compose exec -T postgres psql -U postgres books_db < $(BACKUP_FILE)

deploy: ## Deploy to remote server (requires REMOTE_HOST)
	@./deploy.sh

env: ## Create .env file from example
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo ".env file created from .env.example"; \
		echo "Please edit .env with your configuration"; \
	else \
		echo ".env file already exists"; \
	fi

check: ## Check if services are healthy
	@echo "Checking API health..."
	@curl -f http://localhost:7070/api/v1/routes > /dev/null 2>&1 && echo "✓ API is healthy" || echo "✗ API is not responding"
	@echo "Checking Database health..."
	@docker-compose exec postgres pg_isready -U postgres > /dev/null 2>&1 && echo "✓ Database is healthy" || echo "✗ Database is not responding"
