<!DOCTYPE html>
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="infouser" uri="/WEB-INF/taglibs/tagInfoUser.tld"%>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="static/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link href="static/css/font-awesome.css" rel="stylesheet" media="screen">
<link href="static/css/main.css" rel="stylesheet" media="screen">
<script src="static/js/jquery.min.js" type="text/javascript"></script>
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
			<a class="navbar-brand" href="dashboard"> Application - Computer
				Database </a>
		</div>
	</header>
	<section id="main">
		<div class="container">
			<div class="row">
				<div class="col-xs-8 col-xs-offset-2 box">
					<h1>Add Computer</h1>
					<form id="addAComputer" action="computer" method="POST">
						<fieldset>
							<div id="divComputerName" class="form-group">
								<label class="control-label" for="computerName">Computer
									name</label> <input required type="text" class="form-control"
									id="computerName" name="computerName"
									placeholder="Computer name">
									<span id='nomObligatoire' class='help-block hidden'>Le nom est obligatoire</span>
							</div>
							<div class="form-group">
								<label for="introduced">Introduced date</label> <input
									type="date" class="form-control" name="introduced"
									placeholder="Introduced date">
							</div>
							<div class="form-group">
								<label for="discontinued">Discontinued date</label> <input
									type="date" class="form-control" name="discontinued"
									placeholder="Discontinued date">
							</div>
							<div class="form-group">
								<label for="companyId">Company</label> <select
									class="form-control" name="companyId">
									<option value="0"></option>
									<c:forEach items="${requestScope['companys']}" var="company">
										<option value="${company.id}">${company.name}</option>
									</c:forEach>
								</select>
							</div>
						</fieldset>
						<div class="actions pull-right">
							<input type="submit" name="action" value="add"
								class="btn btn-primary"> or <a href="dashboard"
								class="btn btn-default">Cancel</a>
						</div>
					</form>
				</div>
			</div>
		</div>
	</section>
</body>
<script type="text/javascript">
	$(document).ready(function() {
		$("#addAComputer").submit(function(e) {
			if($("#computerName").val() == ""){
				e.preventDefault();
				$("#divComputerName").removeClass("has-success")
                $("#divComputerName").addClass("has-error")
                $("#nomObligatoire").removeClass("hidden")
			}
		});
		
		$("#computerName").focusout(function() {
			if ($(this).val() == "") {
				$("#divComputerName").removeClass("has-success")
				$("#nomObligatoire").removeClass("hidden")
				$("#divComputerName").addClass("has-error")
			} else {
				$("#divComputerName").removeClass("has-error")
               $("#nomObligatoire").addClass("hidden")
				$("#divComputerName").addClass("has-success")
			}
		}

		)
	});
</script>
</html>
