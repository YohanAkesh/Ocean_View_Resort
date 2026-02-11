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
            background: linear-gradient(rgba(0, 40, 83, 0.6), rgba(0, 40, 83, 0.6)),
                        url('https://www.palaceresorts.com/all_inclusive_family_resort_palace_resorts_af8a736938.webp');
            background-size: cover;
            background-attachment: fixed;
            background-position: center;
            min-height: 100vh;
            overflow-x: hidden;
        }
        
        /* Top Header Bar */
        .top-header {
            background: linear-gradient(135deg, rgba(0, 40, 83, 0.98), rgba(0, 113, 188, 0.98));
            backdrop-filter: blur(20px);
            color: white;
            padding: 16px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
            position: sticky;
            top: 0;
            z-index: 100;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        .top-header .brand {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 20px;
            font-weight: 600;
        }
        
        .top-header .brand .logo {
            font-size: 28px;
        }
        
        .top-header .user-section {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .user-badge {
            display: flex;
            align-items: center;
            gap: 12px;
            background: rgba(255, 255, 255, 0.15);
            padding: 8px 16px;
            border-radius: 25px;
            backdrop-filter: blur(10px);
        }
        
        .user-badge .avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: linear-gradient(135deg, #69d2e7, #0071bc);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 16px;
        }
        
        .user-badge .info {
            display: flex;
            flex-direction: column;
        }
        
        .user-badge .name {
            font-weight: 600;
            font-size: 14px;
        }
        
        .user-badge .role {
            font-size: 11px;
            opacity: 0.8;
        }
        
        .logout-btn {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            border: 1px solid rgba(255, 255, 255, 0.3);
            padding: 8px 20px;
            border-radius: 20px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.3s;
            font-size: 14px;
        }
        
        .logout-btn:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateY(-1px);
        }
        
        /* Layout Container */
        .layout-container {
            display: flex;
            min-height: calc(100vh - 70px);
        }
        
        /* Sidebar Navigation */
        .sidebar {
            width: 280px;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
            padding: 30px 0;
            position: sticky;
            top: 70px;
            height: calc(100vh - 70px);
            overflow-y: auto;
        }
        
        .sidebar-section {
            margin-bottom: 30px;
        }
        
        .sidebar-title {
            padding: 0 25px 15px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            color: #666;
            letter-spacing: 1px;
        }
        
        .nav-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 16px 25px;
            color: #333;
            text-decoration: none;
            transition: all 0.3s;
            cursor: pointer;
            border-left: 3px solid transparent;
        }
        
        .nav-item:hover {
            background: linear-gradient(90deg, rgba(0, 113, 188, 0.1), transparent);
            border-left-color: #0071bc;
            color: #0071bc;
        }
        
        .nav-item .nav-icon {
            font-size: 24px;
            width: 30px;
            text-align: center;
        }
        
        .nav-item .nav-text {
            font-weight: 500;
            font-size: 15px;
        }
        
        .nav-item .nav-badge {
            margin-left: auto;
            background: #0071bc;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
        }
        
        /* Main Content Area */
        .main-content {
            flex: 1;
            padding: 40px;
        }
        
        .welcome-banner {
            background: linear-gradient(135deg, rgba(0, 40, 83, 0.95), rgba(0, 113, 188, 0.95));
            backdrop-filter: blur(20px);
            color: white;
            padding: 40px;
            border-radius: 20px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        
        .welcome-banner h1 {
            font-size: 32px;
            margin-bottom: 10px;
            font-weight: 600;
        }
        
        .welcome-banner p {
            font-size: 16px;
            opacity: 0.9;
        }
        
        /* Quick Actions Grid */
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .action-tile {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            cursor: pointer;
            transition: all 0.3s;
            border: 2px solid transparent;
            text-align: center;
        }
        
        .action-tile:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 30px rgba(0, 113, 188, 0.3);
            border-color: #0071bc;
        }
        
        .action-tile .tile-icon {
            font-size: 48px;
            margin-bottom: 15px;
            display: block;
        }
        
        .action-tile .tile-title {
            font-size: 18px;
            font-weight: 600;
            color: #002853;
            margin-bottom: 8px;
        }
        
        .action-tile .tile-desc {
            font-size: 13px;
            color: #666;
        }
        
        .action-tile.primary {
            background: linear-gradient(135deg, rgba(0, 113, 188, 0.95), rgba(105, 210, 231, 0.95));
            border-color: transparent;
            color: white;
        }
        
        .action-tile.primary .tile-title,
        .action-tile.primary .tile-desc {
            color: white;
        }
        
        .action-tile.primary:hover {
            background: linear-gradient(135deg, rgba(0, 113, 188, 1), rgba(105, 210, 231, 1));
            box-shadow: 0 8px 30px rgba(0, 113, 188, 0.5);
        }
        
        /* Info Cards */
        .info-section {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        
        .info-section h2 {
            font-size: 22px;
            color: #002853;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .feature-list {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
        }
        
        .feature-item {
            display: flex;
            align-items: flex-start;
            gap: 15px;
            padding: 20px;
            background: linear-gradient(135deg, rgba(0, 113, 188, 0.05), rgba(105, 210, 231, 0.05));
            border-radius: 12px;
            border-left: 4px solid #0071bc;
        }
        
        .feature-item .feature-icon {
            font-size: 32px;
        }
        
        .feature-item .feature-content h3 {
            font-size: 16px;
            color: #002853;
            margin-bottom: 5px;
        }
        
        .feature-item .feature-content p {
            font-size: 13px;
            color: #666;
        }
        
        .error {
            background: rgba(248, 215, 218, 0.95);
            color: #721c24;
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            border-left: 4px solid #dc3545;
        }
        
        .success {
            background: rgba(212, 237, 218, 0.95);
            color: #155724;
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            border-left: 4px solid #28a745;
        }
        
        @media (max-width: 768px) {
            .layout-container {
                flex-direction: column;
            }
            
            .sidebar {
                width: 100%;
                height: auto;
                position: relative;
            }
            
            .main-content {
                padding: 20px;
            }
        }
    </style>
</head>
<body>
    <!-- Top Header -->
    <div class="top-header">
        <div class="brand">
            <span class="logo">🏨</span>
            <span>Ocean View Resort</span>
        </div>
        <div class="user-section">
            <div class="user-badge">
                <div class="avatar"><%= fullName.substring(0, 1).toUpperCase() %></div>
                <div class="info">
                    <span class="name"><%= fullName %></span>
                    <span class="role"><%= role %> Portal</span>
                </div>
            </div>
            <form action="LogoutServlet" method="POST" style="display: inline;">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </div>
    
    <!-- Layout Container -->
    <div class="layout-container">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="sidebar-section">
                <div class="sidebar-title">Guest Management</div>
                <div class="nav-item" onclick="location.href='add-guest.jsp'">
                    <span class="nav-icon">👤</span>
                    <span class="nav-text">Register New Guest</span>
                </div>
                <div class="nav-item" onclick="location.href='manage-guests.jsp'">
                    <span class="nav-icon">📇</span>
                    <span class="nav-text">Manage Guests</span>
                </div>
            </div>
            
            <div class="sidebar-section">
                <div class="sidebar-title">Reservations</div>
                <div class="nav-item" onclick="location.href='add-reservation.jsp'">
                    <span class="nav-icon">📝</span>
                    <span class="nav-text">New Reservation</span>
                    <span class="nav-badge">New</span>
                </div>
                <div class="nav-item" onclick="location.href='staff-reservations.jsp'">
                    <span class="nav-icon">📋</span>
                    <span class="nav-text">Manage Reservations</span>
                </div>
                <div class="nav-item" onclick="location.href='staff-reservations.jsp?filter=active'">
                    <span class="nav-icon">🔍</span>
                    <span class="nav-text">Active Bookings</span>
                </div>
            </div>
            
            <div class="sidebar-section">
                <div class="sidebar-title">Billing</div>
                <div class="nav-item" onclick="location.href='generate-bills.jsp'">
                    <span class="nav-icon">💵</span>
                    <span class="nav-text">Generate Bills</span>
                </div>
            </div>
            
            <div class="sidebar-section">
                <div class="sidebar-title">Support</div>
                <div class="nav-item" onclick="location.href='help.jsp'">
                    <span class="nav-icon">❓</span>
                    <span class="nav-text">Need Help?</span>
                </div>
            </div>
        </aside>
        
        <!-- Main Content Area -->
        <main class="main-content">
            <%
                String error = request.getParameter("error");
                String success = request.getParameter("success");
                
                if (error != null && !error.isEmpty()) {
            %>
            <div class="error">
                <strong>⚠️ Error:</strong> <%= error %>
            </div>
            <%
                }
                if (success != null && !success.isEmpty()) {
            %>
            <div class="success">
                <strong>✓ Success:</strong> <%= success %>
            </div>
            <%
                }
            %>
            
            <!-- Welcome Banner -->
            <div class="welcome-banner">
                <h1>Welcome back, <%= fullName %>! 👋</h1>
                <p>Your staff portal for managing guests, reservations, and billing operations.</p>
            </div>
            
            <!-- Quick Actions -->
            <div class="quick-actions">
                <div class="action-tile primary" onclick="location.href='add-guest.jsp'">
                    <span class="tile-icon">👤</span>
                    <div class="tile-title">Register Guest</div>
                    <div class="tile-desc">Add new guest to system</div>
                </div>
                
                <div class="action-tile primary" onclick="location.href='add-reservation.jsp'">
                    <span class="tile-icon">📝</span>
                    <div class="tile-title">New Reservation</div>
                    <div class="tile-desc">Book a room quickly</div>
                </div>
                
                <div class="action-tile" onclick="location.href='staff-reservations.jsp'">
                    <span class="tile-icon">📋</span>
                    <div class="tile-title">All Reservations</div>
                    <div class="tile-desc">View and manage bookings</div>
                </div>
                
                <div class="action-tile" onclick="location.href='generate-bills.jsp'">
                    <span class="tile-icon">💵</span>
                    <div class="tile-title">Generate Bills</div>
                    <div class="tile-desc">Create PDF invoices</div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
