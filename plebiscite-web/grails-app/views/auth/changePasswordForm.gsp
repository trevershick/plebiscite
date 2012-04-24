<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<title><g:message code="default.changepassword.title" default="Change Your Password" /></title>
</head>
<body>
	<div class="body">
		<g:render template="/errors" />

		<div id="user-changepassword" class="content scaffold-edit" role="main">
			<h1>
				<g:message code="default.changepassword.header" default="Change Password" />
			</h1>



			<g:form controller="auth" action="changepassword" method="POST">
				<fieldset class="form">
					<div class="fieldcontain required">
						<label for="password"> <g:message code="user.password.label"
								default="Password" /> <span class="required-indicator">*</span>
						</label>
						<g:passwordField name="password" required="true" value="" />
					</div>
					<div class="fieldcontain required">
						<label for="confirmPassword"> <g:message code="user.password.again.label"
								default="Password Again" /> <span class="required-indicator">*</span>
						</label>
						<g:passwordField name="confirmPassword" required="true" value="" />
					</div>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="submitButton" class="login" value="${message(code: 'default.button.changepassword.label', default: 'Change Password')}" />
				</fieldset>
			</g:form>
		</div>
	</div>
</body>
</html>
