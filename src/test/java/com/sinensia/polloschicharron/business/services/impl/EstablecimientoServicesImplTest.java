package com.sinensia.polloschicharron.business.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sinensia.polloschicharron.business.model.Establecimiento;
import com.sinensia.polloschicharron.business.model.dtos.EstablecimientoDTO1;
import com.sinensia.polloschicharron.integration.model.EstablecimientoPL;
import com.sinensia.polloschicharron.integration.repositories.EstablecimientoPLRepository;

@ExtendWith(MockitoExtension.class)
class EstablecimientoServicesImplTest {

    @Mock
    private EstablecimientoPLRepository establecimientoPLRepository;

    @Mock
    private DozerBeanMapper mapper;

    @InjectMocks
    private EstablecimientoServicesImpl establecimientoServicesImpl;

    private Establecimiento establecimiento1;
    private Establecimiento establecimiento2;
    private EstablecimientoPL establecimientoPL1;
    private EstablecimientoPL establecimientoPL2;

    @BeforeEach
    void init() {
        initObjects();
    }

    @Test
    void testCreate() {

        when(establecimientoPLRepository.existsById("123456789")).thenReturn(false);
        when(mapper.map(establecimiento1, EstablecimientoPL.class)).thenReturn(establecimientoPL1);

        establecimientoServicesImpl.create(establecimiento1);

        // Verificar que el save fue llamado en el repositorio
       // when(establecimientoPLRepository.save(establecimientoPL1)).thenReturn(establecimientoPL1);
    }

    @Test
    void testCreateThrowsIllegalStateExceptionWhenNIFIsNull() {

        establecimiento1.setNIF(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            establecimientoServicesImpl.create(establecimiento1);
        });

        assertEquals("El NIF [null] no es válido o ya existe.", exception.getMessage());
    }

    @Test
    void testCreateThrowsIllegalStateExceptionWhenNIFExists() {

        when(establecimientoPLRepository.existsById("123456789")).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            establecimientoServicesImpl.create(establecimiento1);
        });

        assertEquals("El NIF [123456789] no es válido o ya existe.", exception.getMessage());
    }

    @Test
    void testRead() {

        when(establecimientoPLRepository.findById("123456789")).thenReturn(Optional.of(establecimientoPL1));
        when(establecimientoPLRepository.findById("999999999")).thenReturn(Optional.empty());

        when(mapper.map(establecimientoPL1, Establecimiento.class)).thenReturn(establecimiento1);

        Optional<Establecimiento> optional1 = establecimientoServicesImpl.read("123456789");
        Optional<Establecimiento> optional2 = establecimientoServicesImpl.read("999999999");

        assertTrue(optional1.isPresent());
        assertTrue(optional2.isEmpty());

        assertEquals("123456789", optional1.get().getNIF());
    }

    @Test
    void testUpdate() {

        when(establecimientoPLRepository.existsById("123456789")).thenReturn(true);
        when(mapper.map(establecimiento1, EstablecimientoPL.class)).thenReturn(establecimientoPL1);

        establecimientoServicesImpl.update(establecimiento1);

    }

    @Test
    void testUpdateThrowsIllegalStateExceptionWhenEstablecimientoNotExists() {

        when(establecimientoPLRepository.existsById("999999999")).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            establecimientoServicesImpl.update(establecimiento1);
        });

        assertEquals("El establecimiento con NIF [123456789] no existe.", exception.getMessage());
    }

    @Test
    void testGetAll() {

        when(establecimientoPLRepository.findAll()).thenReturn(List.of(establecimientoPL1, establecimientoPL2));
        when(mapper.map(establecimientoPL1, Establecimiento.class)).thenReturn(establecimiento1);
        when(mapper.map(establecimientoPL2, Establecimiento.class)).thenReturn(establecimiento2);

        List<Establecimiento> establecimientos = establecimientoServicesImpl.getAll();

        assertEquals(2, establecimientos.size());
        assertTrue(establecimientos.containsAll(List.of(establecimiento1, establecimiento2)));
    }

    @Test
    void testGetByProvincia() {

        when(establecimientoPLRepository.findByDireccionProvinciaIgnoreCase("Madrid"))
                .thenReturn(List.of(establecimientoPL1, establecimientoPL2));
        when(mapper.map(establecimientoPL1, Establecimiento.class)).thenReturn(establecimiento1);
        when(mapper.map(establecimientoPL2, Establecimiento.class)).thenReturn(establecimiento2);

        List<Establecimiento> establecimientos = establecimientoServicesImpl.getByProvincia("Madrid");

        assertEquals(2, establecimientos.size());
        assertTrue(establecimientos.containsAll(List.of(establecimiento1, establecimiento2)));
    }
/*
    @Test
    void testGetEstablecimientosDTO1() {

        EstablecimientoDTO1 dto1 = new EstablecimientoDTO1("Establecimiento 1", "123456789");
        EstablecimientoDTO1 dto2 = new EstablecimientoDTO1("Establecimiento 2", "987654321");

        when(establecimientoPLRepository.findDTO1()).thenReturn(List.of(dto1, dto2));

        List<EstablecimientoDTO1> dtoList = establecimientoServicesImpl.getEstablecimientosDTO1();

        assertEquals(2, dtoList.size());

        assertTrue(dtoList.stream().anyMatch(d -> d.getNombre().equals("Establecimiento 1")));
        assertTrue(dtoList.stream().anyMatch(d -> d.getNombre().equals("Establecimiento 2")));
    }

*/
    // ********************************************
    //
    // Private Methods
    //
    // ********************************************

    private void initObjects() {

        establecimientoPL1 = new EstablecimientoPL();
        establecimientoPL1.setNIF("123456789");

        establecimientoPL2 = new EstablecimientoPL();
        establecimientoPL2.setNIF("987654321");

        establecimiento1 = new Establecimiento();
        establecimiento1.setNIF("123456789");

        establecimiento2 = new Establecimiento();
        establecimiento2.setNIF("987654321");
    }
}
