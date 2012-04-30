<%@ page import="org.trevershick.plebiscite.model.Ballot" %>

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
	<g:textField name="description" required="" value="${ballotInstance?.description}"/>
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

<div class="fieldcontain ${hasErrors(bean: ballotInstance, field: 'expirationDate', 'error')} required">
	<label for="description">
		<g:message code="ballot.expirationdate.label" default="Expiration Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="expirationDate" value="${ballotInstance?.expirationDate }"/>
</div>
<g:if test="${ballotInstance?.id }">
<div class="fieldcontain">
	<label for="description">
		<g:message code="ballot.voters.label" default="Voters" />
		<span class="required-indicator">*</span>
	</label>
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
				<td><g:checkBox name="emailRequired" value=""/>
				<g:actionSubmit value="add" class="save" action="edit"/>
				</td>
			</tr>
		</tbody>
	</table>
</div>
</g:if>
