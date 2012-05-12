<%@ page import="org.trevershick.plebiscite.model.Ballot" %>
<style>
textarea {
	width:400px;
	height:2em;
}</style>
<g:hiddenField name="id" value="${ballotInstance?.id }"/>
<div class="fieldcontain ${hasErrors(bean: ballotInstance, field: 'title', 'error')} required">
	<label for="title">
		<g:message code="ballot.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="title" required="" value="${ballotInstance?.title}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ballotInstance, field: 'description', 'error')} required">
	<label for="description">
		<g:message code="ballot.description.label" default="Description" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="description" required="true" value="${ballotInstance?.description}" />
</div>

<div class="fieldcontain ${hasErrors(bean: ballotInstance, field: 'openBallot', 'error')} required">
	<label for="state">
		<g:message code="ballot.open.label" default="Open Ballot?" />
		<span class="required-indicator">*</span>
	</label>
	<g:checkBox name="openBallot" value="${ballotInstance?.openBallot }"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ballotInstance, field: 'voteChangeable', 'error')} required">
	<label for="state">
		<g:message code="ballot.votechangeable.label" default="Votes are Changeable" />
		<span class="required-indicator">*</span>
	</label>
	<g:checkBox name="voteChangeable" value="${ballotInstance?.voteChangeable }"/>
</div>
<div class="fieldcontain">
	<label for="ballotExpires">
		Ballot Expires
	</label>
	<g:checkBox name="ballotExpires" value="${ballotInstance?.expires() }" />
</div>
<div class="fieldcontain ${hasErrors(bean: ballotInstance, field: 'expirationDate', 'error')} required">
	<label for="description">
		<g:message code="ballot.expirationdate.label" default="Expiration Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="expirationDate" value="${ballotInstance?.expirationDate }"/>
</div>
<g:if test="${ballotInstance?.id }">
<div>
	<h2>Policies</h2>
	<table>
		<thead>
			<tr><th>Policy Type</th><th>Description</th><th></th></tr>
		</thead>
		<tbody>
			<g:each var="policy" in="${ballotInstance?.policies }">
			<tr>
				<td>${policy.class.simpleName}</td>
				<td>${policy.description}</td>
				<td><g:link controller="ballot" action="policy" params="[policyId: policy.id, ballotId: ballotInstance.id, policyType:policy.class.simpleName.toLowerCase() ]">Edit</g:link></td>
			</tr>
			</g:each>
		</tbody>
	</table>
	<g:link controller="ballot" action="policy" params="[ballotId: ballotInstance.id, policyType:'quorumclosepolicy' ]">Add Quorum</g:link>
	<g:link controller="ballot" action="policy" params="[ballotId: ballotInstance.id, policyType:'timeoutpolicy' ]">Add Timeout</g:link>
	<g:link controller="ballot" action="policy" params="[ballotId: ballotInstance.id, policyType:'superuserclosepolicy' ]">Add Super User</g:link>
</div>

<div class="fieldcontain">
	<h2>Voters</h2>
	<table>
		<thead>
			<tr><th style="width:10px;">Remove</th><th>User</th><th>Required</th></tr>
		</thead>
		<tbody>
			<g:each var="vote" in="${votes }">
			<tr>
				<td><g:checkBox name="removeEmail" value="${vote.userId }" checked="false"/></td>
				<td>${vote.userId }</td>
				<td><g:formatBoolean boolean="${vote.required }" false="Nope" true="Yep" /></td>
			</tr>
			</g:each>
			<tr>
				<td></td>
				<td><g:textField name="email" value=""/></td>
				<td><g:checkBox name="emailRequired" value="true"/>
				<g:actionSubmit value="add" class="save" action="edit"/>
				</td>
			</tr>
		</tbody>
	</table>
</div>
</g:if>
