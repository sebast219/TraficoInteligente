.PHONY: build run stop clean help

help:
	@echo "🚑 Sistema de Tráfico Inteligente - Comandos"
	@echo "============================================="
	@echo "make build   - Construir imagen Docker"
	@echo "make run     - Ejecutar simulación"
	@echo "make stop    - Detener contenedor"
	@echo "make clean   - Limpiar contenedores e imágenes"
	@echo "make logs    - Ver logs del contenedor"

build:
	@echo "📦 Construyendo imagen Docker..."
	docker-compose build

run:
	@echo "🚀 Iniciando simulación..."
	@xhost +local:docker 2>/dev/null || true
	docker-compose up

stop:
	@echo "🛑 Deteniendo contenedor..."
	docker-compose down

clean:
	@echo "🧹 Limpiando Docker..."
	docker-compose down -v
	docker rmi trafico-inteligente 2>/dev/null || true

logs:
	docker logs -f simulador-trafico

