self.importScripts('https://cdnjs.cloudflare.com/ajax/libs/localforage/1.9.0/localforage.min.js');

function syncData() {
    if (!navigator.onLine) return;

    localforage.getItem('pendingUsers').then(users => {
        if (users && users.length > 0) {
            fetch('/usuarios/sync', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(users)
            }).then(response => {
                if (!response.ok) throw new Error("Error en la sincronización.");
                return localforage.removeItem('pendingUsers');
            }).then(() => {
                console.log("Usuarios sincronizados.");
            }).catch(error => {
                console.error("Error al sincronizar:", error);
            });
        }
    });
}

self.addEventListener('message', e => {
    if (e.data === 'sync') syncData();
});
