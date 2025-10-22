# Aplicación Web de Gestión con Capacidad Offline y Georreferenciación

## Descripción

Este proyecto es una aplicación web para el registro y gestión de estudiantes, desarrollada como parte de un parcial de Programación Web. Permite a los usuarios autenticados gestionar estudiantes, registros con ubicación geográfica, y sincronización offline. Incluye roles de usuario (administrador y usuario regular) y una interfaz web moderna.

## Características

- **Autenticación y Autorización**: Sistema de login con roles (Administrador, Usuario).
- **Gestión de Estudiantes**: CRUD completo (Crear, Leer, Actualizar, Eliminar) de estudiantes con información como nombre, sector y nivel escolar.
- **Registros Geográficos**: Registro de estudiantes con coordenadas GPS (latitud y longitud).
- **Dashboard**: Panel de control con estadísticas y visualizaciones.
- **Sincronización Offline**: Soporte para sincronización de datos cuando no hay conexión a internet.
- **Interfaz Web**: Templates HTML con Thymeleaf y estilos con Tailwind CSS.
- **Base de Datos**: Soporte para H2 (desarrollo) y PostgreSQL (producción) usando Hibernate ORM.

## Tecnologías Utilizadas

### Backend
- **Java 11+**
- **Javalin**: Framework web ligero para Java.
- **Thymeleaf**: Motor de plantillas para renderizar vistas HTML.
- **Hibernate ORM**: Mapeo objeto-relacional para persistencia de datos.
- **H2 Database**: Base de datos embebida para desarrollo.
- **PostgreSQL**: Base de datos para producción.
- **Jackson**: Serialización/deserialización JSON.
- **SLF4J**: Logging.

### Frontend
- **HTML/CSS/JavaScript**: Estructura, estilos y funcionalidad del lado cliente.
- **Tailwind CSS**: Framework CSS para diseño responsivo.
- **JavaScript**: Lógica para mapas, sincronización offline y formularios.

### Herramientas de Desarrollo
- **Gradle**: Sistema de construcción y gestión de dependencias.
- **Shadow Plugin**: Para crear JARs ejecutables con todas las dependencias.
- **JUnit 5**: Pruebas unitarias.

## Estructura del Proyecto

```
parcial-2/
├── build.gradle                 # Configuración de Gradle
├── settings.gradle              # Nombre del proyecto
├── package.json                 # Dependencias para Tailwind CSS
├── gradlew                      # Wrapper de Gradle para Unix
├── gradlew.bat                  # Wrapper de Gradle para Windows
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/pucmm/eict/
│       │       ├── Main.java                    # Punto de entrada de la aplicación
│       │       ├── controladores/              # Controladores MVC
│       │       │   ├── AuthController.java     # Autenticación
│       │       │   ├── DashboardController.java # Dashboard
│       │       │   ├── EstudianteController.java # Gestión de estudiantes
│       │       │   ├── RegistrosController.java # Gestión de registros
│       │       │   └── UsuarioController.java   # Gestión de usuarios
│       │       ├── modelos/                    # Modelos de datos (Entidades JPA)
│       │       │   ├── Estudiante.java         # Modelo Estudiante
│       │       │   ├── NivelEscolar.java       # Enum Nivel Escolar
│       │       │   ├── Registro.java           # Modelo Registro
│       │       │   ├── Rol.java                # Enum Rol
│       │       │   ├── SyncMessage.java        # Modelo para sincronización
│       │       │   └── Usuario.java            # Modelo Usuario
│       │       └── services/                   # Servicios de negocio
│       │           ├── BootStrapServices.java  # Inicialización de la app
│       │           ├── DatabaseService.java    # Configuración de BD
│       │           ├── EstudianteService.java  # Lógica de estudiantes
│       │           ├── GestionDb.java          # Gestión de BD
│       │           ├── RegistroService.java    # Lógica de registros
│       │           └── UserService.java        # Lógica de usuarios
│       └── resources/
│           ├── META-INF/
│           │   └── persistence.xml             # Configuración JPA
│           └── public/                         # Recursos estáticos
│               ├── css/
│               │   ├── style.css               # Estilos personalizados
│               │   └── tailwind.css            # CSS generado por Tailwind
│               └── js/
│                   ├── dashboard.js            # JS para dashboard
│                   ├── formulario-estudiante.js # JS para formularios de estudiante
│                   ├── formulario-usuario.js   # JS para formularios de usuario
│                   ├── mapa.js                 # JS para mapas
│                   ├── offlineSync.js          # JS para sincronización offline
│                   └── syncWorker.js           # Worker para sincronización
│           └── templates/                      # Plantillas Thymeleaf
│               ├── crear-estudiante.html      # Formulario crear estudiante
│               ├── crear-usuario.html         # Formulario crear usuario
│               ├── dashboard.html             # Página dashboard
│               ├── estudiantes.html           # Lista de estudiantes
│               ├── login.html                 # Página de login
│               ├── register.html              # Página de registro
│               ├── registros.html             # Lista de registros
│               └── usuarios.html              # Gestión de usuarios
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties          # Propiedades del wrapper Gradle
└── README.md                                  # Este archivo
```

## Instalación y Configuración

### Prerrequisitos
- **Java 11 o superior**: Asegúrate de tener JDK instalado.
- **Gradle**: Opcional, ya que se incluye el wrapper (`gradlew`).
- **Node.js y npm**: Para compilar los estilos CSS con Tailwind (opcional).

### Pasos de Instalación

1. **Clona el repositorio**:
   ```bash
   git clone <url-del-repositorio>
   cd parcial-2
   ```

2. **Compila los estilos CSS (opcional)**:
   Si deseas modificar los estilos, instala las dependencias de npm y compila:
   ```bash
   npm install
   npm run build:css
   ```

3. **Construye el proyecto**:
   ```bash
   ./gradlew build
   ```

4. **Ejecuta la aplicación**:
   ```bash
   ./gradlew run
   ```
   O para Windows:
   ```cmd
   gradlew.bat run
   ```

   La aplicación estará disponible en `http://localhost:7000`.

### Configuración de Base de Datos

- **Desarrollo**: Usa H2 por defecto. El servidor H2 se inicia automáticamente en el puerto 9092.
- **Producción**: Configura PostgreSQL en `persistence.xml` y establece las variables de entorno para la conexión.

## Uso

1. **Accede a la aplicación**: Ve a `http://localhost:7000`.
2. **Regístrate o inicia sesión**: Crea una cuenta o usa las credenciales por defecto.
3. **Dashboard**: Visualiza estadísticas y navega por las secciones.
4. **Gestión de Estudiantes**: Crea, edita, elimina y lista estudiantes.
5. **Registros**: Registra estudiantes con ubicación GPS.
6. **Sincronización**: La app soporta sincronización offline para registros.

### Usuario por Defecto
- **Username**: admin
- **Password**: admin
- **Rol**: Administrador

## API y WebSockets

- **WebSockets**: Endpoint `/sync` para sincronización en tiempo real.
- **Rutas REST-like**: Implementadas en los controladores para CRUD operations.

## Pruebas

Ejecuta las pruebas con:
```bash
./gradlew test
```

## Despliegue

### Heroku
La aplicación está configurada para desplegarse en Heroku. El puerto se obtiene de la variable de entorno `PORT`.

### JAR Ejecutable
Genera un JAR con todas las dependencias:
```bash
./gradlew shadowJar
```
Ejecuta con:
```bash
java -jar build/libs/parcial-2-1.0-SNAPSHOT-all.jar
```

## Contribución

1. Fork el proyecto.
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`).
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`).
4. Push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.
