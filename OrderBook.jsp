<%@ page import="org.json.JSONObject, org.json.JSONArray" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Order Book</title>
</head>
<body>
    <h1>Order Book</h1>
    <%
        String customerDataJson = (String) request.getAttribute("customerData");
        JSONObject customerData = new JSONObject(customerDataJson);
        JSONObject scrips = customerData.getJSONObject("scrips");
    %>
    <table border="1">
        <tr>
            <th>ScripCode</th>
            <th>Exchange</th>
            <th>OrderId</th>
            <th>Buy/Sell</th>
            <th>Quantity</th>
            <th>Price</th>
        </tr>
        <%
            for (Object scripObj : scrips.keySet()) {
            	String scrip = (String) scripObj;
                JSONArray orders = scrips.getJSONArray(scrip);
                for (int i = 0; i < orders.length(); i++) {
                    JSONObject order = orders.getJSONObject(i);
        %>
        <tr>
            <td><%= scrip %></td>
            <td><%= order.getString("exchange") %></td>
            <td><%= order.getString("order_id") %></td>
            <td><%= order.getString("Buy/Sell") %></td>
            <td><%= order.getInt("quantity") %></td>
            <td><%= order.getDouble("price") %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</body>
</html>
