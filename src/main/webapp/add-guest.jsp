<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Security: Prevent back button access
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
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
    <title>Ocean View Resort - Register Guest</title>
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
            max-width: 900px;
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
            border-color: #2193b0;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .btn-submit {
            background: linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%);
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
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
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
            <h2>👤 Register New Guest</h2>
            <p>Enter guest information to register them in the system</p>
            
            <div class="info-box">
                <h4>📌 Important Information</h4>
                <p>All fields marked with <span class="required">*</span> are required. Please ensure all guest information is accurate.</p>
            </div>
            
            <form method="POST" action="GuestServlet">
                <input type="hidden" name="action" value="add">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="firstName">First Name <span class="required">*</span></label>
                        <input type="text" id="firstName" name="firstName" required 
                               placeholder="Enter first name">
                    </div>
                    
                    <div class="form-group">
                        <label for="lastName">Last Name <span class="required">*</span></label>
                        <input type="text" id="lastName" name="lastName" required 
                               placeholder="Enter last name">
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="email">Email <span class="required">*</span></label>
                        <input type="email" id="email" name="email" required 
                               placeholder="guest@example.com">
                    </div>
                    
                    <div class="form-group">
                        <label for="phoneNumber">Phone Number <span class="required">*</span></label>
                        <input type="tel" id="phoneNumber" name="phoneNumber" required 
                               placeholder="+1234567890">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="address">Address</label>
                    <textarea id="address" name="address" 
                              placeholder="Enter guest's full address"></textarea>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="idType">ID Type <span class="required">*</span></label>
                        <select id="idType" name="idType" required>
                            <option value="">-- Select ID Type --</option>
                            <option value="PASSPORT">Passport</option>
                            <option value="DRIVERS_LICENSE">Driver's License</option>
                            <option value="NATIONAL_ID">National ID Card</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="idNumber">ID Number <span class="required">*</span></label>
                        <input type="text" id="idNumber" name="idNumber" required 
                               placeholder="Enter ID number">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="nationality">Nationality</label>
                    <input type="text" id="nationality" name="nationality" 
                           placeholder="e.g., American, British, Canadian">
                </div>
                
                <button type="submit" class="btn-submit">Register Guest</button>
                <a href="manage-guests.jsp" class="btn-back">Cancel</a>
            </form>
        </div>
    </div>
</body>
</html>
