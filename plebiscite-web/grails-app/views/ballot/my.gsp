
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

			<table>
				<thead>
					<tr>
						<g:sortableColumn property="title" title="${message(code: 'ballot.title.label', default: 'Title')}" />
					
						<g:sortableColumn property="description" title="${message(code: 'ballot.description.label', default: 'Description')}" />
					
						<g:sortableColumn property="state" title="${message(code: 'ballot.state.label', default: 'State')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${ballotInstanceList}" status="i" var="ballotInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${ballotInstance.id}">${fieldValue(bean: ballotInstance, field: "title")}</g:link></td>
					
						<td>${fieldValue(bean: ballotInstance, field: "description")}</td>
					
						<td>${fieldValue(bean: ballotInstance, field: "state")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${ballotInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
