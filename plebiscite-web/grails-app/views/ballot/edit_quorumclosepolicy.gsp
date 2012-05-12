<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Add / Edit Quorum Policy</title>
	</head>
	<body>

		<div id="edit-quorum-policy" class="content scaffold-edit" role="main">
			<g:render template="/errors"/>
			<h1>Add / Edit Quorum Policy</h1>

			<g:form method="post" controller="ballot" action="policy">
				<fieldset class="form">
<g:hiddenField name="id" value="${ballotInstance?.id }"/>
					<g:hiddenField name="policyType" value="${params.policyType }" />
					<g:hiddenField name="ballotId" value="${ballotInstance.id}"/>
					<g:hiddenField name="policyId" value="${policy?.id }"/>
					
					<div class="fieldcontain ${hasErrors(bean: policy, field: 'numberRequired', 'error')} required">
						<label for="numberRequired">
							Number Required for Quorum
							<span class="required-indicator">*</span>
						</label>
						<g:textField name="numberRequired" required="true" value="${policy?.numberRequired}"/>
					</div>
					<div class="fieldcontain ${hasErrors(bean: policy, field: 'requiredVotersOnly', 'error')} required">
						<label for="requiredVotersOnly">
							Tally Required Voters Only?
							<span class="required-indicator">*</span>
						</label>
						<g:checkBox name="requiredVotersOnly" value="${policy?.requiredVotersOnly}"/>
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
