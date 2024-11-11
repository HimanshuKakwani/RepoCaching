<!DOCTYPE html>
<html>
<head>
    <title>Position Book</title>
</head>
<body>
    <h1>Position Book Application</h1>
    <form action="PositionBookServlet" method="get">
        <label for="customerId">Enter Customer ID:</label>
        <input type="text" id="customerId" name="customerId" required>
        <button type="submit">Search Position Book</button>
    </form>
    <button onclick="location.href='AllPositionsServlet'">View All Positions</button>
</body>
</html>
