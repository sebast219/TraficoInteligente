@echo off
echo 🚑 Sistema de Tráfico Inteligente - Docker
echo ==========================================

REM Verificar si Docker está corriendo
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Docker no está corriendo
    echo Por favor inicia Docker Desktop
    pause
    exit /b 1
)

REM Instrucciones para Windows
echo.
echo 📌 IMPORTANTE PARA WINDOWS:
echo 1. Instala VcXsrv desde: https://sourceforge.net/projects/vcxsrv/
echo 2. Ejecuta XLaunch con estas opciones:
echo    - Multiple windows
echo    - Display number: 0
echo    - Start no client
echo    - Disable access control: ACTIVADO
echo.
echo ⏳ Esperando 5 segundos para que inicies XLaunch...
timeout /t 5 /nobreak

REM Construir imagen
echo 📦 Construyendo imagen Docker...
docker-compose build

REM Ejecutar contenedor
echo 🚀 Iniciando simulación...
set DISPLAY=host.docker.internal:0
docker run -it --rm ^
    -e DISPLAY=%DISPLAY% ^
    trafico-inteligente

pause

