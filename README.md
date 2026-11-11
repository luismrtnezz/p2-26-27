# Practica 2 - Programacion para Ingenieria Telematica

Aplicacion de consola de alquiler de vehiculos. Java 8 y Maven.

## Ejecutar

1. Ejecutar `mvn test` desde la raiz del proyecto.
2. Abrir `src/main/java/telem/GestorAlquileres.java` en VS Code y pulsar Run sobre su `main`.
3. Menu principal: 1 operar como cliente, 2 guardar JSON, 3 recuperar JSON, 0 salir.
4. Dentro de un cliente: 1 alquilar, 2 devolver por matricula, 3 consultar historial.

Ejemplo: cliente 0, alquilar, turismo, categoria normal, vehiculo 0, tres dias: 150 euros.
La devolucion de ese vehiculo se hace indicando `TNA2020`.
Las rutas de guardado pueden ser absolutas o relativas al directorio de ejecucion.

## Diseno y decisiones

- **Estrategia:** `PoliticaPrecio` define el contrato. Las cuatro implementaciones calculan
  el precio sin condicionales en `Alquiler.calcularPrecio`.
- **Factoria de vehiculos:** `FactoriaVehiculos` lee `tipo` y elige el constructor JSON
  de Turismo, Motocicleta o VehiculoIndustrial. Clientes y alquileres tienen tambien
  constructores JSON y metodos de creacion en sus respectivas factorias.
- **Factoria de politicas:** `FactoriaPoliticas` recibe el enumerado `PoliticasDePrecios`
  del profesor y devuelve la estrategia correspondiente.
- **Fechas:** se incluyen inicio y fin, conforme al apartado 4. Tres dias empezando el
  11 terminan el 13. Las fechas invertidas se rechazan.
- **Disponibilidad:** un vehiculo nuevo esta disponible; registrarlo en el gestor lo
  ocupa y devolverlo lo libera. El historial no se elimina. El array `activos` distingue
  alquileres devueltos de posteriores alquileres del mismo vehiculo.
- **Cliente frecuente:** el gestor suministrado indica mas de tres alquileres previos.
  Se cuentan antes de registrar el nuevo: el quinto alquiler obtiene el descuento.
- **Persistencia:** se guarda una instantanea completa con clientes, vehiculos,
  alquileres y el indicador `activo`. Recuperar sustituye el estado actual. Cargar
  dos veces no duplica registros. Se valida todo antes de cambiar el estado.
- **Identidad:** al recuperar, los alquileres apuntan a los objetos del catalogo,
  identificados por DNI y matricula. Una devolucion actualiza el mismo vehiculo
  que aparece en la seleccion. La disponibilidad del vehiculo anidado en el JSON
  de un alquiler refleja su estado actual, no una instantanea historica.
- **Consola:** se utiliza un unico Scanner dentro del gestor para evitar la perdida
  de entradas causada por crear lectores con buffer nuevos en cada lectura.
  `KeyBoard.java` se conserva como fue suministrado.

## Pruebas y ejemplos

Las pruebas comprueban precios de las nueve combinaciones tipo/categoria, fechas,
descuentos en los dias 3/4 y 7/8, delegacion de estrategias, reconstruccion JSON,
factorias, filtros, alquileres, devoluciones, cliente frecuente y persistencia.

Ejemplos ejecutables: `Alquiler.main`, `FactoriaVehiculos.main`, `FactoriaPoliticas.main`.
El punto de entrada interactivo es `GestorAlquileres.main`; `App` es el ejemplo original.

## Entrega y autoria

El enunciado pide entregar el enlace al repositorio remoto. Los cambios no estan
entregados hasta realizar los commits, integrar las ramas y subirlos a GitHub.
`finalizar-entrega.ps1` organiza los cambios actuales por ramas y los integra;
con `-Subir` tambien envia las ramas a origin. No reconstruye fechas de trabajo
ni sustituye el historial anterior. Debe ejecutarse desde el PowerShell del alumno.

Se ha utilizado asistencia de IA en implementacion, pruebas y ejemplos. Para la
entrevista es necesario comprender las decisiones anteriores y poder explicar
el codigo. El reparto real de trabajo entre los miembros debe describirlo la pareja.
