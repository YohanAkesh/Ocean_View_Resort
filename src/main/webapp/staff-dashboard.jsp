<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Check if user is logged in
    Object userObj = session.getAttribute("user");
    if (userObj == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String role = (String) session.getAttribute("role");
    String fullName = (String) session.getAttribute("username");
    
    // Redirect admins to their dashboard
    if ("ADMIN".equals(role)) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Staff Portal</title>
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
            background: linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%);
            color: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .navbar h1 {
            font-size: 24px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .user-details {
            text-align: right;
        }
        
        .user-details p {
            font-size: 14px;
        }
        
        .logout-btn {
            background: white;
            color: #2193b0;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: transform 0.2s;
        }
        
        .logout-btn:hover {
            transform: translateY(-2px);
        }
        
        .container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .welcome {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
        
        .welcome h2 {
            color: #333;
            margin-bottom: 10px;
        }
        
        .welcome p {
            color: #666;
            font-size: 16px;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .stat-card .icon {
            font-size: 40px;
            margin-bottom: 10px;
        }
        
        .stat-card h3 {
            color: #666;
            font-size: 14px;
            margin-bottom: 5px;
        }
        
        .stat-card .number {
            font-size: 32px;
            font-weight: bold;
            color: #2193b0;
        }
        
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
        }
        
        .menu-card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            text-align: center;
            text-decoration: none;
            color: inherit;
            transition: all 0.3s;
            cursor: pointer;
            border: 2px solid transparent;
        }
        
        .menu-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
            border-color: #2193b0;
        }
        
        .menu-card .icon {
            font-size: 64px;
            margin-bottom: 15px;
        }
        
        .menu-card h3 {
            font-size: 24px;
            margin-bottom: 10px;
            color: #333;
        }
        
        .menu-card p {
            color: #666;
            font-size: 14px;
        }
        
        .menu-card.primary {
            background: linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%);
            color: white;
        }
        
        .menu-card.primary h3,
        .menu-card.primary p {
            color: white;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
        }
        
        .section-title {
            background: white;
            padding: 20px 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        
        .section-title h3 {
            color: #2193b0;
            font-size: 20px;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>🏨 Ocean View Resort - Staff Portal</h1>
        <div class="user-info">
            <div class="user-details">
                <p><strong><%= fullName %></strong></p>
                <p>Role: <strong><%= role %></strong></p>
            </div>
            <form action="LogoutServlet" method="POST" style="display: inline;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>
    
    <div class="container">
        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");
            
            if (error != null && !error.isEmpty()) {
        %>
        <div class="error">
            <strong>Error!</strong> <%= error %>
        </div>
        <%
            }
            if (success != null && !success.isEmpty()) {
        %>
        <div class="success">
            <strong>Success!</strong> <%= success %>
        </div>
        <%
            }
        %>
        
        <div class="welcome">
            <h2>Welcome, <%= fullName %>! 👋</h2>
            <p>Manage guests and reservations efficiently with the Ocean View Resort Staff Portal.</p>
        </div>
        
        <div class="section-title">
            <h3>👥 Guest Management</h3>
        </div>
        
        <div class="menu-grid">
            <div class="menu-card primary" onclick="location.href='add-guest.jsp'">
                <div class="icon">👤</div>
                <h3>Register New Guest</h3>
                <p>Add a new guest to the system</p>
            </div>
            
            <div class="menu-card" onclick="location.href='manage-guests.jsp'">
                <div class="icon">📇</div>
                <h3>Manage Guests</h3>
                <p>View and update guest information</p>
            </div>
        </div>
        
        <div class="section-title">
            <h3>📊 Reservation Management</h3>
        </div>
        
        <div class="menu-grid">
            <div class="menu-card primary" onclick="location.href='add-reservation.jsp'">
                <div class="icon">📝</div>
                <h3>New Reservation</h3>
                <p>Book a room for a guest</p>
            </div>
            
            <div class="menu-card" onclick="location.href='staff-reservations.jsp'">
                <div class="icon">📋</div>
                <h3>Manage Reservations</h3>
                <p>View, edit, and manage bookings</p>
            </div>
            
            <div class="menu-card" onclick="location.href='staff-reservations.jsp?filter=active'">
                <div class="icon">🔍</div>
                <h3>Active Bookings</h3>
                <p>View current active reservations</p>
            </div>
            
            <div class="menu-card" onclick="location.href='staff-reservations.jsp'">
                <div class="icon">💰</div>
                <h3>Billing</h3>
                <p>Generate guest bills</p>
            </div>
        </div>
    </div>
</body>
</html>
