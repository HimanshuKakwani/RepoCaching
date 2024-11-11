<!DOCTYPE html>
<html>
<head>
    <title>Position Book for Customer</title>
</head>
<body>
    <h2>Position Book for Customer ${param.customerId}</h2>
    <table border="1">
        <thead>
            <tr>
                <th>Scrip Code</th><th>Pos Type</th><th>Buy Qty</th><th>Avg Buy Rate</th>
                <th>Sell Qty</th><th>Avg Sell Rate</th><th>Net Qty</th>
                <th>Avg Price</th><th>Buy Amt</th><th>Sell Amt</th><th>P/L</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="order" items="${userJson.scrips}">
                <tr>
                    <td>${order.scripCode}</td>
                    <td>${order.posType}</td>
                    <td>${order.buyQty}</td>
                    <td>${order.avgBuyRate}</td>
                    <td>${order.sellQty}</td>
                    <td>${order.avgSellRate}</td>
                    <td>${order.netQty}</td>
                    <td>${order.avgPrice}</td>
                    <td>${order.buyAmt}</td>
                    <td>${order.sellAmt}</td>
                    <td>${order.pl}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <p>Cache retrieval time: ${elapsedTime} ns</p>
    <a href="index.jsp">Back to Home</a>
</body>
</html>
