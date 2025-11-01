# 🚑 Sistema de Tráfico Urbano Inteligente

Sistema de simulación de tráfico que demuestra el funcionamiento de una ambulancia con **prioridad semafórica inteligente** en una ciudad, utilizando el **algoritmo de Dijkstra** para calcular rutas óptimas.

---

## 📋 Características Principales

✅ **Programación Orientada a Objetos (POO)**  
✅ **Estructuras de datos**: Grafo, Cola de Prioridad, Listas, Mapas  
✅ **Algoritmo de Dijkstra** para rutas más cortas  
✅ **Semáforos inteligentes** con prioridad dinámica  
✅ **Animación 2D en JavaFX Canvas**  
✅ **Interfaz gráfica interactiva**  

---

## 🏗️ Estructura del Proyecto

### Estructura Completa del Proyecto

```
TraficoInteligente-1/
│
├── 📄 Dockerfile                          # Configuración Docker
├── 📄 docker-compose.yml                  # Orquestación Docker (Linux/Mac)
├── 📄 docker-compose.windows.yml          # Orquestación Docker (Windows)
├── 📄 pom.xml                             # Configuración Maven
├── 📄 .dockerignore                       # Exclusiones Docker
├── 📄 run.sh                              # Script ejecución Linux/Mac
├── 📄 run.bat                             # Script ejecución Windows
├── 📄 Makefile                            # Comandos Make
├── 📄 README.md                           # Este archivo
├── 📄 README_DOCKER.md                    # Documentación Docker
├── 📄 INFORME_AUDITORIA.md                # Informe de auditoría completo
├── 📄 TraficoInteligente-1.iml           # Configuración IntelliJ
│
└── 📁 TraficoInteligente/
    ├── 📄 README.md                        # Documentación del módulo
    │
    └── 📁 src/
        └── 📁 com/
            └── 📁 trafico/
                ├── 📄 Main.java                    # Punto de entrada
                │
                ├── 📁 model/                       # Modelo de datos (POO)
                │   ├── 📄 Nodo.java               # Intersección con semáforo
                │   ├── 📄 Arista.java             # Vía entre intersecciones
                │   ├── 📄 Grafo.java              # Grafo + Dijkstra
                │   ├── 📄 Vehiculo.java           # Clase base abstracta
                │   ├── 📄 Ambulancia.java         # Vehículo de emergencia
                │   └── 📄 Semaforo.java           # Control semafórico
                │
                ├── 📁 controller/                  # Controladores
                │   └── 📄 MapaController.java     # Lógica de UI y animación
                │
                └── 📁 util/                        # Utilidades
                    └── 📄 Simulador.java          # Motor de simulación
```

### ✅ Checklist de Archivos

#### Archivos de Código Fuente (src/)
- [x] Main.java
- [x] model/Nodo.java
- [x] model/Arista.java
- [x] model/Grafo.java
- [x] model/Vehiculo.java
- [x] model/Ambulancia.java
- [x] model/Semaforo.java
- [x] controller/MapaController.java
- [x] util/Simulador.java

#### Archivos de Configuración Docker
- [x] Dockerfile
- [x] docker-compose.yml
- [x] docker-compose.windows.yml
- [x] pom.xml
- [x] .dockerignore

#### Scripts de Ejecución
- [x] run.sh (Linux/Mac)
- [x] run.bat (Windows)
- [x] Makefile (opcional)

#### Documentación
- [x] README.md
- [x] README_DOCKER.md
- [x] INFORME_AUDITORIA.md

---

## 🔧 Estado de Integración

### Estado Actual del Proyecto

⚠️ **IMPORTANTE:** Este proyecto está en fase de **ESQUELETO/PLANTILLA**. El código Java tiene la estructura básica pero requiere implementación completa.

#### Componentes Listos:
- ✅ **Estructura de directorios:** Completa y correcta
- ✅ **Configuración Docker:** Completamente funcional
- ✅ **Configuración Maven:** Correcta con dependencias JavaFX
- ✅ **Scripts de ejecución:** Preparados para Docker

#### Componentes Pendientes:
- ❌ **Código Java:** Packages comentados, sin implementación funcional
- ❌ **Método main():** No implementado en Main.java
- ❌ **Clases del modelo:** Solo contienen TODOs, sin código funcional
- ❌ **Algoritmo de Dijkstra:** No implementado
- ❌ **Interfaz JavaFX:** No implementada

