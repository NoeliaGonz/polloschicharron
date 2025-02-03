package com.sinensia.polloschicharron.business.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sinensia.polloschicharron.business.model.Pedido;
import com.sinensia.polloschicharron.business.model.dtos.PedidoDTO1;
import com.sinensia.polloschicharron.integration.model.PedidoPL;
import com.sinensia.polloschicharron.integration.repositories.PedidoPLRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServicesImplTest {

    @Mock
    private PedidoPLRepository pedidoPLRepository;

    @Mock
    private DozerBeanMapper mapper;

    @InjectMocks
    private PedidoServicesImpl pedidoServicesImpl;

    private Pedido pedido1;
    private Pedido pedido2;
    private PedidoPL pedidoPL1;
    private PedidoPL pedidoPL2;

    @BeforeEach
    void init() {
        initObjects();
    }

    @Test
    void testCreate() {
        // Caso cuando el ID no es nulo, debe lanzar IllegalStateException
        Pedido pedidoConId = new Pedido();
        pedidoConId.setId(1L);  // Aquí el ID no es null, lo cual debería lanzar la excepción
        assertThrows(IllegalStateException.class, () -> pedidoServicesImpl.create(pedidoConId));

        // Caso cuando el ID es null, el pedido se puede crear correctamente
        Pedido pedidoSinId = new Pedido();
        pedidoSinId.setId(null);  // Asegúrate de que el ID sea null para que pase la validación

        // Mapea el pedido al PedidoPL
        when(mapper.map(pedidoSinId, PedidoPL.class)).thenReturn(pedidoPL1);
        // Simula la llamada al repositorio para guardar el pedido
        when(pedidoPLRepository.save(pedidoPL1)).thenReturn(pedidoPL1);

        Long idCreado = pedidoServicesImpl.create(pedidoSinId);  // Esto no debería lanzar la excepción

        assertNotNull(idCreado);  // Verifica que el ID retornado no sea null
        assertEquals(1L, idCreado);  // Verifica que el ID creado sea el esperado (simulado como 1L en este caso)
    }


    @Test
    void testRead() {
        // Caso cuando el pedido existe
        when(pedidoPLRepository.findById(1L)).thenReturn(Optional.of(pedidoPL1));
        when(mapper.map(pedidoPL1, Pedido.class)).thenReturn(pedido1);

        Optional<Pedido> optional1 = pedidoServicesImpl.read(1L);

        assertTrue(optional1.isPresent());
        assertEquals(1L, optional1.get().getId());

        // Caso cuando el pedido no existe
        when(pedidoPLRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pedido> optional2 = pedidoServicesImpl.read(999L);

        assertTrue(optional2.isEmpty());
    }

    @Test
    void testUpdate() {
        Long idExistente = 1L;
        Long idNoExistente = 999L;  // ID que no existe

        // Simula un Pedido con ID que no existe
        Pedido pedidoAActualizarNoExistente = new Pedido();
        pedidoAActualizarNoExistente.setId(idNoExistente);

        // Simula que el pedido no existe en la base de datos
        when(pedidoPLRepository.existsById(idNoExistente)).thenReturn(false);

        // Aquí estamos esperando que se lance una IllegalStateException
        assertThrows(IllegalStateException.class, () -> pedidoServicesImpl.update(pedidoAActualizarNoExistente));

        // Para el caso en que el pedido sí existe:
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(idExistente);

        // Simula que el pedido con el ID 1 existe en la base de datos
        when(pedidoPLRepository.existsById(idExistente)).thenReturn(true);

        // Simula el mapeo de Pedido -> PedidoPL
        PedidoPL pedidoPL = new PedidoPL();
        when(mapper.map(pedidoExistente, PedidoPL.class)).thenReturn(pedidoPL);

        // Simula que el método save recibe un PedidoPL no null
        when(pedidoPLRepository.save(pedidoPL)).thenReturn(pedidoPL);

        // Ahora no debería lanzar excepción, porque el pedido existe y el mapeo es válido
        pedidoServicesImpl.update(pedidoExistente);
    }



    @Test
    void testGetAll() {
        when(pedidoPLRepository.findAll()).thenReturn(List.of(pedidoPL1, pedidoPL2));
        when(mapper.map(pedidoPL1, Pedido.class)).thenReturn(pedido1);
        when(mapper.map(pedidoPL2, Pedido.class)).thenReturn(pedido2);

        List<Pedido> pedidos = pedidoServicesImpl.getAll();

        assertEquals(2, pedidos.size());
        assertTrue(pedidos.containsAll(List.of(pedido1, pedido2)));
    }
    @Test
    void testGetPedidosDTO1() {
        // Crear instancias de Pedido para simular los datos que tendríamos en la base de datos.
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setFechaHora(new Date());
        // Aquí debes agregar más campos al Pedido (empleado, establecimiento, estado)
        // Asegúrate de crear esos objetos como sean necesarios, como Empleado, Establecimiento, EstadoPedido
        pedido1.setObservaciones("Observación 1");

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setFechaHora(new Date());
        pedido2.setObservaciones("Observación 2");

        // Crear PedidoDTO1 con los campos ajustados a tu modelo
        PedidoDTO1 pedidoDTO11 = new PedidoDTO1(
            pedido1.getId(),
            pedido1.getFechaHora(),
            "ESTABLECIMIENTO X", // Aquí debes mapear la información adecuada de tu objeto Establecimiento
            "GALVEZ RIDRUEJO, PEPÍN", // Aquí debes mapear la información adecuada de tu objeto Empleado
            "En preparación" // Aquí debes mapear el estado adecuado del pedido
        );

        PedidoDTO1 pedidoDTO12 = new PedidoDTO1(
            pedido2.getId(),
            pedido2.getFechaHora(),
            "ESTABLECIMIENTO Y", // Asumiendo el nombre de establecimiento
            "PÉREZ LÓPEZ, ANA", // Asumiendo el nombre del empleado
            "Completado" // Asumiendo el estado
        );

        // Simula el comportamiento del repositorio y el mapper
        when(pedidoPLRepository.findDTO1()).thenReturn(List.of(pedidoDTO11, pedidoDTO12));

        // Llama al servicio para obtener la lista de PedidoDTO1
        List<PedidoDTO1> pedidosDTO1 = pedidoServicesImpl.getPedidosDTO1();

        // Verifica que el tamaño de la lista sea 2
        assertEquals(2, pedidosDTO1.size());

        // Verifica que los DTOs devueltos son correctos
        assertTrue(pedidosDTO1.contains(pedidoDTO11));
        assertTrue(pedidosDTO1.contains(pedidoDTO12));

        // Verifica que los valores de los DTOs sean correctos
        assertEquals("ESTABLECIMIENTO X", pedidosDTO1.get(0).getEstablecimiento());
        assertEquals("GALVEZ RIDRUEJO, PEPÍN", pedidosDTO1.get(0).getEmpleado());
        assertEquals("En preparación", pedidosDTO1.get(0).getEstado());

        assertEquals("ESTABLECIMIENTO Y", pedidosDTO1.get(1).getEstablecimiento());
        assertEquals("PÉREZ LÓPEZ, ANA", pedidosDTO1.get(1).getEmpleado());
        assertEquals("Completado", pedidosDTO1.get(1).getEstado());
    }

    // ********************************************
    //
    // Private Methods
    //
    // ********************************************

    private void initObjects() {
        pedidoPL1 = new PedidoPL();
        pedidoPL1.setId(1L);
        pedidoPL1.setFechaHora(new java.util.Date());
        pedidoPL1.setObservaciones("Observaciones Pedido 1");

        pedidoPL2 = new PedidoPL();
        pedidoPL2.setId(2L);
        pedidoPL2.setFechaHora(new java.util.Date());
        pedidoPL2.setObservaciones("Observaciones Pedido 2");

        pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setFechaHora(new java.util.Date());
        pedido1.setObservaciones("Observaciones Pedido 1");

        pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setFechaHora(new java.util.Date());
        pedido2.setObservaciones("Observaciones Pedido 2");
    }
}
