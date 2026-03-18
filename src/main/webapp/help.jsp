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
    <title>Ocean View Resort - Help Guide</title>
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
            background: linear-gradient(135deg, rgba(0, 40, 83, 0.98), rgba(0, 113, 188, 0.98));
            backdrop-filter: blur(20px);
            color: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
            border-bottom: 2px solid rgba(255, 255, 255, 0.1);
        }
        
        .navbar h1 {
            font-size: 24px;
            cursor: pointer;
        }
        
        .navbar h1:hover {
            opacity: 0.9;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .back-btn {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            border: 1px solid rgba(255, 255, 255, 0.3);
            padding: 10px 20px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        
        .back-btn:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateY(-1px);
        }
        
        .container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 20px;
        }
        
        .hero-section {
            background: linear-gradient(135deg, rgba(0, 40, 83, 0.95), rgba(0, 113, 188, 0.95));
            backdrop-filter: blur(20px);
            color: white;
            padding: 50px;
            border-radius: 20px;
            text-align: center;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        
        .hero-section h1 {
            font-size: 42px;
            margin-bottom: 15px;
        }
        
        .hero-section p {
            font-size: 18px;
            opacity: 0.9;
        }
        
        .help-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }
        
        .help-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            padding: 35px;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            border-left: 5px solid #0071bc;
            transition: all 0.3s;
        }
        
        .help-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 30px rgba(0, 113, 188, 0.3);
        }
        
        .help-card .card-icon {
            font-size: 48px;
            margin-bottom: 20px;
            display: block;
        }
        
        .help-card h2 {
            color: #002853;
            font-size: 24px;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .help-card h3 {
            color: #0071bc;
            font-size: 18px;
            margin-top: 20px;
            margin-bottom: 10px;
        }
        
        .help-card ol, .help-card ul {
            margin-left: 25px;
            color: #333;
            line-height: 1.8;
        }
        
        .help-card li {
            margin-bottom: 10px;
        }
        
        .help-card p {
            color: #555;
            line-height: 1.8;
            margin-bottom: 15px;
        }
        
        .step-number {
            display: inline-block;
            background: linear-gradient(135deg, #0071bc, #69d2e7);
            color: white;
            width: 28px;
            height: 28px;
            border-radius: 50%;
            text-align: center;
            line-height: 28px;
            font-weight: bold;
            font-size: 14px;
            margin-right: 8px;
        }
        
        .tip-box {
            background: linear-gradient(135deg, rgba(105, 210, 231, 0.1), rgba(0, 113, 188, 0.1));
            border-left: 4px solid #69d2e7;
            padding: 15px;
            border-radius: 8px;
            margin-top: 15px;
        }
        
        .tip-box strong {
            color: #0071bc;
            display: block;
            margin-bottom: 8px;
        }
        
        .quick-links {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            padding: 35px;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        
        .quick-links h2 {
            color: #002853;
            margin-bottom: 25px;
            font-size: 26px;
        }
        
        .link-buttons {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            justify-content: center;
        }
        
        .link-btn {
            background: linear-gradient(135deg, #0071bc, #69d2e7);
            color: white;
            padding: 12px 30px;
            border-radius: 25px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            box-shadow: 0 4px 15px rgba(0, 113, 188, 0.3);
        }
        
        .link-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0, 113, 188, 0.5);
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1 style="cursor: pointer;" onclick="window.location.href='staff-dashboard.jsp'">🏨 Ocean View Resort - Help Center</h1>
        <div class="user-info">
            <a href="staff-dashboard.jsp" class="back-btn">← Back to Dashboard</a>
        </div>
    </div>
    
    <div class="container">
        <div class="hero-section">
            <h1>📚 Staff Help Guide</h1>
            <p>Complete guidelines for using the Ocean View Resort reservation system</p>
        </div>
        
        <div class="help-grid">
            <!-- Guest Registration -->
            <div class="help-card">
                <span class="card-icon">👤</span>
                <h2>Guest Registration</h2>
                <p>How to register a new guest in the system:</p>
                
                <h3>Steps:</h3>
                <ol>
                    <li><span class="step-number">1</span>Click "Register New Guest" from the sidebar or dashboard</li>
                    <li><span class="step-number">2</span>Fill in guest details:
                        <ul>
                            <li>Full Name (required)</li>
                            <li>Email address (required, must be valid)</li>
                            <li>Phone number (required)</li>
                            <li>Address (optional but recommended)</li>
                        </ul>
                    </li>
                    <li><span class="step-number">3</span>Click "Register Guest" button</li>
                    <li><span class="step-number">4</span>Verify the success message appears</li>
                </ol>
                
                <div class="tip-box">
                    <strong>💡 Pro Tip:</strong>
                    Always double-check email and phone number for accuracy. This information is crucial for guest communication and billing.
                </div>
            </div>
            
            <!-- Create Reservation -->
            <div class="help-card">
                <span class="card-icon">📝</span>
                <h2>Creating a Reservation</h2>
                <p>Book a room for a guest:</p>
                
                <h3>Steps:</h3>
                <ol>
                    <li><span class="step-number">1</span>Navigate to "New Reservation" from the sidebar</li>
                    <li><span class="step-number">2</span>Select guest from dropdown (must be registered first)</li>
                    <li><span class="step-number">3</span>Choose an available room from the list</li>
                    <li><span class="step-number">4</span>Enter check-in date (cannot be in the past)</li>
                    <li><span class="step-number">5</span>Enter check-out date (must be after check-in)</li>
                    <li><span class="step-number">6</span>Add any special requests or notes</li>
                    <li><span class="step-number">7</span>Review all details and click "Create Reservation"</li>
                </ol>
                
                <div class="tip-box">
                    <strong>💡 Pro Tip:</strong>
                    If guest is not in the dropdown, register them first before creating the reservation. The system only shows available rooms for the selected dates.
                </div>
            </div>
            
            <!-- Manage Reservations -->
            <div class="help-card">
                <span class="card-icon">📋</span>
                <h2>Managing Reservations</h2>
                <p>Edit, update status, or cancel bookings:</p>
                
                <h3>View Reservations:</h3>
                <ul>
                    <li>Go to "Manage Reservations" from sidebar</li>
                    <li>Use search bar to find specific reservations</li>
                    <li>Filter by status: Active, Confirmed, Cancelled</li>
                </ul>
                
                <h3>Edit a Reservation:</h3>
                <ol>
                    <li><span class="step-number">1</span>Click "Edit" button on the reservation</li>
                    <li><span class="step-number">2</span>Modify dates or details in the modal</li>
                    <li><span class="step-number">3</span>Click "Update Reservation"</li>
                </ol>
                
                <h3>Update Status:</h3>
                <ul>
                    <li>Click status dropdown for the reservation</li>
                    <li>Select new status: PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED</li>
                    <li>Status updates immediately</li>
                </ul>
                
                <div class="tip-box">
                    <strong>💡 Pro Tip:</strong>
                    Always confirm reservations within 24 hours. Update status to CHECKED_IN when guest arrives and CHECKED_OUT when they leave.
                </div>
            </div>
            
            <!-- Guest Management -->
            <div class="help-card">
                <span class="card-icon">📇</span>
                <h2>Guest Database Management</h2>
                <p>View and update guest information:</p>
                
                <h3>Steps:</h3>
                <ol>
                    <li><span class="step-number">1</span>Go to "Manage Guests" from sidebar</li>
                    <li><span class="step-number">2</span>Use search to find specific guests by name, email, or phone</li>
                    <li><span class="step-number">3</span>Click "Edit" to update guest details</li>
                    <li><span class="step-number">4</span>Click "Delete" to remove a guest (only if no active reservations)</li>
                </ol>
                
                <div class="tip-box">
                    <strong>💡 Pro Tip:</strong>
                    Regular guests should have their profiles kept up-to-date with current contact information. Check for duplicates before registering returning guests.
                </div>
            </div>
            
            <!-- Billing and Invoicing -->
            <div class="help-card">
                <span class="card-icon">💵</span>
                <h2>Bill Generation</h2>
                <p>Create and download PDF invoices:</p>
                
                <h3>Steps:</h3>
                <ol>
                    <li><span class="step-number">1</span>Navigate to "Generate Bills" from sidebar</li>
                    <li><span class="step-number">2</span>Find the reservation to bill</li>
                    <li><span class="step-number">3</span>Click "Generate Bill" button</li>
                    <li><span class="step-number">4</span>PDF bill downloads automatically</li>
                </ol>
                
                <h3>Bill Details Include:</h3>
                <ul>
                    <li>Guest information and contact details</li>
                    <li>Room number and type</li>
                    <li>Check-in and check-out dates</li>
                    <li>Number of nights stayed</li>
                    <li>Room charges</li>
                    <li>5% service charge</li>
                    <li>10% tax</li>
                    <li>Total amount due</li>
                </ul>
                
                <div class="tip-box">
                    <strong>💡 Pro Tip:</strong>
                    Generate bills before guest checkout. Keep PDF copies for accounting records. Bills include unique bill numbers for tracking.
                </div>
            </div>
            
            <!-- Common Issues -->
            <div class="help-card">
                <span class="card-icon">⚠️</span>
                <h2>Common Issues & Solutions</h2>
                
                <h3>Issue: Can't create reservation</h3>
                <p><strong>Solution:</strong> Ensure guest is registered first. Check that dates are valid (check-in before check-out, no past dates).</p>
                
                <h3>Issue: No rooms available</h3>
                <p><strong>Solution:</strong> All rooms are booked for selected dates. Try different dates or check with admin about room status.</p>
                
                <h3>Issue: Can't delete guest</h3>
                <p><strong>Solution:</strong> Guest may have active or past reservations. Cancel reservations first or contact admin.</p>
                
                <h3>Issue: Bill not generating</h3>
                <p><strong>Solution:</strong> Ensure reservation exists and has valid data. Check browser allows PDF downloads.</p>
                
                <div class="tip-box">
                    <strong>💡 Pro Tip:</strong>
                    If you encounter persistent errors, note the error message and report to your supervisor. Always log out properly when ending your shift.
                </div>
            </div>
        </div>
        
        <div class="quick-links">
            <h2>🚀 Quick Access Links</h2>
            <div class="link-buttons">
                <a href="add-guest.jsp" class="link-btn">👤 Register Guest</a>
                <a href="add-reservation.jsp" class="link-btn">📝 New Reservation</a>
                <a href="staff-reservations.jsp" class="link-btn">📋 Manage Reservations</a>
                <a href="manage-guests.jsp" class="link-btn">📇 Manage Guests</a>
                <a href="generate-bills.jsp" class="link-btn">💵 Generate Bills</a>
                <a href="staff-dashboard.jsp" class="link-btn">🏠 Dashboard</a>
            </div>
        </div>
    </div>
</body>
</html>
