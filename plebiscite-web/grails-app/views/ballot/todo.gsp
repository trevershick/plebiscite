
<%@page import="org.trevershick.plebiscite.model.BallotState"%>
<%@ page import="org.trevershick.plebiscite.model.Ballot" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		
		<title>Ballots I Need to Vote On</title>
		<style>
		
		</style>
	</head>
	<body>
		<div id="list-ballot" class="content scaffold-list" role="main">
			<g:render template="/errors"/>
			<h1>Ballots I Need to Vote On</h1>

				<g:each in="${ballotInstanceList}" status="i" var="ballotInstance">
					<div class="ballot-box">
					<h1><g:link action="show" id="${ballotInstance.key.id}">${fieldValue(bean: ballotInstance.key, field: "title")}</g:link></h1>
					<p>${fieldValue(bean: ballotInstance.key, field: "description")}</p>
					</div>
				</g:each>

			
		</div>
	</body>
</html>
