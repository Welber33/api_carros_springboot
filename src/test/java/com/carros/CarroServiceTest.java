package com.carros;

import com.carros.api.exception.ObjectNotFoundException;
import com.carros.domain.Carro;
import com.carros.domain.CarroService;
import com.carros.domain.carroDTO.CarroDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static junit.framework.TestCase.*;


@RunWith(SpringRunner.class)
@SpringBootTest
class CarroServiceTest {

    @Autowired
    private CarroService service;

    @Test
    public void testSave(){
        Carro carro = new Carro();
        carro.setNome("Ferrari");
        carro.setTipo("Esportivo");

        CarroDTO c = service.insert(carro);

        assertNotNull(c);

        Long id = c.getId();
        assertNotNull(id);

        //buscar o objeto carroDTO
        c = service.getCarroById(id);
        assertNotNull(c);

        assertEquals("Ferrari", c.getNome());
        assertEquals("Esportivo", c.getTipo());

        //Deletar objeto
        service.delete(id);

        //verificar se deletou
        // Verificar se deletou
        try {
            service.getCarroById(id);
            fail("O carro não foi excluído");
        } catch (ObjectNotFoundException e) {
            // OK
        }

    }
    @Test
    public void testLista() {
        List<CarroDTO> carros = service.getCarros();

        assertEquals(30, carros.size());
    }

    @Test
    public void testGet(){

        CarroDTO c = service.getCarroById(11L);

        assertNotNull(c);

        assertEquals("Ferrari FF", c.getNome());

    }

    @Test
    public void testListaPorTipo(){

        assertEquals(10, service.getCarroByTipo("classicos").size());
        assertEquals(10, service.getCarroByTipo("esportivos").size());
        assertEquals(10, service.getCarroByTipo("luxo").size());

        assertEquals(0, service.getCarroByTipo("x").size());

    }

}
