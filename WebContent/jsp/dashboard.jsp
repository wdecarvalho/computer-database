<!DOCTYPE html>
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="pagination" uri="/WEB-INF/taglibs/custom.tld"%>
<%@ taglib prefix="infouser" uri="/WEB-INF/taglibs/tagInfoUser.tld"%>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="utf-8">
<!-- Bootstrap -->
<link href="static/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link href="static/css/font-awesome.css" rel="stylesheet" media="screen">
<link href="static/css/main.css" rel="stylesheet" media="screen">
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
			<h1 id="homeTitle">${requestScope.nbComputers} Computers found</h1>
			<div id="actions" class="form-horizontal">
				<div class="pull-left">
					<form id="searchForm" action="#" method="GET" class="form-inline">

						<input type="search" id="searchbox" name="search"
							class="form-control" placeholder="Search name" /> <input
							type="submit" id="searchsubmit" value="Filter by name"
							class="btn btn-primary" />
					</form>
				</div>
				<div class="pull-right">
					<a class="btn btn-success" id="addComputer"
						href="computer?action=addForm">Add Computer</a> <a
						class="btn btn-default" id="editComputer" href="#"
						onclick="$.fn.toggleEditMode();">Edit</a>
				</div>
			</div>
		</div>

		<form id="deleteForm" action="computer" method="POST">
			<input type="hidden" name="selection" value="">
				<input type="hidden" name="action" value="delete"/>
		</form>

		<div class="container" style="margin-top: 10px;">
			<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<!-- Variable declarations for passing labels as parameters -->
						<!-- Table header for Computer Name -->

						<th class="editMode" style="width: 60px; height: 22px;"><input
							type="checkbox" id="selectall" /> <span
							style="vertical-align: top;"> - <a href="#"
								id="deleteSelected" onclick="$.fn.deleteSelected();"> <i
									class="fa fa-trash-o fa-lg"></i>
							</a>
						</span></th>
						<th>Computer name</th>
						<th>Introduced date</th>
						<!-- Table header for Discontinued Date -->
						<th>Discontinued date</th>
						<!-- Table header for Company -->
						<th>Company</th>

					</tr>
				</thead>
				<!-- Browse attribute computers -->
				<tbody id="results">
					<c:forEach var="computer" items="${requestScope['computers']}">
						<tr>
							<td class="editMode"><input type="checkbox" name="cb"
								class="cb" value="${computer.id}"></td>
							<td><a href="computer?id=${computer.id}&action=editForm"
								onclick="">${computer.name}</a></td>
							<td>${computer.introDate}</td>
							<td>${computer.disconDate}</td>
							<td>${computer.companyName}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</section>

	<footer class="navbar-fixed-bottom">
		<div class="container text-center">

			<pagination:pagination limit="${requestScope.limit}"
				pageCourante="${requestScope.pageCourante}" />

			<div class="btn-group btn-group-sm pull-right" role="group">
				<form id="sendNumberResult" action="dashboard" method="get">
					<input hidden name="page" value="${requestScope.pageCourante}" />
				</form>
				<button form="sendNumberResult" type="submit" name="numberResult"
					value="10"
					class="<c:if test="${sessionScope.numberResult == 10}">active</c:if> btn btn-default">10</button>
				<button form="sendNumberResult" type="submit" name="numberResult"
					value="50"
					class="<c:if test="${sessionScope.numberResult == 50}">active</c:if> btn btn-default">50</button>
				<button form="sendNumberResult" type="submit" name="numberResult"
					value="100"
					class="<c:if test="${sessionScope.numberResult == 100}">active</c:if> btn btn-default">100</button>
			</div>
		</div>
	</footer>
	<script src="static/js/jquery.min.js"></script>
	<script src="static/js/bootstrap.min.js"></script>
	<script src="static/js/dashboard.js"></script>

</body>
</html>
