package plebiscite.web




class UserController {

	static allowedMethods = [login: "GET", doLogin: "POST"]
	
	def engineService;
	
	def login() {
		
	}

	def logout() {
		session.user = null
		redirect (uri:'/')
	}
	
	def doLogin = {
		def user = null;
		try {
			user = engineService.authenticateUser(params.email, params.password);
			session.user = user
		} catch (BannedUserException bue) {
			flash.error = message(code: 'default.banneduser', default:"You've been banned")
			redirect(controller:'user',action:'login')
		}
		
		if (user)
			redirect(controller:'ballot',action:'list')
		else {
//			flash.message = message(code: 'default.invalidlogin', default:'Invalid Login')
			flash.error = message(code: 'default.invalidlogin', default:'Invalid Login')
			
			
			redirect(controller:'user',action:'login')
		}
	}
}
