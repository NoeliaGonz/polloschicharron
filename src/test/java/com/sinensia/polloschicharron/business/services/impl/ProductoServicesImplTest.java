package com.sinensia.polloschicharron.business.services.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sinensia.polloschicharron.business.model.Familia;
import com.sinensia.polloschicharron.business.model.Producto;
import com.sinensia.polloschicharron.business.model.dtos.ProductoDTO1;
import com.sinensia.polloschicharron.integration.repositories.ProductoPLRepository;
import com.sinensia.polloschicharron.integration.model.ProductoPL;
import org.dozer.DozerBeanMapper;

@ExtendWith(MockitoExtension.class)
class ProductoServicesImplTest {

    @Mock
    private ProductoPLRepository productoPLRepository;

    @Mock
    private DozerBeanMapper mapper;

    @InjectMocks
    private ProductoServicesImpl productoServicesImpl;

    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear objetos Producto para las pruebas
        producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Producto A");
        producto1.setPrecio(15.0);
        producto1.setFamilia(new Familia());  // Simular una familia, puedes mockearla si es necesario
        producto1.setFechaAlta(new Date());
        producto1.setDescripcion("Descripción de Producto A");

        producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Producto B");
        producto2.setPrecio(20.0);
        producto2.setFamilia(new Familia());  // Similar a lo anterior
        producto2.setFechaAlta(new Date());
        producto2.setDescripcion("Descripción de Producto B");
    }

    @Test
    void testCreate() {
        // Asegúrate de que el ID del producto sea nulo antes de crear el producto
        producto1.setId(null);

        // Crear un ProductoPL con un ID simulado
        ProductoPL productoPLMock = new ProductoPL();
        productoPLMock.setId(1L);  // Asignamos un ID válido al ProductoPL simulado

        // Mockear el comportamiento del mapper y repositorio
        when(mapper.map(producto1, ProductoPL.class)).thenReturn(productoPLMock);
        when(productoPLRepository.save(any(ProductoPL.class))).thenReturn(productoPLMock);

        // Ejecutar el método create
        Long productoId = productoServicesImpl.create(producto1);

        // Verificar que el repositorio fue llamado con el objeto adecuado
        verify(productoPLRepository).save(any(ProductoPL.class));
        assertNotNull(productoId); // Verificamos que el ID no sea nulo
        assertEquals(1L, productoId); // Verificamos que el ID devuelto sea el esperado
    }

    @Test
    void testUpdate() {
        // Simular que el producto existe
        when(productoPLRepository.existsById(1L)).thenReturn(true);
        when(mapper.map(producto1, ProductoPL.class)).thenReturn(new ProductoPL());

        // Ejecutar el método update
        productoServicesImpl.update(producto1);

        // Verificar que el repositorio fue llamado para guardar el producto
        verify(productoPLRepository).save(any(ProductoPL.class));
    }

    @Test
    void testUpdateProductoNoExistente() {
        
        // Verificar que el método lance la excepción
        assertThrows(IllegalStateException.class, () -> productoServicesImpl.update(producto2));
    }


    @Test
    void testDelete() {
        // Simular que el producto existe
        when(productoPLRepository.existsById(1L)).thenReturn(true);
        when(productoPLRepository.findById(1L)).thenReturn(Optional.of(new ProductoPL()));

        // Ejecutar el método delete
        productoServicesImpl.delete(1L);

        // Verificar que el producto fue marcado como descatalogado
        verify(productoPLRepository).save(any(ProductoPL.class));
    }

    @Test
    void testDeleteProductoNoExistente() {
        // Simular que el producto no existe
        when(productoPLRepository.existsById(1L)).thenReturn(false);

        // Verificar que el método lance la excepción
        assertThrows(IllegalStateException.class, () -> productoServicesImpl.delete(1L));
    }

    @Test
    void testGetProductosDTO1() {
        // Crear los resultados simulados de la consulta en el repositorio
        Object[] fila1 = { "Producto A", "Familia 1", 15.0 };
        Object[] fila2 = { "Producto B", "Familia 2", 20.0 };
        List<Object[]> resultadosSimulados = List.of(fila1, fila2);

        // Simular el comportamiento del repositorio
        when(productoPLRepository.findDTO1()).thenReturn(resultadosSimulados);

        // Llamar al servicio para obtener los productos como DTOs
        List<ProductoDTO1> productosDTO1 = productoServicesImpl.getProductosDTO1();

        // Verificar que la lista devuelta tenga 2 elementos
        assertEquals(2, productosDTO1.size());

        // Verificar que los DTOs devueltos contienen los productos correctos
        ProductoDTO1 productoDTO11 = new ProductoDTO1();
        productoDTO11.setNombre("Producto A");
        productoDTO11.setFamilia("Familia 1");
        productoDTO11.setPrecio(15.0);

        ProductoDTO1 productoDTO12 = new ProductoDTO1();
        productoDTO12.setNombre("Producto B");
        productoDTO12.setFamilia("Familia 2");
        productoDTO12.setPrecio(20.0);

        assertTrue(productosDTO1.contains(productoDTO11));
        assertTrue(productosDTO1.contains(productoDTO12));
    }

    @Test
    void testGetAll() {
        // Simular que el repositorio devuelve productos
        when(productoPLRepository.findAll()).thenReturn(List.of(new ProductoPL(), new ProductoPL()));

        // Llamar al servicio para obtener todos los productos
        List<Producto> productos = productoServicesImpl.getAll();

        // Verificar que la lista de productos no esté vacía
        assertNotNull(productos);
        assertEquals(2, productos.size());
    }

    @Test
    void testGetNumeroTotalProductos() {
        // Mockear el comportamiento del count() del repositorio
        when(productoPLRepository.count()).thenReturn(5L);

        // Llamar al servicio para obtener el número total de productos
        int totalProductos = productoServicesImpl.getNumeroTotalProductos();

        // Verificar que el número total de productos es el esperado
        assertEquals(5, totalProductos);

        // Verificar que el método count() fue llamado
        verify(productoPLRepository).count();
    }



}
