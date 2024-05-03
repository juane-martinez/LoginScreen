<?php
include 'conexion.php';

$latitud = $_POST['latitud'];
$longitud = $_POST['longitud'];
$hora = $_POST['hora'];

$sentencia = $conexion->prepare("INSERT INTO ubicacion (latitud, longitud, hora) VALUES (?, ?, ?)");
$sentencia->bind_param('dds', $latitud, $longitud, $hora);
if ($sentencia->execute()) {
    // Si quieres retornar el ID generado
    $id_ubicacion = $sentencia->insert_id;
    echo json_encode(['mensaje' => "Datos insertados correctamente", 'id_ubicacion' => $id_ubicacion]);
} else {
    echo json_encode(['error' => "Error al insertar datos"]);
}

$sentencia->close();
$conexion->close();
?>