<%@ page import="org.trevershick.plebiscite.model.Ballot"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">

<title><g:message code="default.register.label" default="Register" /></title>
</head>
<body>
	<div class="body">
		<g:render template="/errors" />

		<div id="user-register" class="content scaffold-edit" role="main">
			<h1>
				<g:message code="default.register.label" default="Register" />
			</h1>



			<g:form controller="auth" action="register" method="POST">
				<fieldset class="form">
					<div class="fieldcontain required">
						<label for="email"> <g:message code="user.email.label"
								default="E-Mail" /> <span class="required-indicator">*</span>
						</label>
						<g:textField name="email" required="true" value="" />
					</div>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="submitButton" class="register" value="${message(code: 'default.button.register.label', default: 'Register')}" />
				</fieldset>
			</g:form>
		</div>
	</div>
</body>
</html>
