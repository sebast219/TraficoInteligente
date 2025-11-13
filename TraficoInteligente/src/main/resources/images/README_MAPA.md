# Instrucciones para obtener el mapa OSM de Asunción

## Opción 1: Exportar desde OpenStreetMap (Recomendado)

1. Ir a: https://www.openstreetmap.org/
2. Buscar: "Asunción, Paraguay"
3. Navegar al microcentro (área alrededor de Plaza de los Héroes)
4. Ajustar el zoom para cubrir aproximadamente:
   - Norte: -25.2750
   - Sur: -25.2850
   - Oeste: -57.6400
   - Este: -57.6300
5. Hacer captura de pantalla o usar herramienta de exportación
6. Guardar como: `mapa_asuncion.png` en esta carpeta

## Opción 2: Usar Static Maps API

Puedes usar el servicio de mapas estáticos de OSM:

```
https://staticmap.openstreetmap.de/staticmap.php?
  center=-25.2819,-57.6351&
  zoom=16&
  size=1200x800&
  maptype=mapnik
```

Descargar la imagen y guardarla como `mapa_asuncion.png`

## Opción 3: Usar QGIS

1. Instalar QGIS (software GIS gratuito)
2. Agregar capa OSM
3. Exportar el área del microcentro como PNG
4. Guardar como `mapa_asuncion.png`

## Nota importante

Si no tienes el mapa PNG, la aplicación funcionará igual pero mostrará un fondo sólido en lugar del mapa real. El grafo y la simulación funcionarán normalmente.

## Atribución

Si usas un mapa de OpenStreetMap, debes incluir la atribución:
"© OpenStreetMap contributors"

Esto ya está incluido en la interfaz de la aplicación.

