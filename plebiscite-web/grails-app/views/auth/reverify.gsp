<%@ page import="org.trevershick.plebiscite.model.Ballot"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<title><g:message code="default.reverify.title"
		default="Reverify E-Mail" /></title>
</head>
<body>
	<div class="body">
		<g:render template="/errors" />

		<div id="email-reverify" class="content scaffold-edit" role="main">
			<h1>
				<g:message code="default.reverify.header" default="Reverify E-Mail" />
			</h1>



			<g:form controller="auth" action="reverify" method="POST">
				<fieldset class="form">
					<div class="fieldcontain required">
						<label for="email"> <g:message code="user.email.label"
								default="E-Mail" /> <span class="required-indicator">*</span>
						</label>
						<g:textField name="email" required="true"
							value="${session.user? session.user.emailAddress : params.em }" />
					</div>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="submitButton" class="login"
						value="${message(code: 'default.reverify.button.label', default: 'Send Verification E-Mail')}" />
				</fieldset>
			</g:form>
		</div>
	</div>
</body>
</html>
