<?php
include 'conexion.php'; // Incluye tu script de conexión a la base de datos.

$id_paciente = $_POST['id_paciente'];

$sentencia = $conexion->prepare("SELECT fecha_creacion, ultima_actualizacion, diagnostico, tratamiento FROM historia_clinica WHERE id_paciente = ?");
$sentencia->bind_param('i', $id_paciente);
$sentencia->execute();
$resultado = $sentencia->get_result();

if ($fila = $resultado->fetch_assoc()) {
    echo json_encode($fila, JSON_UNESCAPED_UNICODE);
} else {
    echo json_encode(['error' => 'No se encontró la historia clínica del paciente']);
}

$sentencia->close();
$conexion->close();
?>