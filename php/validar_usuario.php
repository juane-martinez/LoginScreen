<?php
include 'conexion.php';
$nombre_usuario = $_POST['usuario'];
$contraseña = $_POST['password'];

$sentencia = $conexion->prepare("SELECT * FROM usuarios WHERE nombre_usuario=? AND contraseña=?");
$sentencia->bind_param('ss', $nombre_usuario, $contraseña);
$sentencia->execute();

$resultado = $sentencia->get_result();
if ($fila = $resultado->fetch_assoc()) {
    // Incluir el tipo de usuario en la respuesta JSON
    echo json_encode([
        'usuario' => $fila['nombre_usuario'],
        'tipo_usuario' => $fila['tipo_usuario']
    ], JSON_UNESCAPED_UNICODE);
} else {
    // Enviar una respuesta que el cliente puede verificar para saber que la autenticación falló
    echo json_encode(['error' => 'Usuario o contraseña incorrectos']);
}
$sentencia->close();
$conexion->close();
?>