package com.fortumo.practical;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

@WebServlet(name = "Servlet", urlPatterns = { "/" })
public class FortumoApplication extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(FortumoApplication.class);

    private static final String END_STRING = "end";

    private static int MAX_REQUESTS = 20;
    private static AtomicLong activeRequestsSumValue = new AtomicLong(0);  // The counter for the values.
    private static AtomicLong lastSumValue = new AtomicLong(0);  // Store the value after /end/ has arrived.

    @Override
    protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String inputValue = request.getReader().readLine();
        LOGGER.info("doPost(): inputValue={}", inputValue);

        if (StringUtils.isNumeric(inputValue) && MAX_REQUESTS > 0) {
            try {
                activeRequestsSumValue.getAndAdd(Long.parseLong(inputValue));
                MAX_REQUESTS -= 1;
                wait();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                response.getWriter().print("Invalid input");
                return;
            }
        } else if (END_STRING.equalsIgnoreCase(inputValue)) {
            notifyAll();
            lastSumValue.set(activeRequestsSumValue.get());
            activeRequestsSumValue.set(0);
            MAX_REQUESTS = 20;
            LOGGER.info("doPost(): END inserted, summary={}", lastSumValue.get());
        } else {
            response.getWriter().print("Invalid input");
            return;
        }
        response.getWriter().print(lastSumValue.get());
    }

    @Override
    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().append("Fortumo practical by Karl-Martin Uiga, created on 04.04.2020");
    }
}
