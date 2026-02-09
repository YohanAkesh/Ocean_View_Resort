<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.app.model.Room" %>
<%@ page import="com.app.service.RoomService" %>
<%@ page import="java.util.List" %>

<%
    Object userObj = session.getAttribute("user");
    String role = (String) session.getAttribute("role");
    
    if (userObj == null || !"ADMIN".equals(role)) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
    
    RoomService roomService = new RoomService();
    List<Room> rooms = roomService.getAllRooms();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Rooms - Ocean View Resort</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
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
            max-width: 1200px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        
        .add-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .add-btn:hover {
            transform: translateY(-2px);
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
            display: none;
        }
        
        .success.show {
            display: block;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
            display: none;
        }
        
        .error.show {
            display: block;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 5px;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        th {
            background: #f8f9fa;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            border-bottom: 2px solid #ddd;
        }
        
        td {
            padding: 15px;
            border-bottom: 1px solid #ddd;
        }
        
        tr:hover {
            background: #f9f9f9;
        }
        
        .status-badge {
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .status-available {
            background: #d4edda;
            color: #155724;
        }
        
        .status-occupied {
            background: #fff3cd;
            color: #856404;
        }
        
        .status-maintenance {
            background: #f8d7da;
            color: #721c24;
        }
        
        .action-buttons {
            display: flex;
            gap: 10px;
        }
        
        .btn {
            padding: 8px 12px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 12px;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-delete {
            background: #f8d7da;
            color: #721c24;
        }
        
        .btn-delete:hover {
            background: #f5c6cb;
        }
        
        .no-rooms {
            text-align: center;
            padding: 40px;
            background: white;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>🏨 Ocean View Resort - Manage Rooms</h1>
        <div>
            <a href="dashboard.jsp">Dashboard</a>
            <a href="LogoutServlet">Logout</a>
        </div>
    </div>
    
    <div class="container">
        <%
            String success = request.getParameter("success");
            String error = request.getParameter("error");
            
            if (success != null && !success.isEmpty()) {
        %>
        <div class="success show">
            <strong>Success!</strong> <%= success %>
        </div>
        <%
            }
            if (error != null && !error.isEmpty()) {
        %>
        <div class="error show">
            <strong>Error!</strong> <%= error %>
        </div>
        <%
            }
        %>
        
        <div class="header">
            <h2>📋 Room Management</h2>
            <a href="add-room.jsp" class="add-btn">➕ Add New Room</a>
        </div>
        
        <%
            if (rooms.isEmpty()) {
        %>
        <div class="no-rooms">
            <p>No rooms found. <a href="add-room.jsp">Add a room</a></p>
        </div>
        <%
            } else {
        %>
        <table>
            <thead>
                <tr>
                    <th>Room #</th>
                    <th>Type</th>
                    <th>Capacity</th>
                    <th>Price/Night</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    for (Room room : rooms) {
                        String statusClass = "status-" + room.getStatus().toLowerCase();
                %>
                <tr>
                    <td><strong><%= room.getRoomNumber() %></strong></td>
                    <td><%= room.getRoomType() %></td>
                    <td><%= room.getCapacity() %> guests</td>
                    <td>$<%= String.format("%.2f", room.getPricePerNight()) %></td>
                    <td><span class="status-badge <%= statusClass %>"><%= room.getStatus() %></span></td>
                    <td>
                        <div class="action-buttons">
                            <form action="RoomManagementServlet" method="POST" style="display:inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="roomId" value="<%= room.getRoomId() %>">
                                <button type="submit" class="btn btn-delete" onclick="return confirm('Delete this room?')">Delete</button>
                            </form>
                        </div>
                    </td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        <%
            }
        %>
    </div>
</body>
</html>
