<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'ballot2.label', default: 'Ballot2')}" />
		<title><g:message code="default.edit.label" args="[ballotInstance.title]" /></title>
	</head>
	<body>

		<div id="edit-ballot" class="content scaffold-edit" role="main">
			<g:render template="/errors"/>
			<h1><g:message code="default.edit.label" args="[ballotInstance.title]" /></h1>


			<g:hasErrors bean="${ballotInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${ballotInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form method="post" id="${ballotInstance.id }">
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="edit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					<g:actionSubmit class="cancel" action="show" value="Done"/>
				</fieldset>
			</g:form>
		</div>

	</body>
</html>
