<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<%@page session="true" import="java.util.*, olabpkg.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>OriginLab League Matchs</title>
</head>
<body>
    <a href = "scoring">view player scores</a>
    <hr>
    <%
    String playerListOpts = new String();
    Map<Integer, String> players = (Map<Integer, String>) request.getAttribute("players");
    if ( null != players )
        for (Map.Entry<Integer, String> entry : players.entrySet())
        {
            playerListOpts += "<option value='" + entry.getKey() + "'>" + entry.getValue() + "</option>";
        }
    %>
    <form name="AddForm" action="matching" method="POST">
        <input type="hidden" name="todo" value="addsingle">
        <b>Single Match</b><br>
        Date: <input type="date" name="date", id="singledate">
        <br>
        <script>
        document.getElementById('singledate').value = new Date().toISOString().substring(0, 10);
        </script>
        Play Winer : <select name=playerwin><%=playerListOpts%></select>
        Score:
        <input type="text" name="scorewin" size="5" value="">
        <br>
        Play Losser: <select name=playerloss><%=playerListOpts%></select>
        Score:
        <input type="text" name="scoreloss" size="5" value="">
        <br>
        <input type="submit" value="Add to Match list">
    </form>
    <br>
    <form name="AddForm" action="matching" method="POST">
        <input type="hidden" name="todo" value="adddouble">
        <b>double Match</b><br>
        Date: <input type="date" name="date", id="doubledate">
        <br>
        <script>
        document.getElementById('doubledate').value = new Date().toISOString().substring(0, 10);
        </script>
        Play Winer :
        <select name=playerwina><%=playerListOpts%></select>
        <select name=playerwinb><%=playerListOpts%></select>
        Score:
        <input type="text" name="scorewin" size="5" value="">
        <br>
        Play Losser:
        <select name=playerlossa><%=playerListOpts%></select>
        <select name=playerlossb><%=playerListOpts%></select>
        Score:
        <input type="text" name="scoreloss" size="5" value="">
        <br>
        <input type="submit" value="Add to Match list">
    </form>
    <hr>
    <table border="1">
        <tr>
            <th>Date</th>
            <th>Winer</th>
            <th>Losser</th>
            <th>Score</th>
        </tr>
        <%
        List<Match> list = (List<Match>) request.getAttribute("matchs");
        if ( null != list )
            for (Match item : list) {
        %>
        <tr>
            <td><%=item.date%></td>
            <td><%=item.playerWin%></td>
            <td><%=item.playerLoss%></td>
            <td><%=item.scoreWin%>-<%=item.scoreLoss%></td>
        </tr>
        <%}%>
    </table>
</body>
</html>