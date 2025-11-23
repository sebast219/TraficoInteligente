# 🚀 Guía Completa: Ejecutar el Proyecto en IntelliJ IDEA

Esta guía te ayudará a configurar y ejecutar el **Sistema de Tráfico Inteligente** en IntelliJ IDEA paso a paso.

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

### 1. **Java JDK 17 o superior**
   - **Verificar instalación:**
     ```bash
     java -version
     ```
   - **Si no lo tienes:** Descarga desde [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://adoptium.net/)

### 2. **IntelliJ IDEA** (Community o Ultimate)
   - **Descargar:** [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)
   - **Versión mínima:** 2021.1 o superior

### 3. **Maven** (generalmente incluido en IntelliJ)
   - IntelliJ incluye Maven embebido, pero puedes verificar:
     ```bash
     mvn -version
     ```

---

## 🔧 Configuración del Proyecto en IntelliJ

### ⚠️ IMPORTANTE: Si Maven no se detecta

**Si IntelliJ no detecta Maven automáticamente, consulta primero:**
👉 **[SOLUCION_MAVEN.md](SOLUCION_MAVEN.md)** - Guía completa para resolver problemas de Maven

### Paso 1: Abrir el Proyecto

**Opción A: Abrir como Proyecto Maven (RECOMENDADO si Maven no se detecta)**

1. **Abrir IntelliJ IDEA**
2. **Seleccionar:** `File → Open`
3. **Navegar** a la carpeta del proyecto:
   ```
   C:\Users\sebay\OneDrive\Documentos\TraficoInteligente
   ```
4. **IMPORTANTE:** Seleccionar el archivo **`pom.xml`** (no la carpeta)
5. Hacer clic en **Open**
6. IntelliJ preguntará: **"Open as Project"** → Seleccionar **"Trust Project"**

**Opción B: Abrir carpeta (si Maven ya está detectado)**

1. **Abrir IntelliJ IDEA**
2. **Seleccionar:** `File → Open`
3. **Navegar** a la carpeta del proyecto
4. **Seleccionar** la carpeta `TraficoInteligente` y hacer clic en **OK**
5. IntelliJ debería detectar automáticamente que es un proyecto Maven

### Paso 2: Configurar el SDK de Java

1. **Ir a:** `File → Project Structure` (o presionar `Ctrl+Alt+Shift+S`)
2. **En la pestaña "Project":**
   - **Project SDK:** Seleccionar Java 17 o superior
   - **Project language level:** 17 o superior
3. **Hacer clic en "OK"**

### Paso 3: Sincronizar Maven

1. **Abrir el panel Maven:**
   - En la barra lateral derecha, buscar el ícono **Maven** (o `View → Tool Windows → Maven`)
2. **Sincronizar dependencias:**
   - Hacer clic en el ícono **Reload All Maven Projects** (🔄)
   - O hacer clic derecho en `pom.xml` → `Maven → Reload Project`
3. **Esperar** a que Maven descargue todas las dependencias (JavaFX, Gson, etc.)

### Paso 4: Configurar la Estructura de Carpetas

1. **Ir a:** `File → Project Structure` → `Modules`
2. **Seleccionar** el módulo `trafico-inteligente`
3. **En la pestaña "Sources":**
   - Marcar como **Sources:**
     - `TraficoInteligente/src`
   - Marcar como **Resources:**
     - `TraficoInteligente/src/main/resources`
4. **Hacer clic en "OK"**

---

## ⚙️ Configurar la Ejecución

### Paso 5: Crear Configuración de Ejecución

1. **Ir a:** `Run → Edit Configurations...` (o hacer clic en la lista desplegable junto al botón Run)
2. **Hacer clic en el botón "+"** (arriba a la izquierda)
3. **Seleccionar:** `Application`
4. **Configurar:**
   - **Name:** `TraficoInteligente`
   - **Main class:** `com.trafico.Main`
   - **VM options:** (dejar vacío por ahora, JavaFX se carga automáticamente con Maven)
   - **Working directory:** `$PROJECT_DIR$/TraficoInteligente`
5. **Hacer clic en "OK"**

### Paso 6: Verificar Dependencias JavaFX

El proyecto usa **JavaFX 17** a través de Maven. IntelliJ debería detectarlo automáticamente, pero si hay problemas:

1. **Verificar en `pom.xml`** que las dependencias JavaFX estén presentes:
   ```xml
   <dependency>
       <groupId>org.openjfx</groupId>
       <artifactId>javafx-controls</artifactId>
       <version>17.0.2</version>
   </dependency>
   ```

2. **Si IntelliJ no detecta JavaFX:**
   - Ir a `File → Project Structure → Libraries`
   - Verificar que las librerías JavaFX estén presentes
   - Si no, hacer clic en "+" → `From Maven` → buscar `javafx-controls`

---

## 🚀 Ejecutar el Proyecto

### Opción A: Ejecutar desde IntelliJ (Recomendado)

1. **Abrir** el archivo `Main.java`:
   ```
   TraficoInteligente/src/com/trafico/Main.java
   ```

2. **Hacer clic derecho** en el archivo → `Run 'Main.main()'`
   - O presionar `Shift+F10`
   - O hacer clic en el botón verde ▶️ junto a la clase `Main`

3. **Esperar** a que la aplicación se inicie

### Opción B: Ejecutar desde la Configuración

1. **Seleccionar** la configuración `TraficoInteligente` en la lista desplegable (arriba)
2. **Hacer clic** en el botón verde ▶️ **Run**

### Opción C: Ejecutar con Maven

1. **Abrir terminal** en IntelliJ: `Alt+F12`
2. **Ejecutar:**
   ```bash
   mvn clean javafx:run
   ```

---

## ✅ Verificar que Todo Funciona

### 1. **Interfaz Gráfica**
   - Deberías ver una ventana con:
     - Panel superior con botones: 🚑 Iniciar Emergencia, ⏸ Pausar, 🔄 Reiniciar
     - Panel central con el mapa (o fondo sólido si no hay imagen)
     - Panel lateral derecho con información de estado

### 2. **Elementos Visuales**
   - ✅ **Nodos (intersecciones):** Círculos grises con semáforos
   - ✅ **Semáforos:** Círculos de color (🟢 Verde, 🟡 Amarillo, 🔴 Rojo)
   - ✅ **Aristas (calles):** Líneas azules conectando nodos
   - ✅ **Ambulancia:** Cuadrado rojo con emoji 🚑
   - ✅ **Accidente:** Texto "📍 ACCIDENTE" en el nodo n6
   - ✅ **Hospital:** Texto "🏥 HOSPITAL" en el nodo n7

### 3. **Funcionalidad**
   - ✅ **Botón "Iniciar Emergencia":**
     - La ambulancia debe comenzar a moverse desde la base (n0) hacia el accidente (n6)
     - Los semáforos en la ruta deben cambiar a verde cuando la ambulancia se acerca
     - Debe aparecer una línea verde mostrando la ruta calculada por Dijkstra
   
   - ✅ **Botón "Pausar":**
     - La simulación debe detenerse
     - El estado debe cambiar a "Pausado"
   
   - ✅ **Botón "Reiniciar":**
     - La ambulancia debe volver a la base
     - Los contadores deben resetearse
     - Los semáforos deben volver a su ciclo normal

### 4. **Panel de Información**
   - ✅ **Estado:** Debe cambiar según la simulación
   - ✅ **Distancia:** Debe aumentar mientras la ambulancia se mueve
   - ✅ **Tiempo:** Debe aumentar durante la simulación

---

## 🐛 Solución de Problemas Comunes

### Error: "Maven no se detecta" o "Maven Projects está vacío"

**Solución:**
👉 **Consulta la guía completa:** `SOLUCION_MAVEN.md`

**Solución rápida:**
1. Cerrar el proyecto: `File → Close Project`
2. `File → Open` → Seleccionar **SOLO el archivo `pom.xml`**
3. Seleccionar **"Open as Project"**

### Error: "JavaFX runtime components are missing"

**Solución:**
1. Verificar que Maven haya descargado las dependencias:
   - Panel Maven → Verificar que las dependencias no estén en rojo
   - Si están en rojo: Clic derecho en proyecto → **Maven → Reload Project**

2. Si el problema persiste, en la configuración de ejecución, agregar en **VM options:**
   ```
   --module-path %USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\17.0.2 --add-modules javafx.controls,javafx.fxml
   ```
   (Ajustar la ruta según tu instalación de Maven)

### Error: "Cannot find symbol: class Application"

**Solución:**
1. Verificar que JavaFX esté en el classpath
2. Sincronizar Maven nuevamente: `Maven → Reload Project`
3. Limpiar y reconstruir: `Build → Rebuild Project`

### La ventana no aparece o se cierra inmediatamente

**Solución:**
1. Verificar que la clase `Main` extienda `Application`
2. Verificar que el método `main` llame a `launch(args)`
3. Revisar la consola de IntelliJ para errores

### El mapa no se muestra (fondo sólido)

**Esto es normal** si no tienes el archivo `mapa_asuncion.png`. El proyecto funciona sin él.

**Para agregar el mapa:**
1. Descargar un mapa de OpenStreetMap del área de Asunción
2. Guardarlo como `mapa_asuncion.png` en:
   ```
   TraficoInteligente/src/main/resources/images/
   ```
3. Reiniciar la aplicación

### La ambulancia no se mueve

**Solución:**
1. Verificar que hayas presionado el botón "🚑 Iniciar Emergencia"
2. Verificar que exista una ruta entre n0 (base) y n6 (accidente)
3. Revisar la consola para errores de NullPointerException

### Los semáforos no cambian

**Solución:**
1. Verificar que el método `actualizar()` del `Simulador` se esté llamando
2. Verificar que la animación esté activa (no pausada)

---

## 📁 Estructura del Proyecto

```
TraficoInteligente/
├── pom.xml                          # Configuración Maven
├── TraficoInteligente/
│   └── src/
│       ├── com/trafico/
│       │   ├── Main.java            # Punto de entrada
│       │   ├── controller/
│       │   │   └── MapaController.java
│       │   ├── model/
│       │   │   ├── Ambulancia.java
│       │   │   ├── Arista.java
│       │   │   ├── Grafo.java
│       │   │   ├── MapaOSM.java
│       │   │   ├── Nodo.java
│       │   │   ├── Semaforo.java
│       │   │   └── Vehiculo.java
│       │   └── util/
│       │       └── Simulador.java
│       └── main/resources/
│           ├── data/
│           │   └── intersecciones.json
│           └── images/
│               └── README_MAPA.md
```

---

## 🎯 Características del Proyecto

### ✅ Implementado y Funcional:

1. **Estructura de Datos:**
   - ✅ Grafo con nodos y aristas
   - ✅ Algoritmo de Dijkstra para rutas más cortas
   - ✅ Cola de prioridad para Dijkstra

2. **Simulación:**
   - ✅ Ambulancia que se mueve por la ruta
   - ✅ Semáforos con estados (Verde, Amarillo, Rojo)
   - ✅ Prioridad semafórica cuando se acerca la ambulancia
   - ✅ Cálculo de distancias reales con fórmula de Haversine

3. **Interfaz Gráfica:**
   - ✅ Canvas JavaFX para dibujar el mapa
   - ✅ Animación en tiempo real
   - ✅ Controles (Iniciar, Pausar, Reiniciar)
   - ✅ Panel de información en tiempo real

4. **Modelo:**
   - ✅ Programación Orientada a Objetos (POO)
   - ✅ Herencia (Vehiculo → Ambulancia)
   - ✅ Composición (Nodo contiene Semaforo)

---

## 📝 Notas Importantes

1. **Mapa OSM:** El proyecto funciona sin el mapa PNG. Si no está presente, se muestra un fondo sólido.

2. **Coordenadas:** El proyecto usa coordenadas reales del microcentro de Asunción, Paraguay.

3. **Rendimiento:** La animación corre a ~60 FPS. Si notas lag, reduce el número de nodos o optimiza el canvas.

4. **JavaFX:** Este proyecto usa JavaFX 17, que requiere Java 11 o superior.

---

## 🎓 Uso Académico

Este proyecto es ideal para demostrar:
- ✅ Implementación de grafos
- ✅ Algoritmo de Dijkstra
- ✅ Programación Orientada a Objetos
- ✅ Interfaces gráficas con JavaFX
- ✅ Estructuras de datos avanzadas

---

## 📞 Soporte Adicional

Si encuentras problemas:

1. **Revisar la consola de IntelliJ** para mensajes de error
2. **Verificar que todas las dependencias** estén descargadas (panel Maven)
3. **Limpiar y reconstruir:** `Build → Rebuild Project`
4. **Invalidar caché:** `File → Invalidate Caches / Restart`

---

## ✅ Checklist Final

Antes de ejecutar, verifica:

- [ ] Java JDK 17+ instalado
- [ ] IntelliJ IDEA instalado
- [ ] Proyecto abierto en IntelliJ
- [ ] SDK de Java configurado (Project Structure)
- [ ] Maven sincronizado (dependencias descargadas)
- [ ] Estructura de carpetas correcta (Sources/Resources)
- [ ] Configuración de ejecución creada
- [ ] Sin errores en el código (verificar panel de problemas)

---

¡Listo! Ahora deberías poder ejecutar el proyecto sin problemas. 🚀

Si todo funciona correctamente, verás la simulación de la ambulancia moviéndose por la ciudad con semáforos inteligentes. 🚑🚦

