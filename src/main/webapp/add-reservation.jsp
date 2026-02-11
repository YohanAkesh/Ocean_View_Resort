<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.app.model.Room" %>
<%@ page import="com.app.model.Guest" %>
<%@ page import="com.app.service.ReservationService" %>
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
    
    // Get available rooms
    ReservationService reservationService = new ReservationService();
    List<Room> availableRooms = reservationService.getAvailableRooms();
    
    // Get all registered guests
    GuestService guestService = new GuestService();
    List<Guest> guests = guestService.getAllGuests();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Add Reservation</title>
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
            max-width: 800px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .form-container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .form-container h2 {
            color: #333;
            margin-bottom: 10px;
        }
        
        .form-container p {
            color: #666;
            margin-bottom: 30px;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
        }
        
        .required {
            color: red;
        }
        
        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .btn-submit {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 15px 40px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            font-size: 16px;
            width: 100%;
            transition: transform 0.2s;
        }
        
        .btn-submit:hover {
            transform: translateY(-2px);
        }
        
        .btn-back {
            background: #6c757d;
            color: white;
            border: none;
            padding: 15px 40px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            font-size: 16px;
            width: 100%;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            margin-top: 10px;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 2px solid #f5c6cb;
            font-size: 15px;
            box-shadow: 0 2px 8px rgba(220, 53, 69, 0.2);
        }
        
        .error strong {
            font-size: 16px;
            display: block;
            margin-bottom: 8px;
        }
        
        .warning-box {
            background-color: #fff3cd;
            color: #856404;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #ffc107;
            font-size: 14px;
        }
        
        .info-box {
            background: #e7f3ff;
            border-left: 4px solid #2196F3;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        
        .info-box h4 {
            color: #1976D2;
            margin-bottom: 5px;
        }
        
        .info-box p {
            color: #555;
            font-size: 14px;
            margin: 0;
        }
        
        .room-info {
            background: #f9f9f9;
            padding: 10px;
            border-radius: 5px;
            margin-top: 10px;
            display: none;
        }
        
        .room-info.active {
            display: block;
        }
        
        .room-details {
            display: flex;
            justify-content: space-between;
            margin-top: 5px;
            font-size: 14px;
        }
        
        .cost-calculator {
            background: #fff3cd;
            border: 1px solid #ffc107;
            padding: 15px;
            border-radius: 5px;
            margin-top: 20px;
            display: none;
        }
        
        .cost-calculator.active {
            display: block;
        }
        
        .cost-calculator h4 {
            color: #856404;
            margin-bottom: 10px;
        }
        
        .cost-details {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            color: #856404;
        }
        
        .cost-total {
            font-size: 18px;
            font-weight: bold;
            border-top: 2px solid #ffc107;
            padding-top: 10px;
            margin-top: 10px;
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
            
            if (error != null && !error.isEmpty()) {
        %>
        <div class="error">
            <strong>Error!</strong> <%= error %>
        </div>
        <%
            }
        %>
        
        <div class="form-container">
            <h2>📝 Create New Reservation</h2>
            <p>Select a registered guest and room for reservation</p>
            
            <div class="info-box">
                <h4>📌 Important Information</h4>
                <p>All fields marked with <span class="required">*</span> are required. Select from registered guests or <a href="add-guest.jsp" style="color: #2196F3; font-weight: bold;">register a new guest</a>.</p>
                <p style="margin-top: 8px;"><strong>⚠️ Room Availability:</strong> The system will automatically check if your selected room is available for the chosen dates. If the room is already booked during that period, you'll need to select different dates or another room.</p>
            </div>
            
            <form method="POST" action="ReservationServlet" onsubmit="return validateForm()">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="guestId">Select Guest <span class="required">*</span></label>
                    <select id="guestId" name="guestId" required onchange="showGuestInfo()">
                        <option value="">-- Select a guest --</option>
                        <% 
                        if (guests != null && !guests.isEmpty()) {
                            for (Guest guest : guests) {
                        %>
                        <option value="<%= guest.getGuestId() %>" 
                                data-name="<%= guest.getFullName() %>"
                                data-email="<%= guest.getEmail() %>"
                                data-phone="<%= guest.getPhoneNumber() %>"
                                data-address="<%= guest.getAddress() != null ? guest.getAddress().replace("\"", "&quot;") : "N/A" %>"
                                data-idtype="<%= guest.getIdType() %>"
                                data-idnumber="<%= guest.getIdNumber() %>"
                                data-nationality="<%= guest.getNationality() != null ? guest.getNationality() : "N/A" %>">
                            <%= guest.getFullName() %> - <%= guest.getEmail() %> - <%= guest.getPhoneNumber() %>
                        </option>
                        <% 
                            }
                        } else {
                        %>
                        <option value="" disabled>No guests registered</option>
                        <% } %>
                    </select>
                    
                    <div id="guestInfo" class="room-info">
                        <strong>Guest Details:</strong>
                        <div class="room-details" style="display: block;">
                            <div style="margin-top: 5px;"><strong>Name:</strong> <span id="guestName">-</span></div>
                            <div style="margin-top: 5px;"><strong>Email:</strong> <span id="guestEmail">-</span></div>
                            <div style="margin-top: 5px;"><strong>Phone:</strong> <span id="guestPhone">-</span></div>
                            <div style="margin-top: 5px;"><strong>Address:</strong> <span id="guestAddress">-</span></div>
                            <div style="margin-top: 5px;"><strong>ID Type:</strong> <span id="guestIdType">-</span></div>
                            <div style="margin-top: 5px;"><strong>ID Number:</strong> <span id="guestIdNumber">-</span></div>
                            <div style="margin-top: 5px;"><strong>Nationality:</strong> <span id="guestNationality">-</span></div>
                        </div>
                    </div>
                </div>
                
                <div class="form-group" style="text-align: center; margin: 20px 0;">
                    <a href="add-guest.jsp" class="btn" style="display: inline-block; background: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: 600;">+ Register New Guest</a>
                </div>
                
                <div class="form-group">
                    <label for="roomId">Select Room <span class="required">*</span></label>
                    <select id="roomId" name="roomId" required onchange="showRoomInfo()">
                        <option value="">-- Select a room --</option>
                        <% 
                        if (availableRooms != null && !availableRooms.isEmpty()) {
                            for (Room room : availableRooms) {
                        %>
                        <option value="<%= room.getRoomId() %>" 
                                data-room-number="<%= room.getRoomNumber() %>"
                                data-room-type="<%= room.getRoomType() %>"
                                data-capacity="<%= room.getCapacity() %>"
                                data-price="<%= room.getPricePerNight() %>">
                            Room <%= room.getRoomNumber() %> - <%= room.getRoomType() %> 
                            (Capacity: <%= room.getCapacity() %>, $<%= String.format("%.2f", room.getPricePerNight()) %>/night)
                        </option>
                        <% 
                            }
                        } else {
                        %>
                        <option value="" disabled>No rooms available</option>
                        <% } %>
                    </select>
                    
                    <div id="roomInfo" class="room-info">
                        <strong>Room Details:</strong>
                        <div class="room-details">
                            <span>Room Number: <span id="roomNumber">-</span></span>
                            <span>Type: <span id="roomType">-</span></span>
                            <span>Capacity: <span id="roomCapacity">-</span> guests</span>
                            <span>Price: $<span id="roomPrice">0.00</span>/night</span>
                        </div>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="checkInDate">Check-In Date <span class="required">*</span></label>
                        <input type="date" id="checkInDate" name="checkInDate" required 
                               onchange="calculateCost()">
                    </div>
                    
                    <div class="form-group">
                        <label for="checkOutDate">Check-Out Date <span class="required">*</span></label>
                        <input type="date" id="checkOutDate" name="checkOutDate" required 
                               onchange="calculateCost()">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="numberOfGuests">Number of Guests <span class="required">*</span></label>
                    <input type="number" id="numberOfGuests" name="numberOfGuests" required 
                           min="1" value="1" placeholder="Enter number of guests">
                </div>
                
                <div class="form-group">
                    <label for="specialRequests">Special Requests</label>
                    <textarea id="specialRequests" name="specialRequests" 
                              placeholder="Any special requirements or requests (optional)"></textarea>
                </div>
                
                <div id="costCalculator" class="cost-calculator">
                    <h4>💰 Cost Breakdown</h4>
                    <div class="cost-details">
                        <span>Number of Nights:</span>
                        <span id="numNights">0</span>
                    </div>
                    <div class="cost-details">
                        <span>Price per Night:</span>
                        <span>$<span id="pricePerNight">0.00</span></span>
                    </div>
                    <div class="cost-details cost-total">
                        <span>Total Cost:</span>
                        <span>$<span id="totalCost">0.00</span></span>
                    </div>
                </div>
                
                <button type="submit" class="btn-submit">Create Reservation</button>
                <a href="staff-reservations.jsp" class="btn-back">Back to Reservations</a>
            </form>
        </div>
    </div>
    
    <script>
        // Set minimum date to today
        var today = new Date().toISOString().split('T')[0];
        document.getElementById('checkInDate').setAttribute('min', today);
        document.getElementById('checkOutDate').setAttribute('min', today);
        
        function showGuestInfo() {
            var select = document.getElementById('guestId');
            var selectedOption = select.options[select.selectedIndex];
            
            if (selectedOption.value) {
                document.getElementById('guestName').textContent = selectedOption.dataset.name;
                document.getElementById('guestEmail').textContent = selectedOption.dataset.email;
                document.getElementById('guestPhone').textContent = selectedOption.dataset.phone;
                document.getElementById('guestAddress').textContent = selectedOption.dataset.address;
                document.getElementById('guestIdType').textContent = selectedOption.dataset.idtype;
                document.getElementById('guestIdNumber').textContent = selectedOption.dataset.idnumber;
                document.getElementById('guestNationality').textContent = selectedOption.dataset.nationality;
                document.getElementById('guestInfo').classList.add('active');
            } else {
                document.getElementById('guestInfo').classList.remove('active');
            }
        }
        
        function showRoomInfo() {
            var select = document.getElementById('roomId');
            var selectedOption = select.options[select.selectedIndex];
            
            if (selectedOption.value) {
                document.getElementById('roomNumber').textContent = selectedOption.dataset.roomNumber;
                document.getElementById('roomType').textContent = selectedOption.dataset.roomType;
                document.getElementById('roomCapacity').textContent = selectedOption.dataset.capacity;
                document.getElementById('roomPrice').textContent = parseFloat(selectedOption.dataset.price).toFixed(2);
                document.getElementById('roomInfo').classList.add('active');
                
                calculateCost();
            } else {
                document.getElementById('roomInfo').classList.remove('active');
            }
        }
        
        function calculateCost() {
            var checkIn = document.getElementById('checkInDate').value;
            var checkOut = document.getElementById('checkOutDate').value;
            var select = document.getElementById('roomId');
            var selectedOption = select.options[select.selectedIndex];
            
            if (checkIn && checkOut && selectedOption.value) {
                var checkInDate = new Date(checkIn);
                var checkOutDate = new Date(checkOut);
                
                if (checkOutDate > checkInDate) {
                    var timeDiff = checkOutDate.getTime() - checkInDate.getTime();
                    var nights = Math.ceil(timeDiff / (1000 * 3600 * 24));
                    var pricePerNight = parseFloat(selectedOption.dataset.price);
                    var totalCost = nights * pricePerNight;
                    
                    document.getElementById('numNights').textContent = nights;
                    document.getElementById('pricePerNight').textContent = pricePerNight.toFixed(2);
                    document.getElementById('totalCost').textContent = totalCost.toFixed(2);
                    document.getElementById('costCalculator').classList.add('active');
                } else {
                    document.getElementById('costCalculator').classList.remove('active');
                }
            }
        }
        
        function validateForm() {
            var guestId = document.getElementById('guestId').value;
            var checkIn = document.getElementById('checkInDate').value;
            var checkOut = document.getElementById('checkOutDate').value;
            var roomId = document.getElementById('roomId').value;
            
            if (!guestId) {
                alert('Please select a guest');
                return false;
            }
            
            if (!roomId) {
                alert('Please select a room');
                return false;
            }
            
            if (checkIn && checkOut) {
                var checkInDate = new Date(checkIn);
                var checkOutDate = new Date(checkOut);
                var today = new Date();
                today.setHours(0, 0, 0, 0);
                
                if (checkInDate < today) {
                    alert('Check-in date cannot be in the past');
                    return false;
                }
                
                if (checkOutDate <= checkInDate) {
                    alert('Check-out date must be after check-in date');
                    return false;
                }
            }
            
            return true;
        }
        
        // Update check-out min date when check-in changes
        document.getElementById('checkInDate').addEventListener('change', function() {
            var checkInDate = this.value;
            document.getElementById('checkOutDate').setAttribute('min', checkInDate);
        });
    </script>
</body>
</html>
