<?php
include 'conexion.php';

// Obtener la id del query string
$id = $_GET['id'];

// Preparar y ejecutar la consulta
$stmt = $conn->prepare("SELECT p.id_paciente, p.nombre, p.fecha_nacimiento, hc.diagnostico, hc.tratamiento FROM paciente p INNER JOIN historia_clinica hc ON p.id_paciente = hc.id_paciente WHERE p.id_paciente = ?");
$stmt->bind_param("s", $id);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}

$stmt->close();
$conn->close();

// Devolver los datos en formato JSON
echo json_encode($data);
?>
