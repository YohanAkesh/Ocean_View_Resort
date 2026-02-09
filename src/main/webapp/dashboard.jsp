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
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Dashboard</title>
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
            color: #667eea;
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
        
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        
        .menu-card {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            text-align: center;
            text-decoration: none;
            color: inherit;
            transition: transform 0.3s, box-shadow 0.3s;
            cursor: pointer;
        }
        
        .menu-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }
        
        .menu-card h3 {
            font-size: 24px;
            margin-bottom: 10px;
        }
        
        .menu-card .icon {
            font-size: 48px;
            margin-bottom: 15px;
        }
        
        .menu-card p {
            color: #666;
            font-size: 14px;
        }
        
        .admin-section {
            border-top: 2px solid #667eea;
            margin-top: 20px;
            padding-top: 20px;
        }
        
        .admin-section h3 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 16px;
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
    </style>
</head>
<body>
    <div class="navbar">
        <h1>🏨 Ocean View Resort</h1>
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
            <strong>Success!</strong> Operation completed successfully.
        </div>
        <%
            }
        %>
        
        <div class="welcome">
            <h2>Welcome, <%= fullName %>! 👋</h2>
            <p>Manage room reservations and billing efficiently using the Ocean View Resort system.</p>
        </div>
        
        <div class="menu-grid">
            <div class="menu-card" onclick="location.href='#'">
                <div class="icon">📝</div>
                <h3>Add Reservation</h3>
                <p>Create a new guest reservation</p>
            </div>
            
            <div class="menu-card" onclick="location.href='#'">
                <div class="icon">🔍</div>
                <h3>View Reservations</h3>
                <p>View reservation details</p>
            </div>
            
            <div class="menu-card" onclick="location.href='#'">
                <div class="icon">💰</div>
                <h3>Calculate Bill</h3>
                <p>Generate and print bills</p>
            </div>
            
            <div class="menu-card" onclick="location.href='#'">
                <div class="icon">❓</div>
                <h3>Help</h3>
                <p>Get help using the system</p>
            </div>
            
            <%
                if ("ADMIN".equals(role)) {
            %>
            <div class="menu-card admin-section" style="grid-column: 1 / -1;">
                <h3>👨‍💼 ADMIN PANEL</h3>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 10px; margin-top: 15px;">
                    <div class="menu-card" onclick="location.href='register.jsp'" style="box-shadow: none; background: #f0f0f0;">
                        <div class="icon">👥</div>
                        <h3>Register Staff</h3>
                        <p>Add new staff members</p>
                    </div>
                    <div class="menu-card" onclick="location.href='manage-rooms.jsp'" style="box-shadow: none; background: #f0f0f0;">
                        <div class="icon">🛏️</div>
                        <h3>Manage Rooms</h3>
                        <p>Add and manage rooms</p>
                    </div>
                </div>
            </div>
            <%
                }
            %>
        </div>
    </div>
</body>
</html>
