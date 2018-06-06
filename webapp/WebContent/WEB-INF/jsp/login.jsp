<!doctype html>
<html lang="en">
  <head>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="icon" href="${pageContext.request.contextPath}/static/images/ComputerDatabase">

    <title>Signin Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
   <link href="${pageContext.request.contextPath}/static/css/bootstrap.min.css" rel="stylesheet"
    media="screen"> 
    <link href="${pageContext.request.contextPath}/static/css/font-awesome.css" rel="stylesheet" media="screen"  type="text/css">
    <link href="${pageContext.request.contextPath}/static/css/main.css" rel="stylesheet" media="screen"  type="text/css">
</head>
  </head>

  <body class="text-center">
  <div class="row">
  <div class="col-xs-6 col-xs-offset-3 box">
    <form class="form-signin" method="POST">
      <img class="mb-4" src="${pageContext.request.contextPath}/static/images/ComputerDatabase.png" alt="" width="72" height="72">
      <h1 class="h3 mb-3 font-weight-normal">Please sign in</h1>
      <label for="inputEmail" class="sr-only">Email address</label>
      <input type="type" id="inputEmail" class="form-control" placeholder="Username" name="username" required autofocus>
      <label for="inputPassword" class="sr-only">Password</label>
      <input type="password" id="inputPassword"  name="password" class="form-control" placeholder="Password" required>
      <div class="checkbox mb-3">
        <label>
          <input type="checkbox" value="remember-me"> Remember me
        </label>
      </div>
      <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
      <p class="mt-5 mb-3 text-muted">&copy; 2018-2019</p>
    </form>
    </div>
    </div>
  </body>
  
</html>