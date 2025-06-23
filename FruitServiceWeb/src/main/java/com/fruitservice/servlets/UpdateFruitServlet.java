package com.fruitservice.servlets;

import com.fruitservice.server.FruitComputeTaskRegistry;
import com.fruitservice.tasks.UpdateFruitPrice;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/updateFruit")
public class UpdateFruitServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = response.getWriter();
        
        try {
            String fruitName = request.getParameter("fruitName");
            String priceStr = request.getParameter("newPrice");
            
            System.out.println("UpdateFruit request: fruitName=" + fruitName + ", newPrice=" + priceStr);
            
            if (fruitName == null || fruitName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing or empty fruitName parameter\"}");
                return;
            }
            
            if (priceStr == null || priceStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing or empty newPrice parameter\"}");
                return;
            }
            
            double newPrice = Double.parseDouble(priceStr);
            
            if (newPrice <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Price must be greater than 0\"}");
                return;
            }
            
            UpdateFruitPrice task = new UpdateFruitPrice(fruitName.trim(), newPrice);
            String result = FruitComputeTaskRegistry.executeTask(task);
            
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"Failed to communicate with compute engine\"}");
                return;
            }
            
            out.println("{\"result\": \"" + escapeJson(result) + "\"}");
            System.out.println("UpdateFruit result: " + result);
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid price format. Please enter a valid number.\"}");
            
        } catch (Exception e) {
            System.err.println("UpdateFruit error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Internal server error: " + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}