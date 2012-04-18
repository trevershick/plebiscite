<%@ page import="org.trevershick.plebiscite.model.Ballot" %>



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

<div class="fieldcontain ${hasErrors(bean: ballotInstance, field: 'state', 'error')} required">
	<label for="state">
		<g:message code="ballot.state.label" default="State" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="state" required="" value="${ballotInstance?.state}"/>
</div>





