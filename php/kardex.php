<?php
include 'conexion.php'; // Asegúrate de que tu archivo de conexión es seguro y usa PDO o MySQLi.

$id_paciente = $_POST['id_paciente'];

$sentencia = $conexion->prepare("SELECT fecha_ingreso, cama, medicamento, dosis, via, horario FROM kardex WHERE id_paciente = ?");
$sentencia->bind_param('i', $id_paciente);
$sentencia->execute();
$resultado = $sentencia->get_result();

if ($fila = $resultado->fetch_assoc()) {
    echo json_encode($fila, JSON_UNESCAPED_UNICODE);
} else {
    echo json_encode(['error' => 'No se encontraron registros en el kardex para el paciente']);
}

$sentencia->close();
$conexion->close();
?>