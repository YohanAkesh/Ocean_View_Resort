<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.app.model.Guest" %>
<%@ page import="com.app.service.GuestService" %>
<%@ page import="java.util.List" %>
<%
    // Check if user is logged in
    Object userObj = session.getAttribute("user");
    if (userObj == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String role = (String) session.getAttribute("role");
    String fullName = (String) session.getAttribute("username");
    
    // Get guests
    GuestService guestService = new GuestService();
    List<Guest> guests = guestService.getAllGuests();
    
    // Handle search
    String searchTerm = request.getParameter("search");
    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        guests = guestService.searchGuests(searchTerm);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Manage Guests</title>
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
            color: #2193b0;
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
            background: #2193b0;
            color: white;
            border: 2px solid white;
        }
        
        .container {
            max-width: 1400px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .header-section {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .header-section h2 {
            color: #333;
        }
        
        .search-section {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
        
        .search-form {
            display: flex;
            gap: 10px;
        }
        
        .search-input {
            flex: 1;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        .btn-search {
            background: #2193b0;
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
        }
        
        .btn-clear {
            background: #f5f5f5;
            color: #666;
            border: none;
            padding: 12px 20px;
            border-radius: 5px;
            cursor: pointer;
        }
        
        .table-container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
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
            background: #2193b0;
            color: white;
            font-weight: 600;
            position: sticky;
            top: 0;
        }
        
        tr:hover {
            background: #f9f9f9;
        }
        
        .action-btn {
            padding: 5px 10px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
            margin-right: 5px;
        }
        
        .btn-edit {
            background: #ffc107;
            color: #000;
        }
        
        .btn-delete {
            background: #dc3545;
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
        
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
        }
        
        .modal-content {
            background: white;
            margin: 50px auto;
            padding: 30px;
            border-radius: 10px;
            max-width: 600px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            max-height: 80vh;
            overflow-y: auto;
        }
        
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .modal-header h3 {
            color: #333;
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
            margin-bottom: 5px;
            color: #333;
            font-weight: 600;
        }
        
        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }
        
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        
        .btn-add {
            background: #28a745;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            text-decoration: none;
            display: inline-block;
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
            <h2>👥 Manage Guests</h2>
            <a href="add-guest.jsp" class="btn-add">+ Register New Guest</a>
        </div>
        
        <div class="search-section">
            <form class="search-form" method="GET" action="manage-guests.jsp">
                <input type="text" name="search" class="search-input" 
                       placeholder="Search by name, email, phone, or ID number..." 
                       value="<%= searchTerm != null ? searchTerm : "" %>">
                <button type="submit" class="btn-search">Search</button>
                <button type="button" class="btn-clear" onclick="location.href='manage-guests.jsp'">Clear</button>
            </form>
        </div>
        
        <div class="table-container">
            <% if (guests != null && !guests.isEmpty()) { %>
            <table>
                <thead>
                    <tr>
                        <th>Guest ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>ID Type</th>
                        <th>ID Number</th>
                        <th>Nationality</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Guest guest : guests) { %>
                    <tr>
                        <td>#<%= guest.getGuestId() %></td>
                        <td><strong><%= guest.getFullName() %></strong></td>
                        <td><%= guest.getEmail() %></td>
                        <td><%= guest.getPhoneNumber() %></td>
                        <td><%= guest.getIdType() %></td>
                        <td><%= guest.getIdNumber() %></td>
                        <td><%= guest.getNationality() != null ? guest.getNationality() : "-" %></td>
                        <td>
                            <button class="action-btn btn-edit" 
                                    data-id="<%= guest.getGuestId() %>"
                                    data-firstname="<%= guest.getFirstName() %>"
                                    data-lastname="<%= guest.getLastName() %>"
                                    data-email="<%= guest.getEmail() %>"
                                    data-phone="<%= guest.getPhoneNumber() %>"
                                    data-address="<%= guest.getAddress() != null ? guest.getAddress().replace("\"", "&quot;") : "" %>"
                                    data-idtype="<%= guest.getIdType() %>"
                                    data-idnumber="<%= guest.getIdNumber() %>"
                                    data-nationality="<%= guest.getNationality() != null ? guest.getNationality() : "" %>">
                                Edit
                            </button>
                            <button class="action-btn btn-delete" data-id="<%= guest.getGuestId() %>">
                                Delete
                            </button>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="no-data">
                <h3>No guests found</h3>
                <p>Click "Register New Guest" to add your first guest.</p>
            </div>
            <% } %>
        </div>
    </div>
    
    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Edit Guest Information</h3>
                <span class="close" onclick="closeEditModal()">&times;</span>
            </div>
            <form method="POST" action="GuestServlet">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="guestId" id="editGuestId">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="editFirstName">First Name *</label>
                        <input type="text" id="editFirstName" name="firstName" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="editLastName">Last Name *</label>
                        <input type="text" id="editLastName" name="lastName" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="editEmail">Email *</label>
                        <input type="email" id="editEmail" name="email" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="editPhoneNumber">Phone Number *</label>
                        <input type="text" id="editPhoneNumber" name="phoneNumber" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="editAddress">Address</label>
                    <textarea id="editAddress" name="address"></textarea>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="editIdType">ID Type *</label>
                        <select id="editIdType" name="idType" required>
                            <option value="PASSPORT">Passport</option>
                            <option value="DRIVERS_LICENSE">Driver's License</option>
                            <option value="NATIONAL_ID">National ID Card</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="editIdNumber">ID Number *</label>
                        <input type="text" id="editIdNumber" name="idNumber" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="editNationality">Nationality</label>
                    <input type="text" id="editNationality" name="nationality">
                </div>
                
                <button type="submit" class="btn-add" style="width: 100%;">Update Guest</button>
            </form>
        </div>
    </div>
    
    <script>
        // Add event listeners
        document.addEventListener('DOMContentLoaded', function() {
            // Edit buttons
            document.querySelectorAll('.btn-edit').forEach(function(button) {
                button.addEventListener('click', function() {
                    var id = this.getAttribute('data-id');
                    var firstName = this.getAttribute('data-firstname');
                    var lastName = this.getAttribute('data-lastname');
                    var email = this.getAttribute('data-email');
                    var phone = this.getAttribute('data-phone');
                    var address = this.getAttribute('data-address');
                    var idType = this.getAttribute('data-idtype');
                    var idNumber = this.getAttribute('data-idnumber');
                    var nationality = this.getAttribute('data-nationality');
                    editGuest(id, firstName, lastName, email, phone, address, idType, idNumber, nationality);
                });
            });
            
            // Delete buttons
            document.querySelectorAll('.btn-delete').forEach(function(button) {
                button.addEventListener('click', function() {
                    var id = this.getAttribute('data-id');
                    deleteGuest(id);
                });
            });
        });
        
        function editGuest(id, firstName, lastName, email, phone, address, idType, idNumber, nationality) {
            document.getElementById('editGuestId').value = id;
            document.getElementById('editFirstName').value = firstName;
            document.getElementById('editLastName').value = lastName;
            document.getElementById('editEmail').value = email;
            document.getElementById('editPhoneNumber').value = phone;
            document.getElementById('editAddress').value = address;
            document.getElementById('editIdType').value = idType;
            document.getElementById('editIdNumber').value = idNumber;
            document.getElementById('editNationality').value = nationality;
            document.getElementById('editModal').style.display = 'block';
        }
        
        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
        }
        
        function deleteGuest(id) {
            if (confirm('Are you sure you want to delete this guest? This action cannot be undone.')) {
                var form = document.createElement('form');
                form.method = 'POST';
                form.action = 'GuestServlet';
                
                var actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';
                form.appendChild(actionInput);
                
                var idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = 'guestId';
                idInput.value = id;
                form.appendChild(idInput);
                
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        // Close modal when clicking outside
        window.onclick = function(event) {
            var editModal = document.getElementById('editModal');
            if (event.target == editModal) {
                editModal.style.display = 'none';
            }
        }
    </script>
</body>
</html>
