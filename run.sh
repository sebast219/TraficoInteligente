#!/bin/bash

echo "🚑 Sistema de Tráfico Inteligente - Docker"
echo "=========================================="

# Permitir conexiones X11
xhost +local:docker

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo"
    exit 1
fi

# Construir imagen si no existe
if [[ "$(docker images -q trafico-inteligente 2> /dev/null)" == "" ]]; then
    echo "📦 Construyendo imagen Docker..."
    docker-compose build
fi

# Ejecutar contenedor
echo "🚀 Iniciando simulación..."
docker-compose up

# Limpiar permisos X11 al terminar
xhost -local:docker

