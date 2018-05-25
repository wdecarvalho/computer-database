<!DOCTYPE html>
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="infouser" uri="/WEB-INF/taglibs/tagInfoUser.tld"%>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="../static/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link href="../static/css/font-awesome.css" rel="stylesheet" media="screen">
<link href="../static/css/main.css" rel="stylesheet" media="screen">
</head>
<c:if test="${messageUser!=null}">
	<div class="container">
		<infouser:infouser typeAlerte="${typeMessage}"
			message="${messageUser}" />
	</div>
</c:if>
<body>
	<header class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<a class="navbar-brand" href="../dashboard"> Application - Computer
				Database </a>
		</div>
	</header>
	<section id="main">
		<div class="container">
			<div class="row">
				<div class="col-xs-8 col-xs-offset-2 box">
					<div class="label label-default pull-right">id: ${computer.id}</div>
					<input hidden form="EditComputer" name="id" value="${computer.id}"/>
					<h1>Edit Computer</h1>

					<form id="EditComputer" action="${computer.id}" method="POST">
						<fieldset>
							<div id="divComputerName" class="form-group">
								<label for="computerName">Computer name</label> <input
									value="${computer.name}" required  oninvalid="this.setCustomValidity('Ce champ est obligatoire')"
    oninput="this.setCustomValidity('')"type="text" class="form-control"
									id="computerName" name="name" placeholder="Computer name">
									<span id='nomObligatoire' class='help-block hidden'>Le nom est obligatoire</span>
							</div>
							<div class="form-group">
								<label for="introduced">Introduced date</label> <input
									value="${computer.introDate}" type="date" class="form-control"
									id="introduced" name="introDate" placeholder="Introduced date">
							</div>
							<div class="form-group">
								<label for="discontinued">Discontinued date</label> <input
									value="${computer.disconDate}" type="date" class="form-control"
									id="discontinued" name="disconDate" placeholder="Discontinued date">
							</div>
							<div class="form-group">
								<label for="companyId">Company</label> <select
									class="form-control" name="companyID">
									<option/>
									<c:forEach items="${companys}" var="company">
										<c:choose>
											<c:when test="${company.id == computer.companyID}">
												<option selected value="${company.id}">${company.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${company.id}">${company.name}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
							</div>
						</fieldset>
						<div class="actions pull-right">
							<input type="submit" name="action" value="edit" class="btn btn-primary">
							or <a href="dashboard" class="btn btn-default">Cancel</a>
						</div>
					</form>
				</div>
			</div>
		</div>
	</section>
</body>
<script src="../static/js/jquery.min.js" type="text/javascript"></script>
<script src="../static/js/validationComputer.js" type="text/javascript"></script>
</html>
