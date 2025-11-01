# 🐳 Guía de Docker - Sistema de Tráfico Inteligente

## Instalación Rápida

### Requisitos:

- Docker Desktop instalado
- (Windows) VcXsrv instalado
- (Linux/Mac) X11 configurado

### Ejecución:

**Linux/Mac:**

```bash
chmod +x run.sh
./run.sh
```

**Windows:**

```cmd
run.bat
```

**O con Make:**

```bash
make run
```

**O manual:**

```bash
docker-compose build
docker-compose up
```

## Comandos Útiles

```bash
# Ver logs
docker logs simulador-trafico

# Entrar al contenedor
docker exec -it simulador-trafico bash

# Detener
docker-compose down

# Limpiar todo
docker system prune -a
```

## Solución de Problemas

### "Cannot connect to X server"

```bash
# Linux/Mac
xhost +local:docker

# Windows: Asegúrate de que VcXsrv esté corriendo
```

### "Docker daemon not running"

- Inicia Docker Desktop y espera a que cargue completamente