**Para ver el estado detallado, consulta:** `INFORME_AUDITORIA.md`

#### Próximos Pasos para Completar el Proyecto:

1. **Descomentar packages** en todos los archivos Java
2. **Implementar estructura básica** de cada clase
3. **Implementar algoritmo de Dijkstra** en Grafo.java
4. **Implementar interfaz JavaFX** en Main.java y MapaController.java
5. **Implementar lógica de simulación** en Simulador.java

---

## 🚀 Cómo Ejecutar el Proyecto

### 🐳 Opción A: Con Docker (RECOMENDADO) ⭐

**¿Por qué Docker?**
- ✅ No necesitas instalar Java ni JavaFX
- ✅ Funciona en cualquier sistema operativo
- ✅ Configuración automática
- ✅ Entorno reproducible

**Requisitos:**
- Docker Desktop instalado
- **Linux/Mac**: X11 configurado (generalmente ya incluido)
- **Windows**: VcXsrv o Xming instalado

**Ejecución rápida:**

```bash
# Linux/Mac
chmod +x run.sh
./run.sh

# Windows
run.bat
```

O manualmente:

```bash
# Construir imagen
docker-compose build

# Ejecutar
docker-compose up
```

### 💻 Opción B: Instalación Local

**Requisitos Previos:**
- **Java JDK 11 o superior**
- **JavaFX SDK** (si no está incluido en tu JDK)
- **IDE recomendado**: IntelliJ IDEA, Eclipse o NetBeans

### Pasos de Instalación

#### 1️⃣ Crear el Proyecto

**En IntelliJ IDEA:**
```
File → New → Project → JavaFX Application
```

**En Eclipse:**
```
File → New → JavaFX Project
```

#### 2️⃣ Copiar los Archivos

Copia todo el código en la estructura de carpetas indicada:

```
src/com/trafico/Main.java
src/com/trafico/model/Nodo.java
src/com/trafico/model/Arista.java
src/com/trafico/model/Grafo.java
src/com/trafico/model/Vehiculo.java
src/com/trafico/model/Ambulancia.java
src/com/trafico/model/Semaforo.java
src/com/trafico/controller/MapaController.java
src/com/trafico/util/Simulador.java
```

#### 3️⃣ Configurar JavaFX

**Si usas Maven**, agrega al `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>
</dependencies>
```

**Si no usas Maven**, descarga JavaFX SDK:
- Descarga desde: https://openjfx.io/
- Agrega las librerías al classpath del proyecto

#### 4️⃣ Ejecutar

**Desde el IDE:**
```
Run → Main.java
```

**Desde terminal (con JavaFX configurado):**
```bash
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/com/trafico/*.java src/com/trafico/*/*.java

java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin com.trafico.Main
```

---

## 🎮 Cómo Usar la Aplicación

### Controles

| Botón | Función |
|-------|---------|
| **🚑 Iniciar Emergencia** | Inicia la ruta de la ambulancia desde la base hasta el accidente |
| **⏸ Pausar** | Pausa la simulación |
| **🔄 Reiniciar** | Reinicia la simulación al estado inicial |

### Visualización

- **🟢 Verde**: Semáforo abierto (ambulancia tiene prioridad)
- **🟡 Amarillo**: Semáforo en transición
- **🔴 Rojo**: Semáforo cerrado
- **🚑**: Ambulancia en movimiento
- **📍**: Ubicación del accidente
- **🏥**: Hospital destino
- **Línea verde**: Ruta calculada por Dijkstra

---

## 🧠 Conceptos Técnicos Implementados

### 1. Estructuras de Datos

| Estructura | Uso en el Proyecto |
|------------|-------------------|
| **Grafo** | Representación de la ciudad (nodos = intersecciones, aristas = calles) |
| **Lista de Adyacencia** | Almacenar conexiones entre nodos |
| **Cola de Prioridad** | Implementación de Dijkstra |
| **HashMap** | Almacenar nodos por ID |
| **ArrayList** | Almacenar aristas y rutas |

### 2. Algoritmo de Dijkstra

