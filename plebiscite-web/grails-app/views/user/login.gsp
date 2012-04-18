
<%@ page import="org.trevershick.plebiscite.model.Ballot"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'ballot.label', default: 'ballot')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<div class="body">
		<g:render template="/errors" />

		<div id="user-login" class="content scaffold-edit" role="main">
			<h1>
				<g:message code="default.login.label" default="Log In" />
			</h1>



			<g:form controller="user" action="doLogin" method="POST">
				<fieldset class="form">
					<div class="fieldcontain required">
						<label for="email"> <g:message code="user.email.label"
								default="E-Mail" /> <span class="required-indicator">*</span>
						</label>
						<g:textField name="email" required="true" value="" />
					</div>
					<div class="fieldcontain required">
						<label for="password"> <g:message code="user.password.label"
								default="Password" /> <span class="required-indicator">*</span>
						</label>
						<g:passwordField name="password" required="true" value="" />
					</div>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="submitButton" class="login" value="${message(code: 'default.button.login.label', default: 'Login')}" />
				</fieldset>
			</g:form>
		</div>
	</div>
</body>
</html>
