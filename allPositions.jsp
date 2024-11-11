<!DOCTYPE html>
<html>
<head>
    <title>All Positions</title>
</head>
<body>
    <h1>All Position Books</h1>
    <c:forEach var="user" items="${allUsers}">
        <h3>Customer ID: ${user.userId}</h3>
        <table border="1">
            <!-- Similar structure as in positionBook.jsp -->
        </table>
        <p>Cache retrieval time: ${user.elapsedTime} ns</p>
    </c:forEach>
    <a href="index.jsp">Back to Home</a>
</body>
</html>
