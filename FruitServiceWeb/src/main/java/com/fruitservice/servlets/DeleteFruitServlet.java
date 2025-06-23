package com.fruitservice.servlets;

import com.fruitservice.server.FruitComputeTaskRegistry;
import com.fruitservice.tasks.DeleteFruitPrice;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/deleteFruit")
public class DeleteFruitServlet extends HttpServlet {
    
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
            
            System.out.println("DeleteFruit request: fruitName=" + fruitName);
            
            if (fruitName == null || fruitName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing or empty fruitName parameter\"}");
                return;
            }
            
            DeleteFruitPrice task = new DeleteFruitPrice(fruitName.trim());
            String result = FruitComputeTaskRegistry.executeTask(task);
            
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"Failed to communicate with compute engine\"}");
                return;
            }
            
            out.println("{\"result\": \"" + escapeJson(result) + "\"}");
            System.out.println("DeleteFruit result: " + result);
            
        } catch (Exception e) {
            System.err.println("DeleteFruit error: " + e.getMessage());
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