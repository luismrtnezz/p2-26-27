param([switch]$Subir)

# Ejecutar una sola vez desde el PowerShell del alumno. No requiere administrador.
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
function Invoke-Git {
    & git @args
    if ($LASTEXITCODE -ne 0) { throw "Git ha fallado: $args. No se continuara." }
}

$rama = (& git branch --show-current).Trim()
if ($rama -ne 'feature/estrategia') { throw 'Se esperaba la rama feature/estrategia. Revise git status.' }
$staged = & git diff --cached --name-only
if ($staged) { throw 'Hay cambios preparados en el indice. Revise git status antes de continuar.' }
foreach ($nombre in @('feature/serializacion','feature/factoria-politicas','feature/gestor')) {
    & git show-ref --verify --quiet "refs/heads/$nombre"
    if ($LASTEXITCODE -eq 0) { throw "Ya existe $nombre. Este script no se debe repetir." }
}

# Validar el estado final antes de registrar los cambios.
& mvn test
if ($LASTEXITCODE -ne 0) { throw 'Las pruebas Maven no han terminado correctamente. No se ha creado ningun commit.' }

$rutaAlquiler = Join-Path $PSScriptRoot 'src/main/java/telem/Alquiler.java'
$utf8 = New-Object System.Text.UTF8Encoding($false)
$alquilerFinal = [System.IO.File]::ReadAllText($rutaAlquiler, $utf8)
# La primera version contiene solo Estrategia. La deserializacion se registra despues.
$patron = '(?s)    public Alquiler\(String jsonString\).*?(?=    public static Alquiler\[\] obtenerAlquileres)'
$alquilerEstrategia = [regex]::Replace($alquilerFinal, $patron, '')
if ($alquilerEstrategia -eq $alquilerFinal) { throw 'No se ha encontrado el bloque JSON esperado. No se modifica el codigo.' }
$restaurado = $false
try {
    [System.IO.File]::WriteAllText($rutaAlquiler, $alquilerEstrategia, $utf8)
    Invoke-Git add -- src/main/java/telem/Alquiler.java src/main/java/telem/PoliticaPrecio.java src/main/java/telem/PoliticaNormal.java src/main/java/telem/PoliticaOferton.java src/main/java/telem/PoliticaTemporadaAlta.java src/main/java/telem/PoliticaClienteFrecuente.java src/test/java/telem/PoliticasPrecioTest.java
    Invoke-Git commit -m 'Ejercicio 2: estrategias de precios, ejemplo y pruebas'
    Invoke-Git switch main
    Invoke-Git merge --no-ff feature/estrategia -m 'Integra estrategias de precios'

    Invoke-Git switch -c feature/serializacion
    [System.IO.File]::WriteAllText($rutaAlquiler, $alquilerFinal, $utf8)
    $restaurado = $true
    Invoke-Git add -- src/main/java/telem/Alquiler.java src/main/java/telem/Cliente.java src/main/java/telem/Vehiculo.java src/main/java/telem/Turismo.java src/main/java/telem/Motocicleta.java src/main/java/telem/VehiculoIndustrial.java src/main/java/telem/FactoriaVehiculos.java src/main/java/telem/FactoriaClientes.java src/main/java/telem/FactoriaAlquileres.java src/test/java/telem/FactoriasTest.java src/test/java/telem/ModeloTest.java
    Invoke-Git commit -m 'Ejercicios 1 y 3: pruebas del modelo y factorias de deserializacion'
    Invoke-Git switch main
    Invoke-Git merge --no-ff feature/serializacion -m 'Integra serializacion y pruebas del modelo'

    Invoke-Git switch -c feature/factoria-politicas
    Invoke-Git add -- src/main/java/telem/PoliticasDePrecios.java src/main/java/telem/FactoriaPoliticas.java src/test/java/telem/FactoriaPoliticasTest.java
    Invoke-Git commit -m 'Ejercicio 4: factoria de politicas, ejemplo y pruebas'
    Invoke-Git switch main
    Invoke-Git merge --no-ff feature/factoria-politicas -m 'Integra factoria de politicas'

    Invoke-Git switch -c feature/gestor
    Invoke-Git add -- src/main/java/telem/GestorAlquileres.java src/main/java/telem/KeyBoard.java src/test/java/telem/GestorAlquileresTest.java README.md finalizar-entrega.ps1
    Invoke-Git commit -m 'Ejercicios 5 y 6: gestor completo, persistencia JSON y pruebas'
    Invoke-Git switch main
    Invoke-Git merge --no-ff feature/gestor -m 'Integra gestor y persistencia'

    if ($Subir) {
        Invoke-Git push origin main feature/estrategia feature/serializacion feature/factoria-politicas feature/gestor refactor_fechas
    }
    Invoke-Git status --short
    Write-Host 'Cambios integrados en main.'
    if (-not $Subir) { Write-Host 'Pendiente subir: git push origin main feature/estrategia feature/serializacion feature/factoria-politicas feature/gestor refactor_fechas' }
} finally {
    if (-not $restaurado) { [System.IO.File]::WriteAllText($rutaAlquiler, $alquilerFinal, $utf8) }
}
