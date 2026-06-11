# Prueba 1: CREAR USUARIO
echo "=== CREANDO USUARIO ==="
curl --location "http://localhost:8080/api/auth/register" ^
--header "Content-Type: application/json" ^
--data-raw "{\\"nombre\\":\\"TestUser\\",\\"apellido\\":\\"TestApellido\\",\\"email\\":\\"testuser@test.com\\",\\"password\\":\\"password123\\"}"