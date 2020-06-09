<%@taglib uri="http://example.com/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE HTML>
<html lang="ru">
<head>
    <link rel="shortcut icon" href="calories.png" type="image/x-icon">
    <title>Подсчет калорий</title>
    <style>
        table, th, td {
            border: 2px solid black;
            border-collapse: collapse;
            margin-left: auto;
            margin-right: auto;
            width: auto;
            height: 5%;
            font-family: optima, sans-serif;
            font-size: 15px;
        }

        th {
            width: 20%;
        }

        .darkgreen {
            color: darkgreen;
            background-color: #C0C0C0;
        }

        .crimson {
            color: crimson;
        }

        .button {
            background-color: #4CAF50;
            border: none;
            color: white;
            padding: 15px 32px;
            text-align: center;
            text-decoration: none;
            display: block;
            font-size: 18px;
            margin: auto;
        }

        .redButton {
            background-color: #f44336;
            width: 100%;
        }

        .blueButton {
            background-color: #008CBA;
            width: 100%;
        }

    </style>
</head>
<body style=background-color:#D3D3D3>
<h3><a href="index.html">Home</a></h3>
<h2 align="center">Моя еда</h2>
<button type="button" class="button">
    <a href="meals?action=insert">Add Meal!</a>
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
                    <a href=meals?action=edit&mealId=${mealTo.id}>Edit</a>
                </button>
            </td>
            <td>
                <button type="button" class="button, redButton">
                    <a href=meals?action=delete&mealId=${mealTo.id}>Delete</a>
                </button>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>