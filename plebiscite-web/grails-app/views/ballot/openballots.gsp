
<%@page import="org.trevershick.plebiscite.model.BallotState"%>
<%@ page import="org.trevershick.plebiscite.model.Ballot" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'ballot.label', default: 'Ballot')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<style>
		
		</style>
	</head>
	<body>
		<div id="list-ballot" class="content scaffold-list" role="main">
			<g:render template="/errors"/>
			<h1>Open Ballots</h1>

				<g:each in="${ballotInstanceList}" status="i" var="ballotInstance">
					<div class="ballot-box ${ myVotes.get(ballotInstance)?.type }">
					<h1><g:link action="show" id="${ballotInstance.id}">${fieldValue(bean: ballotInstance, field: "title")}</g:link></h1>
					<p>
						<g:link action="vote" id="${ballotInstance.id }">Vote on this Ballot</g:link>
					</p>
					<p>${fieldValue(bean: ballotInstance, field: "description")}</p>
					</div>
				</g:each>

			
		</div>
	</body>
</html>
