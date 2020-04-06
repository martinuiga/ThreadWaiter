package com.fortumo.practical;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class FortumoApplicationTest {
    private FortumoApplication servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        servlet = new FortumoApplication();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testEndMessage() throws Exception {
        request.setContent("end".getBytes());
        servlet.doPost(request, response);
        assertEquals("0", response.getContentAsString());
    }

    @Test
    public void testInvalidMessage() throws Exception {
        request.setContent("2t".getBytes());
        servlet.doPost(request, response);
        assertEquals("Invalid input", response.getContentAsString());
    }

    @Test
    public void testAllSum() {
        try {
            new Thread(() -> {
                try {
                    request.setContent("2".getBytes());
                    servlet.doPost(request, response);
                    assertEquals("8", response.getContentAsString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "two").start();
            new Thread(() -> {
                try {
                    request.setContent("6".getBytes());
                    servlet.doPost(request, response);
                    assertEquals("8", response.getContentAsString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "six").start();
            new Thread(() -> {
                try {
                    request.setContent("end".getBytes());
                    servlet.doPost(request, response);
                    assertEquals("8", response.getContentAsString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "end").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}