package com.fruitservice.servlets;

import com.fruitservice.server.FruitComputeTaskRegistry;
import com.fruitservice.tasks.AddFruitPrice;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to handle adding new fruits
 * @WebServlet annotation maps this to the /addFruit URL
 */
@WebServlet("/addFruit")
public class AddFruitServlet extends HttpServlet {
    
    /**
     * Handle POST requests to add a fruit
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS for mobile app access
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Get parameters from request
            String fruitName = request.getParameter("fruitName");
            String priceStr = request.getParameter("price");
            
            System.out.println("AddFruit request: fruitName=" + fruitName + ", price=" + priceStr);
            
            // Validate input
            if (fruitName == null || fruitName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing or empty fruitName parameter\"}");
                return;
            }
            
            if (priceStr == null || priceStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing or empty price parameter\"}");
                return;
            }
            
            // Parse price
            double price = Double.parseDouble(priceStr);
            
            if (price <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Price must be greater than 0\"}");
                return;
            }
            
            // Create and execute task via RMI
            AddFruitPrice task = new AddFruitPrice(fruitName.trim(), price);
            String result = FruitComputeTaskRegistry.executeTask(task);
            
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"Failed to communicate with compute engine\"}");
                return;
            }
            
            // Return result as JSON
            out.println("{\"result\": \"" + escapeJson(result) + "\"}");
            System.out.println("AddFruit result: " + result);
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid price format. Please enter a valid number.\"}");
            
        } catch (Exception e) {
            System.err.println("AddFruit error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Internal server error: " + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    /**
     * Handle OPTIONS requests for CORS preflight
     */
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * Escape special characters for JSON
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}