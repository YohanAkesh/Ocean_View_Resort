<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.app.model.Reservation" %>
<%@ page import="com.app.service.ReservationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    // Check if user is logged in
    Object userObj = session.getAttribute("user");
    if (userObj == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String role = (String) session.getAttribute("role");
    String fullName = (String) session.getAttribute("username");
    
    // Get reservations
    ReservationService reservationService = new ReservationService();
    List<Reservation> reservations = reservationService.getAllReservations();
    
    // Handle search
    String searchTerm = request.getParameter("search");
    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        reservations = reservationService.searchReservations(searchTerm);
    }
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Generate Bills</title>
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
            background: linear-gradient(135deg, rgba(0, 40, 83, 0.95) 0%, rgba(0, 113, 188, 0.95) 100%);
            color: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(10px);
            border-bottom: 2px solid rgba(255, 255, 255, 0.1);
        }
        
        .navbar h1 {
            font-size: 24px;
            cursor: pointer;
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
        
        .btn {
            background: white;
            color: #667eea;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: transform 0.2s;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn:hover {
            transform: translateY(-2px);
        }
        
        .btn-secondary {
            background: rgba(0, 113, 188, 0.95);
            color: white;
            border: 2px solid rgba(255, 255, 255, 0.8);
        }
        
        .container {
            max-width: 1400px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .header-section {
            background: rgba(255, 255, 255, 0.95);
            padding: 35px;
            border-radius: 15px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
            margin-bottom: 30px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        
        .header-section h2 {
            color: #002853;
            font-weight: 700;
        }
        
        .search-section {
            background: rgba(255, 255, 255, 0.95);
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
            margin-bottom: 30px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        
        .search-form {
            display: flex;
            gap: 10px;
        }
        
        .search-input {
            flex: 1;
            padding: 14px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s;
        }
        
        .search-input:focus {
            outline: none;
            border-color: #0071bc;
            box-shadow: 0 0 0 4px rgba(0, 113, 188, 0.15);
        }
        
        .btn-search {
            background: linear-gradient(135deg, #002853 0%, #0071bc 100%);
            color: white;
            border: none;
            padding: 14px 35px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 700;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(0, 40, 83, 0.3);
        }
        
        .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0, 40, 83, 0.4);
        }
        
        .btn-clear {
            background: rgba(255, 255, 255, 0.5);
            color: #002853;
            border: 2px solid #e0e0e0;
            padding: 12px 25px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-clear:hover {
            background: rgba(255, 255, 255, 0.8);
            border-color: #0071bc;
        }
        
        .table-container {
            background: rgba(255, 255, 255, 0.95);
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
            overflow-x: auto;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            background: linear-gradient(135deg, #002853 0%, #0071bc 100%);
            color: white;
            font-weight: 600;
            position: sticky;
            top: 0;
        }
        
        tr:hover {
            background: rgba(0, 113, 188, 0.05);
        }
        
        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            display: inline-block;
        }
        
        .status-active {
            background: #d4edda;
            color: #155724;
        }
        
        .status-confirmed {
            background: #cce5ff;
            color: #004085;
        }
        
        .status-completed {
            background: #d1ecf1;
            color: #0c5460;
        }
        
        .status-cancelled {
            background: #f8d7da;
            color: #721c24;
        }
        
        .action-btn {
            padding: 8px 15px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
            margin-right: 5px;
        }
        
        .btn-bill {
            background: linear-gradient(135deg, #0071bc 0%, #69d2e7 100%);
            color: white;
            padding: 10px 18px;
            font-weight: 600;
            transition: all 0.3s;
            box-shadow: 0 4px 10px rgba(0, 113, 188, 0.3);
        }
        
        .btn-bill:hover {
            background: linear-gradient(135deg, #0088dd 0%, #7dd9ed 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(0, 113, 188, 0.4);
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
        
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1 onclick="location.href='staff-dashboard.jsp'">🏨 Ocean View Resort - Staff Portal</h1>
        <div class="user-info">
            <div class="user-details">
                <p><strong><%= fullName %></strong></p>
                <p>Role: <strong><%= role %></strong></p>
            </div>
            <a href="staff-dashboard.jsp" class="btn btn-secondary">Dashboard</a>
            <form action="LogoutServlet" method="POST" style="display: inline;">
                <button type="submit" class="btn">Logout</button>
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
        
        <div class="header-section">
            <h2>📄 Generate Bills</h2>
            <p style="margin-top: 10px; color: #666;">Select a reservation to generate and download a PDF bill</p>
        </div>
        
        <div class="search-section">
            <form class="search-form" method="GET" action="generate-bills.jsp">
                <input type="text" name="search" class="search-input" 
                       placeholder="Search by guest name or reservation number..." 
                       value="<%= searchTerm != null ? searchTerm : "" %>">
                <button type="submit" class="btn-search">Search</button>
                <button type="button" class="btn-clear" onclick="location.href='generate-bills.jsp'">Clear</button>
            </form>
        </div>
        
        <div class="table-container">
            <% if (reservations != null && !reservations.isEmpty()) { %>
            <table>
                <thead>
                    <tr>
                        <th>Reservation #</th>
                        <th>Guest Name</th>
                        <th>Contact</th>
                        <th>Room</th>
                        <th>Check-In</th>
                        <th>Check-Out</th>
                        <th>Nights</th>
                        <th>Total Cost</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    for (Reservation reservation : reservations) {
                        String statusClass = "status-" + reservation.getStatus().toLowerCase();
                        String roomNumber = reservationService.getRoomNumber(reservation.getRoomId());
                    %>
                    <tr>
                        <td><strong><%= reservation.getReservationNumber() %></strong></td>
                        <td><%= reservation.getGuestName() %></td>
                        <td><%= reservation.getContactNumber() %></td>
                        <td><%= roomNumber %></td>
                        <td><%= reservation.getCheckInDate().format(formatter) %></td>
                        <td><%= reservation.getCheckOutDate().format(formatter) %></td>
                        <td><%= reservation.getNumberOfNights() %></td>
                        <td>$<%= String.format("%.2f", reservation.getTotalCost()) %></td>
                        <td><span class="status-badge <%= statusClass %>"><%= reservation.getStatus() %></span></td>
                        <td>
                            <button class="action-btn btn-bill" 
                                    data-id="<%= reservation.getReservationId() %>"
                                    title="Generate and Download Bill">
                                📄 Generate Bill
                            </button>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="no-data">
                <h3>No reservations found</h3>
                <p>There are no reservations available for billing at the moment.</p>
            </div>
            <% } %>
        </div>
    </div>
    
    <script>
        // Function to generate and download bill
        function generateBill(reservationId) {
            window.location.href = 'GenerateBillServlet?reservationId=' + reservationId;
        }
        
        // Add event listeners for bill buttons
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.btn-bill').forEach(function(button) {
                button.addEventListener('click', function() {
                    var id = this.getAttribute('data-id');
                    generateBill(id);
                });
            });
        });
    </script>
</body>
</html>