```
DIJKSTRA(Grafo G, Nodo origen, Nodo destino):
    1. Inicializar distancias[n] = ∞ para todos los nodos
    2. distancias[origen] = 0
    3. Agregar origen a cola_prioridad
    
    4. Mientras cola_prioridad no esté vacía:
        a. nodo_actual = extraer nodo con menor distancia
        b. Si nodo_actual == destino: terminar
        c. Para cada vecino de nodo_actual:
            i. nueva_distancia = distancias[nodo_actual] + peso_arista
            ii. Si nueva_distancia < distancias[vecino]:
                - Actualizar distancias[vecino]
                - Agregar vecino a cola_prioridad
    
    5. Reconstruir ruta usando predecesores
```

### 3. Programación Orientada a Objetos

**Herencia:**
```
Vehiculo (clase abstracta)
    ↓
Ambulancia (hereda de Vehiculo)
```

**Composición:**
```
Nodo contiene → Semaforo
Nodo contiene → List<Arista>
Grafo contiene → Map<String, Nodo>
```

**Polimorfismo:**
```java
@Override
public void mover(double deltaX, double deltaY) {
    // Implementación específica de Ambulancia
}
```

---

## 📊 Complejidad del Algoritmo

| Operación | Complejidad |
|-----------|-------------|
| **Dijkstra con Cola de Prioridad** | O((V + E) log V) |
| **Agregar Nodo** | O(1) |
| **Agregar Arista** | O(1) |
| **Buscar Nodo** | O(1) con HashMap |

Donde:
- V = número de vértices (nodos/intersecciones)
- E = número de aristas (calles)

---

## 🔧 Personalización del Mapa

Para agregar más nodos o modificar la ciudad, edita `Simulador.java`:

```java
// Agregar nuevo nodo
Nodo nuevoNodo = new Nodo("n14", "NuevaCalle", 700, 400);
grafo.agregarNodo(nuevoNodo);

// Conectar con arista bidireccional
crearAristaBidireccional(nodoExistente, nuevoNodo, distancia);
```

---

## 🎯 Posibles Extensiones

1. **Múltiples ambulancias** simultáneas
2. **Tráfico dinámico** (cambios aleatorios en peso de aristas)
3. **Algoritmo A*** en lugar de Dijkstra (con heurística)
4. **Obstáculos temporales** (accidentes que bloquean vías)
5. **Estadísticas avanzadas** (tiempo promedio, consumo de combustible)
6. **Guardado/carga** de configuraciones de ciudad
7. **Modo noche/día** con cambios en el tráfico

---

## 📚 Conceptos para Estudiar

Si quieres profundizar en los conceptos usados:

- **Grafos**: Representación, tipos, aplicaciones
- **Algoritmos de camino más corto**: Dijkstra, A*, Bellman-Ford
- **Estructuras de datos**: Colas de prioridad, heaps
- **JavaFX**: Canvas, AnimationTimer, GraphicsContext
- **Patrones de diseño**: MVC (Model-View-Controller)
- **Programación concurrente**: Threads, tareas asíncronas

---

## 🐛 Solución de Problemas

### Error: "JavaFX not found"
**Solución:**
- Asegúrate de tener JavaFX SDK instalado
- Configura las librerías en el proyecto
- Agrega los módulos en VM options: `--module-path /path/to/javafx --add-modules javafx.controls`

### Error: "NullPointerException en Grafo"
**Solución:**
- Verifica que todos los nodos estén agregados antes de crear aristas
- Revisa que los IDs de nodos sean correctos

### La ambulancia no se mueve
**Solución:**
- Asegúrate de presionar el botón "Iniciar Emergencia"
- Verifica que la ruta exista entre los nodos

---

## 👨‍💻 Autor

Proyecto desarrollado por Sebastian yepes y Andrés Sánchez:

- Estructuras de datos aplicadas
- Algoritmos de grafos
- Programación orientada a objetos
- Interfaces gráficas con JavaFX

---

## 📄 Licencia

Este proyecto es de código abierto y puede ser usado con fines educativos.

---

## 🎓 Uso Académico

Este proyecto es ideal para:

**Conceptos evaluables:**
- Implementación de grafos
- Algoritmo de Dijkstra
- Diseño orientado a objetos
- Interfaces gráficas
- Documentación de código

