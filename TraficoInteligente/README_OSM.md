# Integración OpenStreetMap - Sistema de Tráfico Inteligente

## 🗺️ Descripción

Este proyecto ahora incluye integración con OpenStreetMap (OSM) para mostrar un mapa real del microcentro de Asunción, Paraguay, como fondo de la simulación.

## 📋 Características Implementadas

### ✅ Clases Nuevas

1. **MapaOSM.java** - Maneja el mapa OSM y conversión de coordenadas
   - Conversión entre coordenadas geográficas (lat/lon) y píxeles
   - Cálculo de distancias reales usando fórmula de Haversine
   - Carga de imagen PNG del mapa

2. **Nodo.java (Actualizado)** - Soporta coordenadas reales
   - Coordenadas geográficas (latitud, longitud)
   - Coordenadas en píxeles (calculadas automáticamente)
   - Método `calcularPosicionPixel()` para conversión

### ✅ Actualizaciones

1. **Simulador.java** - Usa coordenadas reales del microcentro de Asunción
   - Nodos con coordenadas geográficas reales
   - Distancias calculadas con fórmula de Haversine
   - Grafo basado en calles reales del microcentro

2. **MapaController.java** - Dibuja mapa OSM de fondo
   - Carga y muestra imagen PNG del mapa
   - Superpone grafo y elementos de simulación
   - Atribución OSM incluida en la interfaz

3. **pom.xml** - Dependencias actualizadas
   - `javafx-graphics` - Para manejo de imágenes
   - `gson` - Para lectura de JSON (futuro)
   - `commons-math3` - Para cálculos geográficos

## 🚀 Cómo Usar

### 1. Obtener el Mapa OSM

El mapa PNG debe estar en: `src/main/resources/images/mapa_asuncion.png`

**Opciones para obtenerlo:**

#### Opción A: Static Maps API (Más fácil)
```
https://staticmap.openstreetmap.de/staticmap.php?
  center=-25.2819,-57.6351&
  zoom=16&
  size=1200x800&
  maptype=mapnik
```

Descargar y guardar como `mapa_asuncion.png`

#### Opción B: OpenStreetMap.org
1. Ir a https://www.openstreetmap.org/
2. Buscar "Asunción, Paraguay"
3. Navegar al microcentro
4. Hacer captura de pantalla o exportar
5. Guardar como `mapa_asuncion.png`

#### Opción C: QGIS
1. Instalar QGIS
2. Agregar capa OSM
3. Exportar área como PNG

**Nota:** Si no tienes el mapa, la aplicación funcionará igual pero mostrará un fondo sólido.

### 2. Compilar y Ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn javafx:run
```

O usar los scripts:
- Windows: `run.bat`
- Linux/Mac: `run.sh`

## 📍 Coordenadas del Microcentro

El sistema está configurado para el área del microcentro de Asunción:

- **Norte:** -25.2750
- **Sur:** -25.2850
- **Oeste:** -57.6400
- **Este:** -57.6300

### Intersecciones Incluidas

- **n0:** Base (Plaza de los Héroes) - -25.2821, -57.6351
- **n1:** Palma y Palacio - -25.2818, -57.6365
- **n2:** Palma y Alberdi - -25.2825, -57.6338
- **n3:** Chile y Independencia - -25.2830, -57.6355
- **n4:** Palma y Estrella - -25.2815, -57.6375
- **n5:** Alberdi y Estrella - -25.2828, -57.6378
- **n6:** Accidente - -25.2835, -57.6340
- **n7:** Hospital - -25.2805, -57.6320
- **n8:** Palma y 14 de Mayo - -25.2810, -57.6385
- **n9:** Alberdi y 14 de Mayo - -25.2823, -57.6388
- **n10:** Chile y Mariscal López - -25.2832, -57.6360

## 🎨 Visualización

La interfaz muestra:

1. **Mapa OSM de fondo** (si está disponible)
2. **Grafo superpuesto** - Calles y conexiones
3. **Semáforos** - Círculos de colores en intersecciones
4. **Ruta de la ambulancia** - Línea verde
5. **Ambulancia** - Rectángulo rojo con emoji 🚑
6. **Marcadores especiales** - Accidente y Hospital

## 📊 Datos JSON

Las coordenadas también están disponibles en:
`src/main/resources/data/intersecciones.json`

Este archivo puede usarse para:
- Cargar datos dinámicamente
- Agregar más intersecciones
- Exportar/importar configuraciones

## ⚖️ Atribución OSM

**IMPORTANTE:** Si usas mapas de OpenStreetMap, debes incluir la atribución:

```
© OpenStreetMap contributors
```

Esto ya está incluido en la interfaz de la aplicación (panel lateral derecho).

## 🔧 Personalización

### Agregar más nodos

Edita `Simulador.java` en el método `crearNodosMicrocentro()`:

```java
Nodo nuevoNodo = new Nodo("n11", "Nueva Intersección", -25.2820, -57.6340);
grafo.agregarNodo(nuevoNodo);
```

### Cambiar área del mapa

Edita `Simulador.java` en el método `crearMapaCiudad()`:

```java
mapa = new MapaOSM(
    "images/mapa_asuncion.png",
    nuevaLatNorte,  // Cambiar límites
    nuevaLatSur,
    nuevaLonOeste,
    nuevaLonEste
);
```

### Usar otro mapa

1. Coloca tu imagen PNG en `src/main/resources/images/`
2. Actualiza la ruta en `Simulador.java`
3. Ajusta las coordenadas de límites según tu mapa

## 🐛 Solución de Problemas

### El mapa no se muestra

- Verifica que `mapa_asuncion.png` esté en `src/main/resources/images/`
- Verifica que el archivo sea un PNG válido
- Revisa la consola para errores de carga

### Coordenadas incorrectas

- Verifica que los límites del mapa coincidan con la imagen
- Usa herramientas como Google Maps para verificar coordenadas
- Ajusta los valores en `MapaOSM` constructor

### Rendimiento lento

- Reduce el tamaño de la imagen PNG
- Limita el número de nodos en el grafo
- Optimiza la frecuencia de actualización del canvas

## 📚 Referencias

- [OpenStreetMap](https://www.openstreetmap.org/)
- [OSM Static Maps](https://staticmap.openstreetmap.de/)
- [Fórmula de Haversine](https://en.wikipedia.org/wiki/Haversine_formula)

## 📝 Notas

- El sistema funciona sin el mapa PNG, pero es más visual con él
- Las coordenadas son aproximadas y pueden necesitar ajuste fino
- El sistema está diseñado para ser extensible a otras ciudades

