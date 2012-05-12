<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Add / Edit Timeout Policy</title>
	</head>
	<body>

		<div id="edit-su-policy" class="content scaffold-edit" role="main">
			<g:render template="/errors"/>
			<h1>Add / Edit Timeout Policy</h1>

			<g:form method="post" controller="ballot" action="policy">
				<fieldset class="form">
					<g:hiddenField name="id" value="${ballotInstance?.id }"/>
					<g:hiddenField name="policyType" value="${params.policyType }" />
					<g:hiddenField name="ballotId" value="${ballotInstance.id}"/>
					<g:hiddenField name="policyId" value="${policy?.id }"/>
					
					<div class="fieldcontain ${hasErrors(bean: policy, field: 'stateOnTimeout', 'error')} required">
						<label for="stateOnTimeout">
							Ballot State to Transition to on Timeout
							<span class="required-indicator">*</span>
						</label>
						<g:select name="stateOnTimeout" from="${['Cancelled','Accepted','Rejected','TimedOut']}" multiple="true" value="${policy.stateOnTimeout.name()}"/>
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
