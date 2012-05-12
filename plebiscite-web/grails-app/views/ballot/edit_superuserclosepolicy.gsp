<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Add / Edit Super User Policy</title>
	</head>
	<body>

		<div id="edit-su-policy" class="content scaffold-edit" role="main">
			<g:render template="/errors"/>
			<h1>Add / Edit Super User Policy</h1>

			<g:form method="post" controller="ballot" action="policy">
				<fieldset class="form">
<g:hiddenField name="id" value="${ballotInstance?.id }"/>
					<g:hiddenField name="policyType" value="${params.policyType }" />
					<g:hiddenField name="ballotId" value="${ballotInstance.id}"/>
					<g:hiddenField name="policyId" value="${policy?.id }"/>
					
					<div class="fieldcontain ${hasErrors(bean: policy, field: 'user', 'error')} required">
						<label for="user">
							Super User E-Mail Address
							<span class="required-indicator">*</span>
						</label>
						<g:textField name="user" required="true" value="${policy?.user}"/>
					</div>
					<div class="fieldcontain ${hasErrors(bean: policy, field: 'acceptOnYes', 'error')} required">
						<label for="acceptOnYes">
							Accept Ballot on Super User's Yay Vote
							<span class="required-indicator">*</span>
						</label>
						<g:checkBox name="acceptOnYes" value="${policy?.acceptOnYes}"/>
					</div>
					<div class="fieldcontain ${hasErrors(bean: policy, field: 'rejectOnNo', 'error')} required">
						<label for="rejectOnNo">
							Reject Ballot on Super User's Nay Vote
							<span class="required-indicator">*</span>
						</label>
						<g:checkBox name="rejectOnNo" value="${policy?.rejectOnNo}"/>
					</div>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="policy" value="Save" />
					<g:actionSubmit class="cancel" value="Cancel" action="cancelPolicy" />
				</fieldset>
			</g:form>
		</div>

	</body>
</html>
