package org.trevershick.plebiscite

import org.codehaus.groovy.grails.web.mapping.LinkGenerator;
import org.trevershick.plebiscite.engine.*;
import org.trevershick.plebiscite.model.*;

import com.google.common.base.Predicate;


class AuthController {
	Engine engine; // number 9
	
	LinkGenerator grailsLinkGenerator
	
	def beforeInterceptor = [action: this.&checkUser]
	
	def checkUser() {
		if (session.user && !session.user.emailVerified) {
			flash.message = message(code:'please.verify.link',
				args:[ grailsLinkGenerator.link(controller:"auth", action:"reverify") ],
					default:"Please verify your e-mail <a href=\"{0}\">here</a>" )
		}
		if (session.user && !session.user.registered) {
			flash.message = message(code:'please.setpassword.link',
				args:[ grailsLinkGenerator.link(controller:"auth", action:"changepassword") ],
					default:"Please change your password <a href=\"{0}\">here</a>" )
		}
	}

	
	def verify() {
		def em = params.em;
		def tk = params.tk;
		if (!engine.verifyEmail(em,tk)) {
			flash.error = message(code: 'default.didntworkreconfirm', default:'The confirmation didn\'t work. Try again.')
			return redirect(action:'reverify')
		}
		
		session.user = engine.getUser(em)
		if (session.user.hasPassword()) {
			flash.message = message(code: 'default.success', default:'Successfully confirmed your e-mail address.')
			return redirect(uri:'/')
		}

		// user has no password
		flash.message = message(code: 'default.needpassword', default:'You have successfully verified your e-mail but you have no password, please establish one.')
		redirect(action:'changepassword')
	}

	def reverify() {
		if (request.method == "GET") {
			render(view: "reverify")
			return;
		}
		def emailParameters = [
			site: "Plebiscite",
			loginLink : grailsLinkGenerator.link( controller : "auth", action : "login", absolute : true),
			verifyLink : grailsLinkGenerator.link( controller : "auth", action : "verify", absolute : true),
			verifyLinkParams : [ email:"em",token:"tk" ]
			]
		
		engine.sendEmailVerificationEmail(params.email, emailParameters)
		render(view: "verificationEmailIsComing")
	}

	def register() {
		if (request.method == "GET") {
			render(view: "register")
			return;
		}

		try {
			def emailParameters = [
				site: "Plebiscite",
				loginLink : grailsLinkGenerator.link( controller : "auth", action : "login", absolute : true),
				verifyLink : grailsLinkGenerator.link( controller : "auth", action : "verify", absolute : true),
				verifyLinkParams : [ email:"em",token:"tk" ]
				]
	
			engine.registerUser(params.email, emailParameters)
			render(view: "verificationEmailIsComing")
		} catch (AlreadyExistsException aee) {
			flash.error = message(code: 'default.yourealreadyregistered', default:'You\'re already registered.')
			redirect(controller:'auth',action:'login')
		}

	}

	def logout() {
		session.user = null
		redirect (uri:'/')
	}


	def login() {
		if (request.method == "GET") {
			render(view: "login")
			return;
		}

		def user = null;
		try {
			user = engine.authenticate(params.email, params.password);
			session.user = user
		} catch (bue) {
			flash.error = message(code: 'default.banneduser', default:"You've been banned : " + bue.message)
			redirect(controller:'auth',action:'login')
			return;
		}

		if (user) {
			redirect(uri:'/')
		} else {
			flash.error = message(code: 'default.invalidlogin', default:'Invalid Login')
			render(view:"login")
		}
	}


	def changepassword() {
		if (request.method == "GET") {
			render(view: "changePasswordForm")
			return;
		}
		engine.changePassword(session.user, params.password);
		flash.message = message(code: 'default.passwordchanged', default:"Your password has been changed.")
		redirect(uri:'/')
		return;

	}

	def forgotpassword() {
		if (request.method == "GET") {
			render(view: "forgotPasswordForm")
			return;
		}
		def user=engine.getUser(params.email)
		if (user) {
			def emailParameters = [
				site: "Plebiscite",
				loginLink : grailsLinkGenerator.link( controller : "auth", action : "login", absolute : true),
				verifyLink : grailsLinkGenerator.link( controller : "auth", action : "verify", absolute : true),
				verifyLinkParams : [ email:"em",token:"tk" ]
				]

			engine.sendTemporaryPassword(params.email, emailParameters);
			render(view: "temporaryPasswordIsComing")
		} else {
			flash.error = message(code:'yourenotregistered', default:"You're not a user yet")
			redirect(controller:"auth",action:"register")
		}
	}
}
