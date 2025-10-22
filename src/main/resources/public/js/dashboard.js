// js/dashboard.js

const openSidebarBtn = document.getElementById('openSidebar');
const closeSidebarBtn = document.getElementById('closeSidebar');
const overlayMenu = document.getElementById('overlayMenu');

// Abrir y cerrar el overlay del menú
openSidebarBtn.addEventListener('click', () => {
    overlayMenu.classList.remove('-translate-x-full');
    overlayMenu.classList.add('translate-x-0');
});
closeSidebarBtn.addEventListener('click', () => {
    overlayMenu.classList.remove('translate-x-0');
    overlayMenu.classList.add('-translate-x-full');
});

function loadPendingRecordsDashboard() {
    // Cargar estudiantes pendientes
    localforage.getItem('surveyData').then(students => {
        students = students || [];
        const list = document.getElementById('pendingStudentsList');
        if (list) {
            list.innerHTML = '';
            students.forEach((student, index) => {
                const li = document.createElement('li');
                li.className = 'p-4 border-b';
                li.innerHTML = `
                    <div>
                      <strong>Nombre:</strong> ${student.nombre}<br>
                      <strong>Sector:</strong> ${student.sector}<br>
                      <strong>Nivel:</strong> ${student.nivelEscolar}
                    </div>
                    <div class="mt-2">
                      <button onclick="sendPendingStudent(${index})" class="bg-green-500 text-white px-2 py-1 rounded">Enviar</button>
                      <button onclick="editPendingStudent(${index})" class="bg-yellow-500 text-white px-2 py-1 rounded">Editar</button>
                      <button onclick="deletePendingStudent(${index})" class="bg-red-500 text-white px-2 py-1 rounded">Eliminar</button>
                    </div>`;
                list.appendChild(li);
            });
        }
    });

    // Cargar usuarios pendientes
    localforage.getItem('pendingUsers').then(users => {
        users = users || [];
        const list = document.getElementById('pendingUsersList');
        if (list) {
            list.innerHTML = '';
            users.forEach((user, index) => {
                // Se muestra el rol basado en la propiedad "rol" (almacenada en minúsculas)
                const displayRole = (user.rol === "admin" || user.rol === "administrador")
                    ? "Administrador"
                    : (user.rol === "encuestador" ? "Encuestador" : "Usuario");
                const li = document.createElement('li');
                li.className = 'p-4 border-b';
                li.innerHTML = `
                    <div>
                      <strong>Usuario:</strong> ${user.username}<br>
                      <strong>Nombre:</strong> ${user.nombre}<br>
                      <strong>Rol:</strong> ${displayRole}
                    </div>
                    <div class="mt-2">
                      <button onclick="sendPendingUser(${index})" class="bg-green-500 text-white px-2 py-1 rounded">Enviar</button>
                      <button onclick="editPendingUser(${index})" class="bg-yellow-500 text-white px-2 py-1 rounded">Editar</button>
                      <button onclick="deletePendingUser(${index})" class="bg-red-500 text-white px-2 py-1 rounded">Eliminar</button>
                    </div>`;
                list.appendChild(li);
            });
        }
    });
}

function sendPendingStudent(index) {
    if (!navigator.onLine) {
        alert("No hay conexión a internet. El registro se mantendrá pendiente.");
        return;
    }

    localforage.getItem('surveyData').then(records => {
        records = records || [];
        if (index >= records.length) {
            alert("Error: Índice fuera de rango.");
            return;
        }

        const student = records[index];

        return fetch('/estudiantes/crear', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams(student)
        }).then(response => {
            if (!response.ok) {
                throw new Error("Error en la respuesta del servidor: " + response.statusText);
            }
            // Si la respuesta es exitosa, eliminar de la lista de pendientes
            records.splice(index, 1);
            return localforage.setItem('surveyData', records);
        }).then(() => {
            alert("Estudiante enviado exitosamente.");
            loadPendingRecordsDashboard();
            window.location.reload();
        }).catch(error => {
            console.error("Error al enviar estudiante:", error);
            alert("Error al enviar estudiante al servidor.");
        });
    });
}

function editPendingStudent(index) {
    window.location.href = "/estudiantes/crear?editPending=" + index;
}

function deletePendingStudent(index) {
    localforage.getItem('surveyData').then(records => {
        records = records || [];
        records.splice(index, 1);
        return localforage.setItem('surveyData', records);
    }).then(() => loadPendingRecordsDashboard());
}

function sendPendingUser(index) {
    if (!navigator.onLine) {
        alert("No hay conexión. El usuario permanecerá en pendientes.");
        return;
    }

    localforage.getItem('pendingUsers').then(users => {
        users = users || [];
        if (index >= users.length) return;

        const user = users[index];

        return fetch('/usuarios/sync', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify([user])
        }).then(response => {
            if (!response.ok) throw new Error("Error al enviar usuario.");
            users.splice(index, 1);
            return localforage.setItem('pendingUsers', users);
        }).then(() => {
            alert("Usuario enviado exitosamente.");
            loadPendingRecordsDashboard();
            window.location.reload();
        }).catch(error => {
            alert("Error al enviar usuario: " + error.message);
        });
    });
}

function editPendingUser(index) {
    window.location.href = "/crear-usuario?editPendingUser=" + index;
}

function deletePendingUser(index) {
    localforage.getItem('pendingUsers').then(users => {
        users = users || [];
        users.splice(index, 1);
        return localforage.setItem('pendingUsers', users);
    }).then(() => loadPendingRecordsDashboard());
}

window.addEventListener('load', () => {
    console.log("Cargando registros pendientes...");
    loadPendingRecordsDashboard();
});
