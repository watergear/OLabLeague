<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<%@page session="true" import="java.util.*, olabpkg.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>OriginLab League Scores</title>
</head>
<body>
	<a href = "matching">view player matchs</a>
	<hr>
	<form name="AddForm" action="scoring" method="POST">
		<input type="hidden" name="todo" value="addplayer">
		Name:
		<input type="text" name="name" size="10" value="">
		<br>
		<input type="submit" value="Add to Player list">
	</form>
	<hr>
	<table border="1">
		<tr>
			<th>Name</th>
			<th>Score</th>
		</tr>
		<%
		List<Achievement> list = (List<Achievement>) request.getAttribute("scores");
		if ( null != list )
			for (Achievement item : list) {
		%>
		<tr>
			<td><%=item.name%></td>
			<td><%=item.score%></td>
		</tr>
		<%}%>
	</table>
</body>
</html>