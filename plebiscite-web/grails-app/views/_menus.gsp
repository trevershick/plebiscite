		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<g:if test="${ session.user?.admin }">
				<li><g:link controller="ballot" class="list" action="list">Ballots</g:link></li>
				</g:if>
				<li><g:link controller="ballot" class="list" action="openballots">Open Ballots</g:link></li>
				
				<g:if test="${ session.user }">
				<li><g:link controller="ballot" class="list" action="my">My Ballots</g:link></li>
				<li><g:link controller="ballot" class="list" action="ivotedon">Ballots I Voted On</g:link></li>
				<li><g:link controller="auth" action="logout" class="logout"><g:message code="default.logout.label" default="Logout"/></g:link></li>
				</g:if>
				<g:if test="${ !session.user }">
				<li><g:link controller="auth" action="login"><g:message code="default.login.label" default="Login" /></g:link></li>
				<li><g:link controller="auth" action="register"><g:message code="default.register.label" default="Register" /></g:link></li>
				<li><g:link controller="auth" action="forgotpassword"><g:message code="default.forgotpassword.label" default="Forgot Password" /></g:link></li>
				</g:if>
			</ul>
		</div>
