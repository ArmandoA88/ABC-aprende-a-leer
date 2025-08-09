# ABC Aprende a Leer - Aplicación Android Educativa

Una aplicación Android nativa desarrollada con Kotlin y Jetpack Compose para enseñar el alfabeto español a niños de 2-6 años de manera interactiva y divertida.

## 🎯 Características Principales

### 🔤 Letras Vivas
- **Animaciones grandes por letra**: Cada letra tiene su propia animación temática (A como avión, B como barco)
- **Palabras ancla**: Cada letra tiene 2-3 palabras asociadas para mayor variedad
- **Sonidos asociados**: Efectos de sonido temáticos para cada letra
- **Emojis visuales**: Representación visual inmediata de cada concepto

### 🗣️ Sistema de Voz Bidireccional
- **Text-to-Speech (TTS)**: Pronunciación clara en español (es-ES/es-MX)
- **Reconocimiento de voz offline**: Detecta y evalúa la pronunciación del niño
- **Feedback inteligente**: Sistema de puntuación con estrellas (verde/amarillo/rojo)
- **Pronunciación guiada**: Separación silábica con resaltado sincrónico

### ✏️ Trazado de Letras
- **Guía fantasma**: Contorno punteado para guiar el trazo
- **Detección de trazo**: Algoritmo tolerante que evalúa la forma y dirección
- **Efectos visuales**: Confeti y animaciones de celebración
- **Canvas interactivo**: Superficie de dibujo optimizada para dedos pequeños

### 🎮 Mini-Juegos Educativos
- **Caza-letras**: Tocar letras que caen desde arriba
- **Memorama**: Emparejar letras con palabras/sonidos
- **Rompecabezas**: Formar palabras sílaba por sílaba
- **Juego de pareo**: Conectar letras con imágenes

### 📊 Sistema de Progresión
- **Estrellas por actividad**: 1-3 estrellas según el desempeño
- **Coleccionables**: Stickers y cromos temáticos por letra
- **Niveles progresivos**:
  - **Nivel 1**: "Escucha y Repite" (Vocales)
  - **Nivel 2**: "Reconoce y Traza" (Consonantes)
  - **Nivel 3**: "Forma Palabras" (Sílabas)
- **Desbloqueo automático**: Niveles se abren al dominar letras anteriores

### 🧠 Aprendizaje Adaptativo
- **Dificultad dinámica**: Se ajusta según el desempeño del niño
- **Repetición espaciada**: Re-presenta letras débiles después de intervalos
- **Análisis de errores**: Identifica patrones para personalizar la experiencia

### 👨‍👩‍👧‍👦 Panel de Padres
- **Estadísticas detalladas**: Tiempo jugado, letras dominadas, precisión
- **Progreso por letra**: Visualización del avance individual
- **Objetivos semanales**: Metas personalizables
- **Exportación de datos**: Informes en formato CSV/JSON

## 🏗️ Arquitectura Técnica

### Stack Tecnológico
- **Lenguaje**: Kotlin 100%
- **UI Framework**: Jetpack Compose
- **Arquitectura**: MVVM + Clean Architecture
- **Base de datos**: Room (SQLite)
- **Inyección de dependencias**: Hilt
- **Navegación**: Navigation Compose
- **Animaciones**: Lottie + Compose Animations

### Componentes Principales

#### 📱 Presentación (UI)
```
presentation/
├── screens/           # Pantallas de la aplicación
├── viewmodels/        # ViewModels con StateFlow
├── components/        # Componentes reutilizables
└── theme/            # Tema, colores y tipografía
```

#### 💾 Datos
```
data/
├── model/            # Modelos de datos
├── database/         # Room DAOs y entidades
└── repository/       # Repositorios para acceso a datos
```

#### 🔧 Servicios
```
services/
├── TTSService        # Text-to-Speech
├── VoiceRecognitionService  # Reconocimiento de voz
└── AudioService      # Efectos de sonido
```

### Base de Datos
- **Entidades principales**:
  - `Letter`: Información de cada letra
  - `LetterProgress`: Progreso individual por letra
  - `UserProgress`: Progreso general del usuario

### Características Técnicas Avanzadas

#### 🎤 Reconocimiento de Voz
- **Algoritmo de similitud**: Levenshtein Distance para comparar pronunciación
- **Evaluación inteligente**: Combina confianza y similitud fonética
- **Soporte multiidioma**: es-ES, es-MX, es-AR, etc.

#### ✏️ Sistema de Trazado
- **Detección de gestos**: Canvas personalizado con eventos touch
- **Evaluación de trazo**: Análisis de puntos, dirección y forma
- **Guías adaptativas**: Diferentes estilos según la letra

#### 🎯 Gamificación
- **Sistema de recompensas**: Estrellas, logros y coleccionables
- **Progresión no lineal**: Múltiples caminos de aprendizaje
- **Feedback inmediato**: Celebraciones y animaciones

## 🚀 Instalación y Configuración

### Prerrequisitos
- Android Studio Arctic Fox o superior
- SDK mínimo: API 21 (Android 5.0)
- SDK objetivo: API 34 (Android 14)
- Kotlin 1.9.0+

### Configuración del Proyecto
1. Clonar el repositorio
2. Abrir en Android Studio
3. Sincronizar dependencias de Gradle
4. Ejecutar en dispositivo o emulador

