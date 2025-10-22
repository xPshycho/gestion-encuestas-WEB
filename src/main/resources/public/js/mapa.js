window.onload = initMap;

var colorMap = {
    'BASICO': 'red',
    'MEDIO': 'blue',
    'GRADO_UNIVERSITARIO': 'green',
    'POSTGRADO': 'purple',
    'DOCTORADO': 'orange'
};

var map;
var markers = [];
var defaultCenter = {lat: 18.2208, lng: -66.5901};
var defaultZoom = 8;

function initMap() {
    if (registros.length > 0) {
        defaultCenter = {
            lat: parseFloat(registros[0].latitud),
            lng: parseFloat(registros[0].longitud)
        };
    }

    map = new google.maps.Map(document.getElementById('map'), {
        zoom: defaultZoom,
        center: defaultCenter
    });

    var infoWindow = new google.maps.InfoWindow();

    registros.forEach(function (registro) {
        var student = estudianteData[registro.estudianteId];
        var color = student ? colorMap[student.nivelEscolar] : 'gray';
        var studentName = student ? student.nombre : 'Nombre no disponible';

        var marker = new google.maps.Marker({
            position: {
                lat: parseFloat(registro.latitud),
                lng: parseFloat(registro.longitud)
            },
            map: map,
            title: "Estudiante: " + studentName,
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                fillColor: color,
                fillOpacity: 1,
                strokeColor: color,
                strokeOpacity: 0.5,
                strokeWeight: 2,
                scale: 6
            }
        });

        marker.addListener('click', function () {
            var infoContent = `
                <div>
                    <strong>Estudiante:</strong> ${registro.estudianteId} - ${studentName}<br>
                    <strong>Usuario:</strong> ${registro.usuarioId || 'No disponible'}<br>
                    <strong>Ubicación:</strong> (${registro.latitud}, ${registro.longitud})
                </div>
            `;
            infoWindow.setContent(infoContent);
            infoWindow.open(map, marker);
        });

        markers.push({id: registro.id, marker: marker});
    });

    document.querySelectorAll("tbody tr").forEach(row => {
        row.addEventListener("click", function () {
            var selectedId = this.cells[0].innerText; // ID de la fila
            var selectedMarker = markers.find(m => m.id == selectedId)?.marker;
            if (selectedMarker) {
                map.setCenter(selectedMarker.getPosition());
                map.setZoom(15); // Zoom más cercano al seleccionar un punto
            }
        });
    });

    document.getElementById("reset-map").addEventListener("click", function () {
        map.setCenter(defaultCenter);
        map.setZoom(defaultZoom);
    });

    var legend = document.createElement('div');
    legend.classList.add('legend');
    legend.innerHTML = `
        <div><strong>Leyenda:</strong></div>
        <div><span style="color: red;">●</span> Básico</div>
        <div><span style="color: blue;">●</span> Medio</div>
        <div><span style="color: green;">●</span> Grado Universitario</div>
        <div><span style="color: purple;">●</span> Postgrado</div>
        <div><span style="color: orange;">●</span> Doctorado</div>
    `;
    map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(legend);
}

window.addEventListener('pageshow', function (event) {
    if (event.persisted) {
        initMap();
    }
});
