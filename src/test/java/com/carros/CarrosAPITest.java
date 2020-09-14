package com.carros;

import com.carros.domain.Carro;
import com.carros.domain.carroDTO.CarroDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import static junit.framework.TestCase.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CarrosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarrosAPITest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    private ResponseEntity<CarroDTO> getCarro(String url){
        return testRestTemplate.withBasicAuth("user", "123").getForEntity(url, CarroDTO.class);
    }

    private  ResponseEntity<List<CarroDTO>> getCarros(String url){
        return testRestTemplate.withBasicAuth("user", "123").exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CarroDTO>>() {
                });
    }

    @Test
    public void testSave(){
        Carro carro = new Carro();
        carro.setNome("Porshe");
        carro.setTipo("esportivos");

        //insert
        ResponseEntity response = testRestTemplate.withBasicAuth("admin", "123").postForEntity("/api/v1/carros", carro, null);
        System.out.println(response);

        //verifica se criou
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        //busca o objeto criados
        String location = response.getHeaders().get("location").get(0);
        CarroDTO c = getCarro(location).getBody();

        assertNotNull(c);
        assertEquals("Porshe", c.getNome());
        assertEquals("esportivos", c.getTipo());

        //deleta o objeto
        testRestTemplate.withBasicAuth("admin", "123").delete(location);

        //verifica se deletou
        assertEquals(HttpStatus.NOT_FOUND, getCarro(location).getStatusCode());
    }

    @Test
    public void testLista(){
        List<CarroDTO> carros = getCarros("/api/v1/carros").getBody();
        assertNotNull(carros);
        assertEquals(30, carros.size());
    }

    @Test
    public void testListaPorTipo(){
        assertEquals(10, getCarros("/api/v1/carros/tipo/classicos").getBody().size());
        assertEquals(10, getCarros("/api/v1/carros/tipo/esportivos").getBody().size());
        assertEquals(10, getCarros("/api/v1/carros/tipo/luxo").getBody().size());

        assertEquals(HttpStatus.NO_CONTENT, getCarros("/api/v1/carros/tipo/xxx").getStatusCode());

    }

    @Test
    public void testGetOk(){
        ResponseEntity<CarroDTO> response = getCarro("/api/v1/carros/11");
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        CarroDTO carro = response.getBody();
        assertEquals("Ferrari FF", carro.getNome());
    }

    @Test
    public void testNotFound(){
        ResponseEntity responseEntity = getCarro("/api/v1/carros/100");

        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }
}
