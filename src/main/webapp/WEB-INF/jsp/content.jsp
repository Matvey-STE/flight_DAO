<%--
  Created by IntelliJ IDEA.
  User: matvey
  Date: 17.9.23.
  Time: 14:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div>
    <span>Content сербскии Русский</span>
    <p>Size: ${requestScope.flights.size()}</p>
    <p>Description: ${requestScope.flights.get(0).description()}</p>
    <p>Id: ${requestScope.flights[1].id()}</p>
    <p>JSESSIONID: ${cookie.get("JSESSIONID")}</p>
    <p>PARAM ID: ${param.id}</p>
    <p>HEADER ID: ${header["cookie"]}</p>
    <p>NOT EMPTY: ${not empty flights}</p>
</div>
</body>
</html>
