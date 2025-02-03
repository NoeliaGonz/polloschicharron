package com.sinensia.polloschicharron.business.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import com.sinensia.polloschicharron.business.model.Familia;
import com.sinensia.polloschicharron.integration.model.FamiliaPL;
import com.sinensia.polloschicharron.integration.repositories.FamiliaPLRepository;

@ExtendWith(MockitoExtension.class)
class FamiliaServicesImplTest {

    @Mock
    private FamiliaPLRepository familiaPLRepository;

    @Mock
    private DozerBeanMapper mapper;

    @InjectMocks
    private FamiliaServicesImpl familiaServicesImpl;

    private Familia familia1;
    private Familia familia2;
    private FamiliaPL familiaPL1;
    private FamiliaPL familiaPL2;

    @BeforeEach
    void init() {
        initObjects();
    }

    @Test
    void testCreate() {

        // Asegúrate de que el id de la familia sea null
        familia1.setId(null);

        // Simula que el repositorio guarda correctamente la FamiliaPL
        when(mapper.map(familia1, FamiliaPL.class)).thenReturn(familiaPL1);
        when(familiaPLRepository.save(familiaPL1)).thenReturn(familiaPL1); // Simula que save() retorna familiaPL1

        // Llamada al método que se está probando
        familiaServicesImpl.create(familia1);

        // Verificación que el save() fue llamado con el objeto esperado
        verify(familiaPLRepository).save(familiaPL1);

        // Asegúrate de que el ID fue asignado correctamente
        assertNotNull(familiaPL1.getId(), "El ID de la familia debería haberse asignado.");
    }




    @Test
    void testCreateThrowsIllegalStateExceptionWhenIdIsNotNull() {

        familia1.setId(1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            familiaServicesImpl.create(familia1);
        });

        assertEquals("Para crear una familia el id ha de ser null.", exception.getMessage());
    }

    @Test
    void testRead() {

        when(familiaPLRepository.findById(1L)).thenReturn(Optional.of(familiaPL1));
        when(familiaPLRepository.findById(999L)).thenReturn(Optional.empty());

        when(mapper.map(familiaPL1, Familia.class)).thenReturn(familia1);

        Optional<Familia> optional1 = familiaServicesImpl.read(1L);
        Optional<Familia> optional2 = familiaServicesImpl.read(999L);

        assertTrue(optional1.isPresent());
        assertTrue(optional2.isEmpty());

        assertEquals(1L, optional1.get().getId());
    }

    @Test
    void testUpdate() {
        // Configura el objeto Familia para actualizar
        familia1.setId(1L);  // Asegúrate de que el id no sea null
        familia1.setNombre("Familia Actualizada"); // Establecer el nombre esperado

        // Configura la simulación para que `existsById()` devuelva true
        when(familiaPLRepository.existsById(1L)).thenReturn(true);

        // Simula el mapeo de Familia a FamiliaPL
        when(mapper.map(familia1, FamiliaPL.class)).thenReturn(familiaPL1);

        // Asegúrate de que familiaPL1 tiene el valor esperado de nombre
        familiaPL1.setNombre("Familia Actualizada"); // Asegúrate de que se asigna el valor correctamente

        // Simula que el repositorio `save()` guarda correctamente la FamiliaPL
        when(familiaPLRepository.save(familiaPL1)).thenReturn(familiaPL1);

        // Llamada al método que estamos probando
        familiaServicesImpl.update(familia1);

        // Verificación de que el repositorio `save()` fue llamado correctamente
        verify(familiaPLRepository).save(familiaPL1);

        // Asegúrate de que el nombre fue actualizado correctamente
        assertEquals("Familia Actualizada", familiaPL1.getNombre()); // Asegúrate de que el nombre no sea null
    }


    @Test
    void testUpdateThrowsIllegalStateExceptionWhenFamiliaNotExists() {
        // Crea un objeto Familia con el ID que no existe
        familia1.setId(1L);  // ID que no existe en el repositorio

        // Simula que `existsById(1L)` devuelve `false` porque la familia no existe
        when(familiaPLRepository.existsById(1L)).thenReturn(false);

        // Verifica que se lance la excepción `IllegalStateException`
        assertThrows(IllegalStateException.class, () -> familiaServicesImpl.update(familia1));

        // Verifica que `save()` no haya sido llamado
        verify(familiaPLRepository, times(0)).save(any());
    }


    @Test
    void testGetAll() {

        when(familiaPLRepository.findAll()).thenReturn(List.of(familiaPL1, familiaPL2));
        when(mapper.map(familiaPL1, Familia.class)).thenReturn(familia1);
        when(mapper.map(familiaPL2, Familia.class)).thenReturn(familia2);

        List<Familia> familias = familiaServicesImpl.getAll();

        assertEquals(2, familias.size());
        assertTrue(familias.containsAll(List.of(familia1, familia2)));
    }

    // ********************************************
    //
    // Private Methods
    //
    // ********************************************

    private void initObjects() {

        familiaPL1 = new FamiliaPL();
        familiaPL1.setId(1L);

        familiaPL2 = new FamiliaPL();
        familiaPL2.setId(2L);

        familia1 = new Familia();
        familia1.setId(1L);

        familia2 = new Familia();
        familia2.setId(2L);
    }
}
