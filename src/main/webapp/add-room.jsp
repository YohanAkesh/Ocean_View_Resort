<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Object userObj = session.getAttribute("user");
    String role = (String) session.getAttribute("role");
    
    if (userObj == null || !"ADMIN".equals(role)) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Room - Ocean View Resort</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(rgba(0, 40, 83, 0.6), rgba(0, 40, 83, 0.6)),
                        url('https://www.palaceresorts.com/all_inclusive_family_resort_palace_resorts_af8a736938.webp');
            background-size: cover;
            background-attachment: fixed;
            background-position: center;
            min-height: 100vh;
        }
        
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .navbar a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
            cursor: pointer;
        }
        
        .navbar a:hover {
            text-decoration: underline;
        }
        
        .container {
            max-width: 600px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .form-box {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .form-box h2 {
            color: #333;
            margin-bottom: 25px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }
        
        input[type="text"],
        input[type="number"],
        select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        input[type="text"]:focus,
        input[type="number"]:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        
        button {
            flex: 1;
            padding: 12px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 5px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        button:hover {
            transform: translateY(-2px);
        }
        
        .cancel-btn {
            background: #6c757d;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            display: none;
        }
        
        .error.show {
            display: block;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>🏨 Ocean View Resort - Add Room</h1>
        <div>
            <a href="dashboard.jsp">Dashboard</a>
            <a href="manage-rooms.jsp">View Rooms</a>
            <a href="LogoutServlet">Logout</a>
        </div>
    </div>
    
    <div class="container">
        <%
            String error = request.getParameter("error");
            if (error != null && !error.isEmpty()) {
        %>
        <div class="error show">
            <strong>Error!</strong> <%= error %>
        </div>
        <%
            }
        %>
        
        <div class="form-box">
            <h2>➕ Add New Room</h2>
            
            <form action="RoomManagementServlet" method="POST" onsubmit="return validateForm()">
                <input type="hidden" name="action" value="add">
                
                <div class="form-group">
                    <label for="roomNumber">Room Number *</label>
                    <input type="text" id="roomNumber" name="roomNumber" required placeholder="e.g., 101">
                </div>
                
                <div class="form-group">
                    <label for="roomType">Room Type *</label>
                    <select id="roomType" name="roomType" required>
                        <option value="">Select Room Type</option>
                        <option value="Single">Single</option>
                        <option value="Double">Double</option>
                        <option value="Suite">Suite</option>
                        <option value="Deluxe">Deluxe</option>
                        <option value="Family">Family</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="capacity">Capacity (guests) *</label>
                    <input type="number" id="capacity" name="capacity" required min="1" placeholder="e.g., 2">
                </div>
                
                <div class="form-group">
                    <label for="price">Price per Night ($) *</label>
                    <input type="number" id="price" name="price" required min="0.01" step="0.01" placeholder="e.g., 99.99">
                </div>
                
                <div class="button-group">
                    <button type="submit">Add Room</button>
                    <button type="button" class="cancel-btn" onclick="window.location.href='manage-rooms.jsp'">Cancel</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        function validateForm() {
            var roomNumber = document.getElementById("roomNumber").value.trim();
            var roomType = document.getElementById("roomType").value;
            var capacity = document.getElementById("capacity").value;
            var price = document.getElementById("price").value;
            
            if (!roomNumber) {
                alert("Room number is required");
                return false;
            }
            
            if (!roomType) {
                alert("Room type is required");
                return false;
            }
            
            if (!capacity || capacity <= 0) {
                alert("Capacity must be a positive number");
                return false;
            }
            
            if (!price || price <= 0) {
                alert("Price must be a positive number");
                return false;
            }
            
            return true;
        }
    </script>
</body>
</html>
