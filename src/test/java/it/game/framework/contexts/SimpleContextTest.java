package it.game.framework.contexts;

import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
class SimpleContextTest {

    @Getter
    @ToString
    static class MapA {
        private Integer intVal;
        private Float floatVal;
    }

    @Getter
    @ToString
    static class MapB {
        private List<String> listVal;
        private List<List<String>> matrixVal;
        private MapA mapVal;
    }


    @Test
    void mapTo() {
        SimpleContext context = new SimpleContext();
        context.put("intVal", 1);
        context.put("floatVal", 3.14f);
        context.put("listVal", List.of("A", "B"));
        context.put("matrixVal", List.of(List.of("A", "B"), List.of("A", "B")));

        MapA popA = context.mapTo(new MapA());

        context.put("mapVal", popA);

        MapB popB = context.mapTo(new MapB());

        System.out.println(popA);
        System.out.println(popB);
    }

    @Test
    void contains() {
    }

    @Test
    void put() {
    }

    @Test
    void putAll() {
    }

    @Test
    void get() {
    }

    @Test
    void testGet() {
    }

    @Test
    void remove() {
    }

    @Test
    void cleanup() {
    }
}