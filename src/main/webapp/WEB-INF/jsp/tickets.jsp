<%@ page import="org.matveyvs.service.TicketService" %>
<%@ page import="org.matveyvs.dto.TicketDto" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>Tickets that have bought from JSP:</h1>
<ul>
    <%
        TicketService ticketService = TicketService.getInstance();
        Long flightId = Long.valueOf(request.getParameter("flightId"));
        for(TicketDto ticketDto : ticketService.findAllByFlightId(flightId)){
            out.write(String.format("<li>%s</li>", ticketDto.seatNo()));
        }
    %>
</ul>
</body>
</html>
