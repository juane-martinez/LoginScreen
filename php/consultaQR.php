<?php
include 'conexion.php'; // Asegúrate de que tu archivo de conexión es seguro y usa PDO o MySQLi.

$id_paciente = $_POST['id_paciente'];

$sentencia = $conexion->prepare("SELECT nombre, apellido, fecha_nacimiento, sexo, direccion, telefono FROM paciente WHERE id_paciente = ?");
$sentencia->bind_param('i', $id_paciente);
$sentencia->execute();
$resultado = $sentencia->get_result();

if ($fila = $resultado->fetch_assoc()) {
    echo json_encode($fila, JSON_UNESCAPED_UNICODE);
} else {
    echo json_encode(['error' => 'No se encontró el paciente']);
}

$sentencia->close();
$conexion->close();
?>