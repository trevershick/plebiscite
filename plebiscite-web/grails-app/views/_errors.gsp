<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>
<g:if test="${flash.error}">
	<ul class="errors" role="alert">
	<li>${flash.error}</li>
	</ul>
</g:if>