### Permisos Requeridos
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 📋 Funcionalidades por Pantalla

### 🏠 Pantalla Principal
- Selector de niveles con indicadores de progreso
- Contador de estrellas totales
- Acceso al panel de padres
- Animaciones de bienvenida

### 🔤 Selección de Letras
- Grid de letras disponibles según el nivel
- Indicadores visuales de progreso (estrellas)
- Letras dominadas marcadas con color especial
- Sonido al hacer hover sobre cada letra

### 🎯 Actividad de Letra
- Animación grande de la letra seleccionada
- Controles de audio (normal, lento, repetir)
- Acceso a actividades: trazar, hablar, jugar
- Progreso visual con estrellas

### ✏️ Pantalla de Trazado
- Canvas interactivo para dibujar
- Guía fantasma opcional
- Botones: borrar, mostrar guía, revisar
- Evaluación automática del trazo

### 🎤 Pantalla de Voz
- Animación de micrófono con ondas sonoras
- Botón para iniciar grabación
- Feedback visual del resultado
- Opción para escuchar ejemplo

### 🎮 Mini-Juegos
- Juego de letras que caen
- Memorama de letras y palabras
- Juego de emparejar
- Puntuación y tiempo límite

### 👨‍👩‍👧‍👦 Panel de Padres
- Estadísticas generales (tiempo, precisión, letras dominadas)
- Progreso detallado por letra
- Opciones para reiniciar o exportar progreso
- Configuraciones de la aplicación

## 🎨 Diseño y UX

### Principios de Diseño
- **Simplicidad**: Interfaz limpia sin distracciones
- **Accesibilidad**: Botones grandes, colores contrastantes
- **Feedback inmediato**: Respuesta visual y auditiva instantánea
- **Consistencia**: Patrones de interacción uniformes

### Paleta de Colores
- **Primario**: Gradiente azul-púrpura (#667eea → #764ba2)
- **Secundario**: Gradiente naranja-rojo (#ff6b6b → #ffa500)
- **Éxito**: Verde (#4CAF50)
- **Advertencia**: Naranja (#FF9800)
- **Error**: Rojo (#F44336)

### Tipografía
- **Títulos**: Fredoka One (divertida y legible)
- **Cuerpo**: Nunito (clara y profesional)
- **Letras grandes**: Tamaños optimizados para visibilidad

## 📊 Métricas y Analytics

### KPIs Principales
- **Engagement**: Sesiones por día, duración promedio
- **Aprendizaje**: % de aciertos por letra, tiempo hasta dominar
- **Usabilidad**: Toques erróneos por sesión
- **Retención**: Usuarios activos a 7 días

### Datos Recopilados (Local)
- Progreso por letra y actividad
- Tiempo de sesión y frecuencia de uso
- Patrones de error para mejora adaptativa
- Preferencias de actividades

## 🔒 Privacidad y Seguridad

### Principios de Privacidad
- **Sin cuentas**: No requiere registro ni login
- **Datos locales**: Toda la información se almacena en el dispositivo
- **Sin publicidad**: Experiencia libre de anuncios
- **Audio local**: Procesamiento de voz completamente offline

### Cumplimiento COPPA
- Diseñado para cumplir con regulaciones de privacidad infantil
- Sin recopilación de datos personales
- Sin comunicación externa no autorizada

## 🚀 Roadmap de Desarrollo

### MVP (Versión 1.0)
- [x] Vocales completas con audio y animaciones
- [x] Sistema de trazado básico
- [x] TTS en español
- [x] Sistema de progreso con estrellas
- [x] Panel de padres básico

### Versión 1.1
- [ ] Consonantes frecuentes (B, C, D, F, G, H, J, L, M, N, P, R, S, T)
- [ ] Reconocimiento de voz offline
- [ ] Sistema adaptativo básico
- [ ] Más coleccionables y logros

### Versión 1.2
- [ ] Todos los mini-juegos implementados
- [ ] Más palabras por letra (3-4 opciones)
- [ ] Sistema de rimas y sonidos similares
- [ ] Modo silábico avanzado

### Versión 2.0
- [ ] Perfiles múltiples de niños
- [ ] Sincronización con Google Drive
- [ ] Packs de voces y temas
- [ ] Modo multijugador local

## 🤝 Contribución

### Cómo Contribuir
1. Fork del repositorio
2. Crear rama para nueva funcionalidad
3. Implementar cambios con tests
4. Crear Pull Request con descripción detallada

### Estándares de Código
- Seguir convenciones de Kotlin
- Documentar funciones públicas
- Incluir tests unitarios
- Usar arquitectura MVVM establecida

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 👥 Equipo

- **Desarrollo**: Implementación con Kotlin y Jetpack Compose
- **Diseño UX/UI**: Interfaz optimizada para niños
- **Contenido Educativo**: Metodología de aprendizaje del alfabeto
- **Testing**: Pruebas con niños reales de 2-6 años

## 📞 Contacto

Para preguntas, sugerencias o reportar bugs, por favor crear un issue en el repositorio.

---

**ABC Aprende a Leer** - Haciendo que aprender el alfabeto sea divertido y efectivo para los más pequeños 🎯📚
