package ch.epfl.javass.net;

import org.junit.jupiter.api.Test;

import static ch.epfl.javass.net.StringSerializer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringSerializerTest {


    @Test
    void serializeIntWorks() {
//        for (int i = 0; i < 3; i++) {
//
//        }
        assertEquals("0", serializeInt(0));
        assertEquals("1", serializeInt(1));
        assertEquals("99df", serializeInt(39391));
        assertEquals("a", serializeInt(10));
        assertEquals("3d", serializeInt(61));
    }

    @Test
    void deserializeIntWorks() {
        assertEquals(10, deserializeInt("a"));
        assertEquals(16, deserializeInt("10"));
        assertEquals(61, deserializeInt("3d"));
        assertEquals(27052, deserializeInt("69ac"));
    }

    @Test
    void serializeStringWorks() {
        assertEquals("QW3DqWxpZQ==", serializeString("Amélie"));
        assertEquals("R2HDq2xsZQ==", serializeString("Gaëlle"));
        assertEquals("w4ltaWxl", serializeString("Émile"));
        assertEquals("TmFkw6hnZQ==", serializeString("Nadège"));
    }

    @Test
    void deserializeStringWorks() {
        assertEquals("Amélie", deserializeString("QW3DqWxpZQ=="));
        assertEquals("Gaëlle", deserializeString("R2HDq2xsZQ=="));
        assertEquals("Émile", deserializeString("w4ltaWxl"));
        assertEquals("Nadège", deserializeString("TmFkw6hnZQ=="));
    }

    @Test
    void combineWorks() {
        assertEquals("bonjour comment ça va", combine(" ",
                "bonjour", "comment", "ça", "va"));

        assertEquals("bonjour,comment,ça,va", combine(",",
                "bonjour", "comment", "ça", "va"));
    }

    @Test
    void splitWorks() {
        String[] expected = new String[] {"Bonjour", "comment", "ça", "va", "aujourd'hui"};
        String[] actual = split(" ", "Bonjour comment ça va aujourd'hui");
        for (int i = 0; i < expected.length; ++i) {
            assertEquals(expected[i], actual[i]);
        }
    }

}
