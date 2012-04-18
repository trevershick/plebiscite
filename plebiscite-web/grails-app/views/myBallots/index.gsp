<!doctype html>
<html>
<head>
<meta name="layout" content="main" />
<title>My Ballots</title>
</head>
<body>
<div>
	<ul>
		<g:each in="${ ballots }" var="b">
			<li>
				${b.title } - ${b.description } @ ${b.state }
			</li>
		</g:each>
	</ul>
</div>
</body>
</html>