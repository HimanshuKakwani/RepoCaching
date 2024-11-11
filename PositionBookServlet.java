//package ;
package caching;

import javax.servlet.*;


import javax.servlet.http.*;
import java.io.IOException;
import org.json.JSONObject;

public class PositionBookServlet extends HttpServlet {
    private PositionBookGenerator.LRUCache<String, JSONObject> cache;

    @Override
    public void init() {
        cache = new PositionBookGenerator.LRUCache<>(10000);
        // Pre-load cache with sample data if necessary
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String customerId = request.getParameter("customerId");

        if (customerId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required");
            return;
        }

        // Measure time taken for cache retrieval
        long startTime = System.nanoTime();
        JSONObject userJson = cache.get("user_" + customerId);
        long elapsedTime = System.nanoTime() - startTime;

        if (userJson == null) {
            response.getWriter().println("Customer data not found in cache.");
        } else {
            request.setAttribute("userJson", userJson);
            request.setAttribute("elapsedTime", elapsedTime);
            RequestDispatcher dispatcher = request.getRequestDispatcher("positionBook.jsp");
            dispatcher.forward(request, response);
        }
    }
}

