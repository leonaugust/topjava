<%@taglib uri="http://example.com/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE HTML>
<html lang="ru">
<head>
    <link rel="shortcut icon" href="calories.png" type="image/x-icon">
    <title>Подсчет калорий</title>
    <style>
        <%@include file="/WEB-INF/css/button.css"%>
        <%@include file="/WEB-INF/css/table.css"%>
    </style>
</head>
<body style=background-color:#D3D3D3>
<h3><a href="index.html">Home</a></h3>
<h2 align="center">Моя еда</h2>
<button type="button" class="button">
    <a href="meals?action=insert">Add Meal</a>
</button>
<table style="width:60%">
    <tr>
        <th>Дата/Время</th>
        <th>Описание</th>
        <th>Калории</th>
        <th colspan=2></th>
    </tr>
    <tbody>
    <c:forEach items="${requestScope.mealsTo}" var="mealTo">
        <tr class="${!mealTo.excess ? 'darkgreen':'crimson'}">
            <td><c:out value="${f:formatLocalDateTime(mealTo.dateTime)}"/></td>
            <td><c:out value="${mealTo.description}"/></td>
            <td><c:out value="${mealTo.calories}"/></td>
            <td>
                <button type="button" class="button, blueButton">
                    <a href=meals?action=edit&id=${mealTo.id}>Edit</a>
                </button>
            </td>
            <td>
                <button type="button" class="button, redButton">
                    <a href=meals?action=delete&id=${mealTo.id}>Delete</a>
                </button>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>