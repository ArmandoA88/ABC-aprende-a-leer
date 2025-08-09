# Instrucciones para Compilar el APK de ABC Aprende a Leer

## Prerrequisitos

### 1. Instalar Android Studio
- Descargar e instalar Android Studio desde: https://developer.android.com/studio
- Durante la instalación, asegúrate de incluir:
  - Android SDK
  - Android SDK Platform-Tools
  - Android SDK Build-Tools
  - Android Emulator (opcional)

### 2. Configurar Java/Kotlin
- Android Studio incluye el JDK necesario
- Kotlin viene integrado con Android Studio

## Pasos para Compilar

### 1. Abrir el Proyecto
1. Abrir Android Studio
2. Seleccionar "Open an Existing Project"
3. Navegar a la carpeta "ABC aprende a leer"
4. Seleccionar la carpeta del proyecto y hacer clic en "OK"

### 2. Sincronizar el Proyecto
1. Android Studio detectará automáticamente que es un proyecto Gradle
2. Hacer clic en "Sync Now" cuando aparezca la notificación
3. Esperar a que se descarguen todas las dependencias (puede tomar varios minutos la primera vez)

### 3. Configurar SDK
1. Ir a File → Project Structure
2. En la sección "Project", verificar que:
   - Compile SDK Version: 34 (Android 14)
   - Build Tools Version: 34.0.0 o superior
3. En la sección "Modules" → "app":
   - Compile SDK Version: 34
   - Min SDK Version: 21 (Android 5.0)
   - Target SDK Version: 34

### 4. Compilar el APK

#### Opción A: APK de Debug (Recomendado para pruebas)
1. En Android Studio, ir al menú "Build"
2. Seleccionar "Build Bundle(s) / APK(s)"
3. Hacer clic en "Build APK(s)"
4. Esperar a que termine la compilación
5. El APK se generará en: `app/build/outputs/apk/debug/app-debug.apk`

#### Opción B: APK de Release (Para distribución)
1. Primero necesitas crear un keystore (archivo de firma):
   ```
   Build → Generate Signed Bundle / APK
   → APK → Create new...
   ```
2. Completar la información del keystore:
   - Key store path: Elegir ubicación y nombre
   - Password: Crear contraseña segura
   - Key alias: Nombre de la clave
   - Validity: 25 años (recomendado)
   - Certificate info: Completar datos
3. Una vez creado el keystore, seleccionar "release" como build variant
4. El APK firmado se generará en: `app/build/outputs/apk/release/app-release.apk`

### 5. Usando Línea de Comandos (Alternativo)

Si prefieres usar la terminal:

```bash
# Navegar al directorio del proyecto
cd "ABC aprende a leer"

# Para APK de debug
./gradlew assembleDebug

# Para APK de release (necesitas keystore configurado)
./gradlew assembleRelease
```

En Windows:
```cmd
gradlew.bat assembleDebug
```

## Instalación del APK

### En Dispositivo Android
1. Habilitar "Fuentes desconocidas" en Configuración → Seguridad
2. Transferir el APK al dispositivo
3. Abrir el archivo APK desde el explorador de archivos
4. Seguir las instrucciones de instalación

### En Emulador
1. Arrastrar y soltar el APK en la ventana del emulador
2. O usar ADB: `adb install app-debug.apk`

## Solución de Problemas Comunes

### Error: "SDK not found"
- Ir a File → Project Structure → SDK Location
- Verificar que la ruta del Android SDK sea correcta
- Típicamente: `C:\Users\[Usuario]\AppData\Local\Android\Sdk`

### Error: "Gradle sync failed"
- Verificar conexión a internet
- Limpiar proyecto: Build → Clean Project
- Invalidar caché: File → Invalidate Caches and Restart

### Error: "Compilation failed"
- Revisar la ventana "Build" para errores específicos
- Verificar que todas las dependencias estén disponibles
- Asegurar que el SDK target esté instalado

### Error de memoria durante compilación
- Aumentar memoria en `gradle.properties`:
  ```
  org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
  ```

## Optimizaciones para Release

### Reducir tamaño del APK
1. Habilitar ProGuard/R8 en `app/build.gradle`:
   ```gradle
   buildTypes {
       release {
           minifyEnabled true
           proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
       }
   }
   ```

2. Habilitar compresión de recursos:
   ```gradle
   android {
       buildTypes {
           release {
               shrinkResources true
               minifyEnabled true
           }
       }
   }
   ```

### Generar APKs por arquitectura
```gradle
android {
    splits {
        abi {
            enable true
            reset()
            include 'arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64'
            universalApk false
        }
    }
}
```

## Verificación del APK

### Información del APK
```bash
# Ver información del APK
aapt dump badging app-debug.apk

# Ver permisos
aapt dump permissions app-debug.apk

# Ver tamaño
ls -lh app-debug.apk
```

### Probar en diferentes dispositivos
- Probar en dispositivos con diferentes versiones de Android
- Verificar en tablets y teléfonos
- Probar con diferentes idiomas del sistema

## Distribución

### Google Play Store
1. Crear cuenta de desarrollador en Google Play Console
2. Subir APK firmado o Android App Bundle (AAB)
3. Completar información de la aplicación
4. Configurar clasificación de contenido
5. Publicar

### Distribución directa
- Compartir el APK directamente
- Usar plataformas como APKPure, F-Droid
- Distribución empresarial interna

## Notas Importantes

- **Siempre probar el APK** antes de distribuir
- **Mantener seguro el keystore** para actualizaciones futuras
- **Versionar correctamente** la aplicación en `build.gradle`
- **Documentar cambios** en cada versión
- **Probar permisos** especialmente el micrófono para reconocimiento de voz

## Contacto

Si encuentras problemas durante la compilación, revisa:
1. Los logs de Android Studio
2. La documentación oficial de Android
3. Stack Overflow para errores específicos

¡El APK resultante será una aplicación educativa completa para enseñar el alfabeto español a niños de 2-6 años!
