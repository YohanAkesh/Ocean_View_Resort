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
    <title>Ocean View Resort - Manage Reservations</title>
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
            background: #667eea;
            color: white;
            border: 2px solid white;
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
            display: flex;
            justify-content: space-between;
            align-items: center;
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
            padding: 8px 14px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 12px;
            margin-right: 5px;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-edit {
            background: linear-gradient(135deg, #ffc107 0%, #ffb300 100%);
            color: #000;
            box-shadow: 0 2px 8px rgba(255, 193, 7, 0.3);
        }
        
        .btn-edit:hover {
            background: linear-gradient(135deg, #ffca28 0%, #ffc107 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255, 193, 7, 0.4);
        }
        
        .btn-cancel {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(220, 53, 69, 0.3);
        }
        
        .btn-cancel:hover {
            background: linear-gradient(135deg, #e4606d 0%, #dc3545 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(220, 53, 69, 0.4);
        }
        
        .btn-status {
            background: linear-gradient(135deg, #28a745 0%, #218838 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(40, 167, 69, 0.3);
        }
        
        .btn-status:hover {
            background: linear-gradient(135deg, #34ce57 0%, #28a745 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
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
            color: rgba(255, 255, 255, 0.7);
            font-size: 16px;
        }
        
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 40, 83, 0.7);
            backdrop-filter: blur(5px);
        }
        
        .modal-content {
            background: rgba(255, 255, 255, 0.98);
            margin: 50px auto;
            padding: 40px;
            border-radius: 15px;
            max-width: 600px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }
        
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .modal-header h3 {
            color: #002853;
            font-weight: 700;
        }
        
        .close {
            font-size: 28px;
            font-weight: bold;
            color: #aaa;
            cursor: pointer;
        }
        
        .close:hover {
            color: #000;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #002853;
            font-weight: 600;
        }
        
        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s;
        }
        
        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #0071bc;
            box-shadow: 0 0 0 4px rgba(0, 113, 188, 0.15);
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }
        
        .btn-add {
            background: linear-gradient(135deg, #28a745 0%, #218838 100%);
            color: white;
            border: none;
            padding: 14px 28px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 700;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
        }
        
        .btn-add:hover {
            background: linear-gradient(135deg, #34ce57 0%, #28a745 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(40, 167, 69, 0.4);
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
            <h2>📋 Manage Reservations</h2>
            <a href="add-reservation.jsp" class="btn-add">+ New Reservation</a>
        </div>
        
        <div class="search-section">
            <form class="search-form" method="GET" action="staff-reservations.jsp">
                <input type="text" name="search" class="search-input" 
                       placeholder="Search by guest name or reservation number..." 
                       value="<%= searchTerm != null ? searchTerm : "" %>">
                <button type="submit" class="btn-search">Search</button>
                <button type="button" class="btn-clear" onclick="location.href='staff-reservations.jsp'">Clear</button>
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
                        <th>Actions</th>
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
                            <button class="action-btn btn-edit" 
                                    data-id="<%= reservation.getReservationId() %>"
                                    data-guest="<%= reservation.getGuestName().replace("\"", "&quot;") %>"
                                    data-address="<%= reservation.getAddress() != null ? reservation.getAddress().replace("\"", "&quot;") : "" %>"
                                    data-contact="<%= reservation.getContactNumber() %>"
                                    data-email="<%= reservation.getEmail() %>"
                                    data-checkin="<%= reservation.getCheckInDate() %>"
                                    data-checkout="<%= reservation.getCheckOutDate() %>"
                                    data-status="<%= reservation.getStatus() %>">
                                Edit
                            </button>
                            <% if (!"CANCELLED".equals(reservation.getStatus()) && !"COMPLETED".equals(reservation.getStatus())) { %>
                            <button class="action-btn btn-status" data-id="<%= reservation.getReservationId() %>">
                                Update Status
                            </button>
                            <button class="action-btn btn-cancel" data-id="<%= reservation.getReservationId() %>">
                                Cancel
                            </button>
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="no-data">
                <h3>No reservations found</h3>
                <p>Click "New Reservation" to create your first reservation.</p>
            </div>
            <% } %>
        </div>
    </div>
    
    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Edit Reservation</h3>
                <span class="close" onclick="closeEditModal()">&times;</span>
            </div>
            <form method="POST" action="ReservationServlet">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="reservationId" id="editReservationId">
                
                <div class="form-group">
                    <label for="editGuestName">Guest Name *</label>
                    <input type="text" id="editGuestName" name="guestName" required>
                </div>
                
                <div class="form-group">
                    <label for="editAddress">Address</label>
                    <textarea id="editAddress" name="address"></textarea>
                </div>
                
                <div class="form-group">
                    <label for="editContactNumber">Contact Number *</label>
                    <input type="text" id="editContactNumber" name="contactNumber" required>
                </div>
                
                <div class="form-group">
                    <label for="editEmail">Email *</label>
                    <input type="email" id="editEmail" name="email" required>
                </div>
                
                <div class="form-group">
                    <label for="editCheckInDate">Check-In Date *</label>
                    <input type="date" id="editCheckInDate" name="checkInDate" required>
                </div>
                
                <div class="form-group">
                    <label for="editCheckOutDate">Check-Out Date *</label>
                    <input type="date" id="editCheckOutDate" name="checkOutDate" required>
                </div>
                
                <div class="form-group">
                    <label for="editStatus">Status *</label>
                    <select id="editStatus" name="status" required>
                        <option value="ACTIVE">Active</option>
                        <option value="CONFIRMED">Confirmed</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="CANCELLED">Cancelled</option>
                    </select>
                </div>
                
                <button type="submit" class="btn-add" style="width: 100%;">Update Reservation</button>
            </form>
        </div>
    </div>
    
    <!-- Status Modal -->
    <div id="statusModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Update Reservation Status</h3>
                <span class="close" onclick="closeStatusModal()">&times;</span>
            </div>
            <form method="POST" action="ReservationServlet">
                <input type="hidden" name="action" value="updateStatus">
                <input type="hidden" name="reservationId" id="statusReservationId">
                
                <div class="form-group">
                    <label for="newStatus">Select New Status *</label>
                    <select id="newStatus" name="status" required>
                        <option value="ACTIVE">Active</option>
                        <option value="CONFIRMED">Confirmed</option>
                        <option value="COMPLETED">Completed</option>
                    </select>
                </div>
                
                <button type="submit" class="btn-add" style="width: 100%;">Update Status</button>
            </form>
        </div>
    </div>
    
    <script>
        // Add event listeners for edit buttons
        document.addEventListener('DOMContentLoaded', function() {
            // Edit buttons
            document.querySelectorAll('.btn-edit').forEach(function(button) {
                button.addEventListener('click', function() {
                    var id = this.getAttribute('data-id');
                    var guestName = this.getAttribute('data-guest');
                    var address = this.getAttribute('data-address');
                    var contact = this.getAttribute('data-contact');
                    var email = this.getAttribute('data-email');
                    var checkIn = this.getAttribute('data-checkin');
                    var checkOut = this.getAttribute('data-checkout');
                    var status = this.getAttribute('data-status');
                    editReservation(id, guestName, address, contact, email, checkIn, checkOut, status);
                });
            });
            
            // Status buttons
            document.querySelectorAll('.btn-status').forEach(function(button) {
                button.addEventListener('click', function() {
                    var id = this.getAttribute('data-id');
                    updateStatus(id);
                });
            });
            
            // Cancel buttons
            document.querySelectorAll('.btn-cancel').forEach(function(button) {
                button.addEventListener('click', function() {
                    var id = this.getAttribute('data-id');
                    cancelReservation(id);
                });
            });
        });
        
        function editReservation(id, guestName, address, contact, email, checkIn, checkOut, status) {
            document.getElementById('editReservationId').value = id;
            document.getElementById('editGuestName').value = guestName;
            document.getElementById('editAddress').value = address;
            document.getElementById('editContactNumber').value = contact;
            document.getElementById('editEmail').value = email;
            document.getElementById('editCheckInDate').value = checkIn;
            document.getElementById('editCheckOutDate').value = checkOut;
            document.getElementById('editStatus').value = status;
            document.getElementById('editModal').style.display = 'block';
        }
        
        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
        }
        
        function updateStatus(id) {
            document.getElementById('statusReservationId').value = id;
            document.getElementById('statusModal').style.display = 'block';
        }
        
        function closeStatusModal() {
            document.getElementById('statusModal').style.display = 'none';
        }
        
        function cancelReservation(id) {
            if (confirm('Are you sure you want to cancel this reservation?')) {
                var form = document.createElement('form');
                form.method = 'POST';
                form.action = 'ReservationServlet';
                
                var actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'cancel';
                form.appendChild(actionInput);
                
                var idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = 'reservationId';
                idInput.value = id;
                form.appendChild(idInput);
                
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        // Close modals when clicking outside
        window.onclick = function(event) {
            var editModal = document.getElementById('editModal');
            var statusModal = document.getElementById('statusModal');
            if (event.target == editModal) {
                editModal.style.display = 'none';
            }
            if (event.target == statusModal) {
                statusModal.style.display = 'none';
            }
        }
    </script>
</body>
</html>
