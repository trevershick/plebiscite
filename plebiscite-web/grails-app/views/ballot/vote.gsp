<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>Vote on ${ballotInstance.title }</title>
	</head>
	<body>
		<div class="content">
		<g:form action="vote" method="POST" id="${ballotInstance.id }">
		<h1>${ballotInstance.title }</h1>
		<p>${ballotInstance.description }</p>
		
		<div class="chit yay-chit">
			<h1>Vote Yay</h1>
			<g:radio name="type" value="Yay" checked="${myvote.type.isYay() }"  class="radio" />
		</div>
		<div class="chit nay-chit">
			<h1>Vote Nay</h1>
			<g:radio name="type" value="Nay" checked="${myvote.type.isNay() }"  class="radio" />
		</div>
		<div class="chit abstain-chit">
			<h1>Abstain</h1>
			<g:radio name="type" value="Abstain" checked="${myvote.type.isAbstain() }" class="radio" />
		</div>
		<div class="chit none-chit">
			<h1>No Vote</h1>
			<g:radio name="type" value="None" checked="${myvote.type.isNone() }"  class="radio" />
		</div>
		<fieldset class="buttons">
			<g:actionSubmit class="save" action="vote" value="Cast My Vote" />
			<g:actionSubmit class="cancel" action="ivotedon" value="Cancel"/>
		</fieldset>
		
		</g:form>
		</div>

	</body>
</html>
