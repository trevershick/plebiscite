
<%@ page import="org.trevershick.plebiscite.model.Ballot" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'ballot.label', default: 'Ballot')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		
		<div id="show-ballot" class="content scaffold-show" role="main">
			<g:render template="/errors"/>
			
						<g:if test="${myvote }">
				<div class="message" role="status">You voted '${myvote.type }' on this ballot on ${myvote.when }</div>
			</g:if>
			
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<ul class="property-list ballot">
			
				<g:if test="${ballotInstance?.title}">
				<li class="fieldcontain">
					<span id="title-label" class="property-label"><g:message code="ballot.title.label" default="Title" /></span>
					
						<span class="property-value" aria-labelledby="title-label"><g:fieldValue bean="${ballotInstance}" field="title"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ballotInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="ballot.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${ballotInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${ballotInstance?.state}">
				<li class="fieldcontain">
					<span id="state-label" class="property-label"><g:message code="ballot.state.label" default="Status" /></span>
					<span class="property-value" aria-labelledby="state-label"><g:fieldValue bean="${ballotInstance}" field="state"/></span>					
				</li>
				</g:if>

				<li class="fieldcontain">
					<span id="state-label" class="property-label"><g:message code="ballot.open.label" default="Open Ballot?" /></span>
					<span class="property-value" aria-labelledby="openballot-label">
					<g:formatBoolean boolean="${ballotInstance?.openBallot }" false="No" true="Yes"/>
					</span>					
				</li>

				<li class="fieldcontain">
					<span id="state-label" class="property-label"><g:message code="ballot.votechangeable.label" default="Votes Changeable?" /></span>
					<span class="property-value" aria-labelledby="voteschangeable-label">
						<g:formatBoolean boolean="${ballotInstance?.voteChangeable }" false="No" true="Yes"/>
					</span>					
				</li>
				<g:if test="${ballotInstance?.expirationDate }">
				<li class="fieldcontain">
					<span id="expirationdate-label" class="property-label"><g:message code="ballot.expirationdate.label" default="Expiration Date" /></span>
					<span class="property-value" aria-labelledby="expirationdate-label">
						<g:formatDate date="${ballotInstance?.expirationDate }" />
					</span>					
				</li>
				</g:if>
				<li class="fieldcontain">
					<span id="owner-label" class="property-label"><g:message code="ballot.owner.label" default="Owner" /></span>
					<span class="property-value" aria-labelledby="owner-label">
					${ballotInstance.owner }
					</span>					
				</li>
				<g:if test="${ballotInstance?.policies }">
				<li>
					<h1>Policies</h1>
					<table>
						<thead>
							<tr><th>Policy Type</th><th>Description</th></tr>
						</thead>
						<tbody>
							<g:each var="policy" in="${policies? }">
							<tr>
								<td>${policy.class.simpleName}</td>
								<td>${policy.description}</td>
							</tr>
							</g:each>
						</tbody>
					</table>
				</li>
				</g:if>
				<g:if test="${showVoters && votes}">
				<li class="fieldcontain">
					<h1>Voters</h1>
					<table>
						<thead>
							<tr><th>User</th><th>Required</th><th>Vote</th><th>When</th></tr>
						</thead>
						<tbody>
							<g:each var="vote" in="${votes }">
							<tr>
								<td>${vote.userId }</td>
								<td><g:formatBoolean boolean="${vote.required }" false="Nope" true="Yep" /></td>
								<td>${vote.type }</td>
								<td>${vote.when }</td>
							</tr>
							</g:each>
						</tbody>
					</table>
				
				</li>
				</g:if>
			</ul>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${ballotInstance?.id}" />
					<g:if test="${ballotInstance?.state.openable }">
						<g:link class="edit" action="edit" id="${ballotInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					</g:if>
					<g:if test="${ballotInstance?.state.cancellable }">
						<g:actionSubmit class="delete" action="cancel" value="${message(code: 'default.button.cancel.label', default: 'Cancel')}" onclick="return confirm('${message(code: 'default.button.cancel.confirm.message', default: 'Are you sure?')}');" />
					</g:if>
					
					<g:if test="${ballotInstance?.state.openable }">
						<g:actionSubmit class="delete" action="open" value="${message(code: 'default.button.open.label', default: 'Start')}" onclick="return confirm('${message(code: 'default.button.open.confirm.message', default: 'Are you sure?')}');" />
					</g:if>
				</fieldset>
			</g:form>
		</div>
		<disqus:comments bean="${ballotInstance}" />
	</body>
</html>
