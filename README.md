# App Android - Reserva de cubículos de estudio

Aplicación del proyecto individual de Desarrollo de aplicaciones móviles (Uniautónoma del Cauca, docente Zulema León).
Permite consultar los cubículos de la biblioteca, ver las reservas por fecha y crear, editar y cancelar reservas. La regla de no cruzar horarios la valida la API.

## Funcionalidades
- Lista de cubículos y reservas por cubículo y fecha.
- Detalle de reserva.
- Crear, editar y cancelar reservas, con aviso cuando el horario ya está ocupado (409).
- Indicador de carga y mensajes de error y confirmación.

## Tecnología
Kotlin , ViewBinding , RecyclerView , Retrofit 2.11.0 + Gson , Corrutinas 1.10.2 , Lifecycle 2.8.7

## Estructura (`app/src/main/java/com/example/reservacubiculos`)
`MainActivity` , `ReservasCubiculoActivity` , `DetalleReservaActivity` , `FormularioReservaActivity` , `adapter/` , `model/` , `network/` (`ApiService`, `RetrofitClient`, `Errores`) , `Utilidades.kt`

## Cómo ejecutarla
1. Abrir la carpeta del proyecto en Android Studio y esperar a que Gradle sincronice.
2. Ejecutar (Run) en un emulador o en un celular Android con internet.
3. También se puede instalar el APK entregado.

## Configurar la URL de la API
En `network/RetrofitClient.kt` está la constante `BASE_URL`:

`https://backend-cubiculos.onrender.com/api/`

Si usas el backend en tu computador, cambia la URL por la de tu servidor (con el emulador, `http://10.0.2.2:3000/api/`; puede requerir permitir tráfico HTTP sin cifrar). La API en la nube está en plan gratuito: la primera petición puede tardar hasta 1 minuto.

## Backend utilizado
https://github.com/Banguero44/backend-cubiculos
