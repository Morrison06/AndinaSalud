# AndinaSalud — Examen Parcial U1

Aplicación Kotlin Multiplatform basada en la arquitectura Clean + MVVM trabajada en PharmaMobil. Implementa el caso AndinaSalud con datos simulados en memoria.

## Arquitectura

- `domain/`: modelos, interfaz `CitaRepository`, reglas y casos de uso.
- `data/`: fuente simulada dinámica y `CitaRepositoryFake`.
- `presentation/`: Inicio, Citas, Detalle, Solicitud, Perfil, navegación y tema.
- `di/`: Koin en `commonMain` y módulos por plataforma.

La regla de dependencia apunta hacia dominio. Cambiar la fuente simulada por una API futura implica crear otra implementación de `CitaRepository` y cambiar el binding de Koin; la UI y los casos de uso no necesitan reescribirse.

## Funcionalidades

- Inicio con saludo, próxima cita y accesos rápidos.
- Lista ordenada con filtros y búsqueda sin mayúsculas/tildes.
- Detalle y cancelación con confirmación y regla de 24 horas.
- Solicitud de cita con validación por campo y reglas RN-01 a RN-05.
- Perfil y tema claro/oscuro.
- Bottom navigation Inicio/Citas/Perfil.
- Estados de carga, contenido, vacío y error implementados.
- Datos semilla con fechas dinámicas relativas al día de ejecución.

## Ejecutar Android

```bash
./gradlew :androidApp:assembleDebug
```

Luego seleccionar un dispositivo físico/emulador y ejecutar `androidApp`.

## Verificar iOS (requiere macOS/Xcode)

```bash
./gradlew :shared:compileKotlinIosSimulatorArm64
```

El proyecto `iosApp/` inicia Koin y muestra la UI compartida.

## Git recomendado para el examen

1. Crear `main` y `develop`.
2. Trabajar en `feature/<funcionalidad>-<apellido>`.
3. La solicitud individual de la Parte II debe ir en `sc-<letra>-<apellido>` creada desde `develop`.
4. En la Parte II hacer al menos 3 commits separados.
5. Al cierre, fusionar `develop` en `main` y crear la etiqueta `v1.0-unidad1`.

> No se incluyen Ktor, Retrofit, Room, SQLDelight ni otra dependencia de red/persistencia.
