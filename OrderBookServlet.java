package caching;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/OrderBookServlet")
public class OrderBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String customerId = request.getParameter("customerId");
        
        // Retrieve the cache from CustOrderProcessor
        Map<String, JSONObject> cache = CustOrderProcessor.cache;
        JSONObject usersJson = cache.get("user_data");

        if (usersJson == null || !usersJson.has("user_" + customerId)) {
            request.setAttribute("error", "Customer data not found in cache.");
        } else {
            JSONObject customerData = usersJson.getJSONObject("user_" + customerId);
            request.setAttribute("customerData", customerData);
        }

        // Forward data to JSP for rendering
        request.getRequestDispatcher("orderBook.jsp").forward(request, response);
    }
}
