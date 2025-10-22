// js/formulario-estudiante.js

// Animación de entrada del formulario
window.addEventListener('load', () => {
    setTimeout(() => {
        const formulario = document.getElementById("formulario");
        formulario.classList.remove("opacity-0", "translate-y-10");
        formulario.classList.add("opacity-100", "translate-y-0");
    }, 100);

    // Si no estamos en modo edición, se obtiene la ubicación
    if (!getEditIndex()) {
        navigator.geolocation.getCurrentPosition(position => {
            document.getElementById('latitud').value = position.coords.latitude.toFixed(6);
            document.getElementById('longitud').value = position.coords.longitude.toFixed(6);
        });
    }
    cargarRegistroPendienteEstudiante();
});

// Detectar si se está editando un registro pendiente
function getEditIndex() {
    const params = new URLSearchParams(window.location.search);
    return params.get('editPending'); // Retorna null si no está en edición
}

document.getElementById('estudianteForm').addEventListener('submit', function (e) {
    if (!navigator.onLine) {
        e.preventDefault();

        const formData = {
            nombre: document.querySelector('[name="nombre"]').value,
            sector: document.querySelector('[name="sector"]').value,
            nivelEscolar: document.querySelector('[name="nivelEscolar"]').value,
            latitud: document.querySelector('[name="latitud"]').value || "0",
            longitud: document.querySelector('[name="longitud"]').value || "0"
        };

        localforage.getItem('surveyData').then(existing => {
            let records = existing || [];
            const editIndex = getEditIndex(); // Obtener el índice del estudiante en edición

            if (editIndex !== null) {
                records.splice(parseInt(editIndex), 1);
            }

            records.push(formData); // Guardar el nuevo registro
            return localforage.setItem('surveyData', records);
        }).then(() => {
            alert("Datos guardados offline y eliminados de registros pendientes.");
            window.location.href = "/dashboard";
        }).catch(error => {
            console.error("Error al guardar en localForage:", error);
            alert("Ocurrió un error al guardar los cambios.");
        });
    } else {
        // Si hay conexión a internet, eliminar el registro pendiente si se está editando
        const editIndex = getEditIndex();
        if (editIndex !== null) {
            e.preventDefault(); // Prevenir el envío del formulario hasta que se elimine el registro pendiente

            localforage.getItem('surveyData').then(records => {
                records = records || [];
                records.splice(parseInt(editIndex), 1); // Eliminar el registro pendiente
                return localforage.setItem('surveyData', records);
            }).then(() => {
                // Ahora enviar el formulario
                document.getElementById('estudianteForm').submit();
            }).catch(error => {
                console.error("Error al eliminar el registro pendiente:", error);
                alert("Ocurrió un error al eliminar el registro pendiente.");
            });
        }
    }
});

// Cargar registro pendiente para edición (si existe el parámetro editPending)
function cargarRegistroPendienteEstudiante() {
    const params = new URLSearchParams(window.location.search);
    const editIndex = params.get('editPending');
    if (editIndex !== null) {
        localforage.getItem('surveyData').then(records => {
            records = records || [];
            const registro = records[editIndex];
            if (registro) {
                document.querySelector('[name="nombre"]').value = registro.nombre;
                document.querySelector('[name="sector"]').value = registro.sector;
                document.querySelector('[name="nivelEscolar"]').value = registro.nivelEscolar;
                document.getElementById('latitud').value = registro.latitud;
                document.getElementById('longitud').value = registro.longitud;
            }
        });
    }
}
