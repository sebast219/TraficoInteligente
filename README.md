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
├── 📄 pom.xml                             # Configuración Maven
├── 📄 README.md                           # Este archivo
├── 📄 GUIA_INTELLIJ.md                     # Guía de ejecución en IntelliJ
├── 📄 REQUISITOS_PROYECTO.md               # Requisitos y estado del proyecto
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

#### Archivos de Configuración
- [x] pom.xml (Maven)

#### Documentación
- [x] README.md
- [x] GUIA_INTELLIJ.md
- [x] REQUISITOS_PROYECTO.md
- [x] INFORME_AUDITORIA.md

---

## 🔧 Estado del Proyecto

### ✅ Estado Actual: COMPLETO Y FUNCIONAL

El proyecto está **100% implementado y funcional**. Todos los componentes principales están desarrollados y listos para ejecutarse.

#### Componentes Implementados:
- ✅ **Estructura de directorios:** Completa y correcta
- ✅ **Código Java:** Completamente implementado y funcional
- ✅ **Configuración Maven:** Correcta con dependencias JavaFX
- ✅ **Algoritmo de Dijkstra:** Implementado en Grafo.java
- ✅ **Interfaz JavaFX:** Completamente implementada
- ✅ **Simulación:** Funcional con ambulancia, semáforos y animación

**Para ver el estado detallado, consulta:** `REQUISITOS_PROYECTO.md`  
**Para ejecutar en IntelliJ, consulta:** `GUIA_INTELLIJ.md`

---

## 🚀 Cómo Ejecutar el Proyecto

### 💻 Instalación Local (Recomendado)

**Requisitos Previos:**
- **Java JDK 17 o superior**
- **IntelliJ IDEA** (recomendado) o cualquier IDE Java
- **Maven** (incluido en IntelliJ)

### 📖 Guía Completa de Ejecución

Para una guía detallada paso a paso de cómo ejecutar el proyecto en IntelliJ IDEA, consulta:

👉 **[GUIA_INTELLIJ.md](GUIA_INTELLIJ.md)**

### Resumen Rápido:

1. **Abrir proyecto en IntelliJ IDEA**
2. **Configurar SDK de Java 17+** en Project Structure
3. **Sincronizar Maven** (descargar dependencias)
4. **Ejecutar** `Main.java`

**Nota:** El proyecto usa Maven, por lo que JavaFX se descarga automáticamente. No necesitas instalar JavaFX manualmente.

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

