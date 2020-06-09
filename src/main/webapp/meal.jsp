<%@taglib uri="http://example.com/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE HTML>
<html lang="ru">
<head>
    <title>Add new meal</title>
    <link rel="shortcut icon" href="calories.png" type="image/x-icon">
</head>
<body style="background-color:#D3D3D3">
<div align="center">
    <jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
    <form method="POST" action='meals' name="frmAddMeal">
        <label>
            <input type="hidden" name="mealId"
                   value="<c:out value="${meal.id}" />"/>
        </label> <br/>
        Date/Time : <label>
        <input type="datetime-local" name="dateTime"
               value="<c:out value="${meal.dateTime}" />"/>
    </label> <br/>
        Description : <label>
        <input type="text" name="description"
               value="<c:out value="${meal.description}" />"/>
    </label> <br/>
        Calories : <label>
        <input type="number" name="calories"
               value="<c:out value="${meal.calories}" />"/>
    </label> <br/>
        <input type="submit" value="Submit"/>
    </form>
</div>
</body>
</html>