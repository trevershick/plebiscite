
<%@ page import="org.trevershick.plebiscite.model.User" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'user')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-user" class="content scaffold-list" role="main">
			<g:render template="/errors"/>
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>

			<table>
				<thead>
					<tr>
						<g:sortableColumn property="emailAddress" title="${message(code: 'user.email.label', default: 'E-Mail Address')}" />
						<g:sortableColumn property="admin" title="${message(code: 'user.admin.label', default: 'Admin')}" />
						<g:sortableColumn property="registered" title="${message(code: 'user.registered.label', default: 'Registered')}" />
						<g:sortableColumn property="userStatus" title="${message(code: 'user.status.label', default: 'Status')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${userInstanceList}" status="i" var="userInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${userInstance.emailAddress}">${fieldValue(bean: userInstance, field: "emailAddress")}</g:link></td>
						<td>${fieldValue(bean: userInstance, field: "admin")}</td>
						<td>${fieldValue(bean: userInstance, field: "registered")}</td>
						<td>${fieldValue(bean: userInstance, field: "userStatus")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
<!-- 			<div class="pagination">
				<g:paginate total="${userInstanceTotal}" />
			</div> -->
		</div>
	</body>
</html>
