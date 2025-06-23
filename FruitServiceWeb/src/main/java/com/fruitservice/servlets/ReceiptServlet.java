package com.fruitservice.servlets;

import com.fruitservice.model.PurchaseItem;
import com.fruitservice.server.FruitComputeTaskRegistry;
import com.fruitservice.tasks.CalculateCost;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/generateReceipt")
public class ReceiptServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Get arrays of fruit names and quantities
            String[] fruitNames = request.getParameterValues("fruitName");
            String[] quantities = request.getParameterValues("quantity");
            String amountPaidStr = request.getParameter("amountPaid");
            String cashierName = request.getParameter("cashierName");
            
            System.out.println("GenerateReceipt request:");
            System.out.println("  fruitNames: " + java.util.Arrays.toString(fruitNames));
            System.out.println("  quantities: " + java.util.Arrays.toString(quantities));
            System.out.println("  amountPaid: " + amountPaidStr);
            System.out.println("  cashierName: " + cashierName);
            
            // Validate required parameters
            if (fruitNames == null || quantities == null || amountPaidStr == null || cashierName == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing required parameters: fruitName, quantity, amountPaid, cashierName\"}");
                return;
            }
            
            if (fruitNames.length != quantities.length) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Fruit names and quantities arrays must have same length\"}");
                return;
            }
            
            if (fruitNames.length == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"At least one item is required\"}");
                return;
            }
            
            if (cashierName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Cashier name cannot be empty\"}");
                return;
            }
            
            // Parse amount paid
            double amountPaid = Double.parseDouble(amountPaidStr);
            
            if (amountPaid < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Amount paid cannot be negative\"}");
                return;
            }
            
            // Build list of purchase items
            List<PurchaseItem> items = new ArrayList<>();
            for (int i = 0; i < fruitNames.length; i++) {
                String fruitName = fruitNames[i];
                String quantityStr = quantities[i];
                
                if (fruitName != null && !fruitName.trim().isEmpty() && 
                    quantityStr != null && !quantityStr.trim().isEmpty()) {
                    
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0) {
                            items.add(new PurchaseItem(fruitName.trim(), quantity));
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.println("{\"error\": \"Invalid quantity: " + quantityStr + "\"}");
                        return;
                    }
                }
            }
            
            if (items.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"No valid items found\"}");
                return;
            }
            
            // Execute receipt generation task
            CalculateCost task = new CalculateCost(items, amountPaid, cashierName.trim());
            String result = FruitComputeTaskRegistry.executeTask(task);
            
            if (result == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"error\": \"Failed to communicate with compute engine\"}");
                return;
            }
            
            out.println("{\"receipt\": \"" + escapeJson(result) + "\"}");
            System.out.println("GenerateReceipt completed successfully");
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid number format: " + e.getMessage() + "\"}");
            
        } catch (Exception e) {
            System.err.println("GenerateReceipt error: " + e.getMessage());
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