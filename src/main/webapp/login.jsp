<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Security: Prevent back button access
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort - Login</title>
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
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        .container {
            width: 100%;
            max-width: 400px;
            padding: 20px;
        }
        
        .login-box {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
            padding: 50px 40px;
            animation: slideUp 0.6s ease-out;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
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
            color: #002853;
            font-size: 32px;
            margin-bottom: 10px;
            font-weight: 700;
        }
        
        .header p {
            color: #0071bc;
            font-size: 15px;
            font-weight: 500;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #002853;
            font-weight: 600;
            font-size: 14px;
        }
        
        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 14px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s;
            background: rgba(255, 255, 255, 0.9);
        }
        
        input[type="text"]:focus,
        input[type="password"]:focus {
            outline: none;
            border-color: #0071bc;
            box-shadow: 0 0 0 4px rgba(0, 113, 188, 0.15);
            background: white;
        }
        
        button {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #002853 0%, #0071bc 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(0, 40, 83, 0.3);
        }
        
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 40, 83, 0.4);
            background: linear-gradient(135deg, #003366 0%, #0088dd 100%);
        }
        
        button:active {
            transform: translateY(0);
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
        
        .footer {
            text-align: center;
            margin-top: 20px;
            color: #666;
            font-size: 14px;
        }
        
        .footer a {
            color: #0071bc;
            text-decoration: none;
            font-weight: 600;
        }
        
        .footer a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="login-box">
            <div class="header">
                <h1>🏨 Ocean View Resort</h1>
                <p>Staff Reservation System</p>
            </div>
            
            <%
                String error = request.getParameter("error");
                if (error != null && !error.isEmpty()) {
            %>
            <div class="error show">
                <strong>Login Failed!</strong> Invalid username or password.
            </div>
            <%
                }
            %>
            
            <form action="LoginServlet" method="POST">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required placeholder="Enter your username" autofocus>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required placeholder="Enter your password">
                </div>
                
                <button type="submit">Login</button>
            </form>
            
            <div class="footer">
                <p>Default Admin: <strong>admin</strong> / <strong>admin123</strong></p>
            </div>
        </div>
    </div>
</body>
</html>
