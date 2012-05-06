
<%@page import="org.trevershick.plebiscite.model.BallotState"%>
<%@ page import="org.trevershick.plebiscite.model.Ballot" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'ballot.label', default: 'Ballot')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div id="list-ballot" class="content scaffold-list" role="main">
			<g:render template="/errors"/>
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>

				<g:each in="${ballotInstanceList}" status="i" var="ballotInstance">
					<div class="ballot-box ballot-box-${ ballotInstance.value?.type }">
					<h1><g:link action="show" id="${ballotInstance.key.id}">${fieldValue(bean: ballotInstance.key, field: "title")}</g:link></h1>
					<p class="${ ballotInstance.value?.type }">
						<g:link controller="Ballot" action="vote" id="${ballotInstance.key.id}">
						You voted ${fieldValue(bean: ballotInstance.value, field: "type")}
						</g:link>
					</p>
					<p class="${fieldValue(bean: ballotInstance.key, field: "state")}">This ballot is ${ballotInstance.key.state}</p>
					<p>${fieldValue(bean: ballotInstance.key, field: "description")}</p>
					</div>
				</g:each>

			
		</div>
	</body>
</html>
