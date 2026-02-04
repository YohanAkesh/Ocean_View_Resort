<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Register Staff</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            width: 100%;
            max-width: 450px;
        }
        
        .register-box {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            padding: 40px;
            animation: slideUp 0.5s ease-out;
        }
        
        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .header h1 {
            color: #333;
            font-size: 28px;
            margin-bottom: 5px;
        }
        
        .header p {
            color: #666;
            font-size: 14px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
            font-size: 14px;
        }
        
        input[type="text"],
        input[type="password"],
        select {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        input[type="text"]:focus,
        input[type="password"]:focus,
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
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
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
            border: 1px solid #f5c6cb;
            display: none;
        }
        
        .error.show {
            display: block;
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
            display: none;
        }
        
        .success.show {
            display: block;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="register-box">
            <div class="header">
                <h1>📝 Register Staff</h1>
                <p>Admin Only - Register New Staff Member</p>
            </div>
            
            <%
                String error = request.getParameter("error");
                String success = request.getParameter("success");
                
                if (error != null && !error.isEmpty()) {
            %>
            <div class="error show">
                <strong>Registration Failed!</strong> <%= error %>
            </div>
            <%
                }
                if (success != null && !success.isEmpty()) {
            %>
            <div class="success show">
                <strong>Success!</strong> Staff member registered successfully.
            </div>
            <%
                }
            %>
            
            <form action="RegisterServlet" method="POST" onsubmit="return validateForm()">
                <div class="form-group">
                    <label for="username">Username (min 3 chars, alphanumeric)</label>
                    <input type="text" id="username" name="username" required placeholder="Enter username" pattern="[a-zA-Z0-9_]{3,}">
                </div>
                
                <div class="form-group">
                    <label for="password">Password (min 6 characters)</label>
                    <input type="password" id="password" name="password" required placeholder="Enter password" minlength="6">
                </div>
                
                <div class="form-group">
                    <label for="fullName">Full Name</label>
                    <input type="text" id="fullName" name="fullName" required placeholder="Enter full name" pattern="[a-zA-Z\s]+">
                </div>
                
                <div class="form-group">
                    <label for="role">Role</label>
                    <select id="role" name="role" required>
                        <option value="">Select a role</option>
                        <option value="ADMIN">ADMIN</option>
                        <option value="STAFF">STAFF</option>
                    </select>
                </div>
                
                <div class="button-group">
                    <button type="submit">Register</button>
                    <button type="button" class="cancel-btn" onclick="window.history.back()">Cancel</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        function validateForm() {
            var username = document.getElementById("username").value;
            var password = document.getElementById("password").value;
            var fullName = document.getElementById("fullName").value;
            var role = document.getElementById("role").value;
            
            if (username.length < 3) {
                alert("Username must be at least 3 characters");
                return false;
            }
            
            if (password.length < 6) {
                alert("Password must be at least 6 characters");
                return false;
            }
            
            if (!fullName.match(/^[a-zA-Z\s]+$/)) {
                alert("Full name must contain only letters");
                return false;
            }
            
            if (role === "") {
                alert("Please select a role");
                return false;
            }
            
            return true;
        }
    </script>
</body>
</html>
