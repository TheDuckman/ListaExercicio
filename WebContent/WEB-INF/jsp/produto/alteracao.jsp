<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CRUD</title>
<style type="text/css">
body
{
background-color:#ffcfeb;
}
h1
{
background-color:#fea8ce;
font-family: "Times New Roman";
color:black;
}
p
{
font-family:"Times New Roman";
font-size:18px;
}
div
{
background-color:#fec2e5;
}
</style> 
</head>
<body>
	<h1>CRUD</h1>
	<p>Alterar produto</p>
	<form action='alteracao'>
		Id do produto a alterar: <input type="text" name="id" /> <br />
		Novo Nome: <input type="text" name="nome"/><br/>
		Nova Descricao : <input type="text" name="descricao"/><br/>
		Novo Preco : <input type="text" name="preco"/><br/>
	<input type="submit" value="OK"/>
	</form>
</body>
</html>