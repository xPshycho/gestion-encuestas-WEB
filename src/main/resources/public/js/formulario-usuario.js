// js/formulario-usuario.js

window.addEventListener('load', () => {
    setTimeout(() => {
        const formulario = document.getElementById("formulario");
        if (formulario) {
            formulario.classList.remove("opacity-0", "translate-y-10");
            formulario.classList.add("opacity-100", "translate-y-0");
        }
    }, 100);

    cargarRegistroPendienteUsuario();
});

function getEditIndex() {
    const params = new URLSearchParams(window.location.search);
    return params.get('editPendingUser');
}

document.getElementById('userForm').addEventListener('submit', function (e) {
    if (!navigator.onLine) {
        e.preventDefault();

        const formData = {
            username: document.querySelector('[name="username"]').value.trim(),
            nombre: document.querySelector('[name="nombre"]').value.trim(),
            password: document.querySelector('[name="password"]').value.trim(),
            rol: document.querySelector('[name="rol"]').value.toLowerCase() // Se almacena el rol como string
        };

        localforage.getItem('pendingUsers').then(existing => {
            let records = existing || [];
            const editIndex = getEditIndex();

            if (editIndex !== null) {
                records[parseInt(editIndex)] = formData;
            } else {
                records.push(formData);
            }

            return localforage.setItem('pendingUsers', records);
        }).then(() => {
            alert("Usuario guardado offline. Se sincronizará desde el dashboard.");
            window.location.href = "/dashboard";
        });
    } else {
        // Si hay conexión a internet, eliminar el registro pendiente si se está editando
        const editIndex = getEditIndex();
        if (editIndex !== null) {
            e.preventDefault();

            localforage.getItem('pendingUsers').then(users => {
                users = users || [];
                users.splice(parseInt(editIndex), 1);
                return localforage.setItem('pendingUsers', users);
            }).then(() => {
                document.getElementById('userForm').submit();
            }).catch(error => {
                console.error("Error al eliminar el registro pendiente:", error);
                alert("Ocurrió un error al eliminar el registro pendiente.");
            });
        }
    }
});

function cargarRegistroPendienteUsuario() {
    const editIndex = getEditIndex();
    if (editIndex !== null) {
        localforage.getItem('pendingUsers').then(users => {
            users = users || [];
            const user = users[editIndex];
            if (user) {
                document.querySelector('[name="username"]').value = user.username;
                document.querySelector('[name="nombre"]').value = user.nombre;
                document.querySelector('[name="password"]').value = user.password;
                document.querySelector('[name="rol"]').value = user.rol;
            }
        });
    }
}
