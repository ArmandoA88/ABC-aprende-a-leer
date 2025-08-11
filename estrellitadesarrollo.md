# Mapa de Desarrollo - App Android "Estrellita"

Este documento define el flujo y la estructura de la adaptación del método **Estrellita** para una aplicación Android orientada a niños pequeños que están aprendiendo a leer.

---

## **1. Principios Clave del Método**
- **Secuencia gradual**: Orden pedagógico de introducción de letras y sílabas.
- **Enfoque fonético**: Enseñar el sonido antes que el nombre de la letra.
- **Asociación multisensorial**: Vista, oído, tacto, interacción.
- **Repetición y práctica breve**: Sesiones cortas, progreso constante.
- **Aprendizaje lúdico**: Juegos, canciones, cuentos, actividades dinámicas.

---

## **2. Fases de Aprendizaje**
1. **Conciencia fonológica**
   - Juegos de identificación de sonidos iniciales.
   - Rimas y asociación palabra-sonido.

2. **Presentación de letras**
   - Imagen clave y animación de trazo.
   - Audio con pronunciación fonética.
   - Interacción con micrófono para repetir.

3. **Formación de sílabas**
   - Combinación interactiva de letras.
   - Escuchar y repetir sílabas formadas.

4. **Lectura de palabras**
   - Rompecabezas de sílabas.
   - Audio y animaciones para significado.

5. **Lectura guiada**
   - Cuentos con texto resaltado y narración.
   - Repetición de palabras al tocarlas.

6. **Refuerzo y fluidez**
   - Listas cronometradas de sílabas/palabras.
   - Sistema de recompensas (estrellas, stickers).

---

## **3. Módulos de la App**

### **Módulo 1: Sonidos y conciencia fonológica**
- Mini-juegos de identificación auditiva.
- Selección de imágenes que comiencen con un sonido.

### **Módulo 2: Presentación de letras**
- Pantalla por letra con:
  - Imagen clave animada.
  - Animación de trazo.
  - Audio fonético.
  - Reconocimiento de voz para evaluar pronunciación.

### **Módulo 3: Sílabas**
- Arrastrar y combinar letras aprendidas.
- Reproducción automática del sonido de la sílaba.

### **Módulo 4: Palabras**
- Armado de palabras usando sílabas.
- Animación ilustrativa del significado.

### **Módulo 5: Lectura guiada**
- Texto resaltado al ritmo del narrador.
- Toque para volver a reproducir palabras.

### **Módulo 6: Refuerzo y fluidez**
- Retos cronometrados.
- Premios virtuales.
- Control de progreso.

---

## **4. Funciones Clave para Niños Pequeños**
- Interfaz **simple, colorida y con botones grandes**.
- Navegación basada en imágenes.
- Progresión adaptativa.
- Retroalimentación positiva inmediata.
- Control parental de progreso.

---

## **5. Ejemplo de Flujo Inicial**
1. Pantalla de bienvenida.
2. Selección de letra del día (ej. **M**).
3. Animación e introducción de sonido /m/.
4. Juego: identificar imágenes que empiezan con /m/.
5. Combinar con "a" → formar “ma”.
6. Mini cuento: “Mi mamá me mima”.
7. Ganar 3 estrellas → desbloquear siguiente letra.

---

## **6. Tecnologías a Utilizar**
- **Android Studio** con **Kotlin/Java**.
- **Text-to-Speech (TTS)** para lectura de letras/palabras.
- **Speech Recognition API** para evaluar pronunciación.
- **SQLite / Room** para registrar progreso.
- **Lottie Animations** para animaciones atractivas.

---

## **7. Estructura Técnica Sugerida**
- **UI Layers**: Jetpack Compose o XML para pantallas interactivas.
- **ViewModels**: Lógica de aprendizaje y control de progreso.
- **Services**:
  - `TTSService` para audio.
  - `VoiceRecognitionService` para pronunciación.
- **Database**: Room + repositorios para progreso.
- **Assets**: Imágenes, sonidos, animaciones.

---

## **8. Roadmap de Implementación**
✅ **Implementado:**  
- Módulo 1 (versión avanzada) — Pantalla `PhonologicalAwarenessScreen` ahora incluye varias rondas con diferentes sonidos (`m`, `s`, `c`), progresión automática entre rondas y retroalimentación hablada y visual. Uso de `TTSService` para instrucciones y refuerzo positivo. Probado en `assembleDebug` sin errores.

⏳ **Pendiente:**  
1. Mejorar Módulo 1 con más sonidos e imágenes adicionales (base implementada con 3 rondas, pendiente incrementar banco de recursos).
2. Placeholder para animaciones integrado en Módulo 1; falta agregar dependencia y recurso Lottie (`confetti.json`) para activarlas.
3. Implementar **Módulo 2: Presentación de letras**.
4. Añadir **Módulo 3 (sílabas)** con arrastrar y soltar.
5. Desarrollar **Módulos 4 y 5** con lecturas guiadas.
6. **Módulo 6** creado como pantalla independiente `Nivel6Screen` que carga `PhonologicalAwarenessScreen` con la letra actual para refuerzo fonológico tras completar una letra en Estrellita.
7. Integrar sistema de recompensas y progresión en Módulo 6.
8. Integrar **voice recognition**.
9. Optimizar experiencia y test con niños.
10. Integrar control parental y estadísticas.
11. Publicación en Play Store.

---

## **9. Notas Finales**
Este diseño mantiene la esencia pedagógica original del método Estrellita, mientras aprovecha la interactividad y tecnologías de Android para mejorar la motivación y la personalización del avance.
