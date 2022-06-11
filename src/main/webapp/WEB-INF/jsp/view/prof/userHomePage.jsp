<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/security/tags"%>


<jsp:include page="../fragments/userheader.jsp" />
<div class="container">

	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<div class="container-fluid">



			<jsp:include page="../fragments/usermenu.jsp" />

		</div>
	</nav>






	<div>
		<h3>Prof home page</h3>
		<p>Hello and welcome to your application</p>

		<s:authorize access="isAuthenticated()">
			You are connected with:
			<s:authentication property="principal.username" /> <br>
			Your Email : <s:authentication property="principal.email" /><br>
			Your First Name : <s:authentication property="principal.firstName" /><br>
			Your Last name : <s:authentication property="principal.LastName" /><br>
		</s:authorize>
		<form action="/prof/import" method="get">
			<button type="submit">Import File</button>
		</form>
	</div>

	<div>
		<form method="post" action="/prof/import" enctype="multipart/form-data">
			<span>Fichiers sélectionnés :</span>
			<input type="file" name="file" accept=".xlsx, .xls, .csv" multiple
				   onchange="readFilesAndDisplayPreview(this.files);" /> <br/>
			<input type="submit" value="Upload" /> <br/>

			<div id="list"> </div>
		</form>

		<c:if test="${message != null}">
			${message}
		</c:if>

		<c:if test="${confirm != null}">
			etes vous sur de vouloir ecraser les donnee precedents
			<form method="GET" action="/prof/confirm">
				<input type="hidden" name="filename" value="${filename}" />
				<input type="submit" value="mettre a jour" />
			</form>
		</c:if>
	</div>


	<jsp:include page="../fragments/userfooter.jsp" />


