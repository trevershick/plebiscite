
<%@ page import="org.trevershick.plebiscite.model.Ballot" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'ballot.label', default: 'ballot')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-ballot" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-ballot" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
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