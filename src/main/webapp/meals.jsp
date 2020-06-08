<%@taglib uri="http://example.com/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <style>
        table, th, td {
            border: 2px solid black;
            border-collapse: collapse;
            margin-left: auto;
            margin-right: auto;
            width: auto;
        }

        .darkgreen{
            color: darkgreen; background-color: #C0C0C0
        }

        .red{
            color: red;
        }
    </style>
</head>

<body style="background-color:#D3D3D3">
<h3><a href="index.html">Home</a></h3>
<div>
<h2 align="center">Meals</h2>
<table style="width:50%">
    <thead>
    <tr>
        <th>Date/Time</th>
        <th>Description</th>
        <th>Calories</th>
        <th>Excess</th>
        <th colspan=2>Action</th>
    </tr>
    </thead>
    <tbody>

    <c:forEach items="${requestScope.mealsTo}" var="mealTo">
        <tr class="${mealTo.excess == false ? 'darkgreen':'red'}">>
            <td><c:out value="${f:formatLocalDateTime(mealTo.dateTime)}"/></td>
            <td><c:out value="${mealTo.description}"/></td>
            <td><c:out value="${mealTo.calories}"/></td>
            <td><c:out value="${mealTo.excess}"/></td>
            <td><a href="${pageContext.request.contextPath}/meals?action=edit&mealId=<c:out value="${mealTo.id}"/>">Edit</a></td>
            <td><a href="${pageContext.request.contextPath}/meals?action=delete&mealId=<c:out value="${mealTo.id}"/>">Delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<p align="center"><a href="${pageContext.request.contextPath}/meals?action=insert">Add Meal</a></p>
</div>
</body>
</html>