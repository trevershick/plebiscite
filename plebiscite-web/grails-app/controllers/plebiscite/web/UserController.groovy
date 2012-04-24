package plebiscite.web

import org.trevershick.plebiscite.engine.*;
import org.trevershick.plebiscite.model.*;

import com.google.common.base.Predicate;




class UserController {

	static allowedMethods = [
		register:"GET",
		doRegister:"POST", 
		login: "GET", 
		doLogin: "POST",
		logout:"GET"]
	
	Engine engine; // number 9
	
	def beforeInterceptor = [
		action:this.&checkUserIsAdmin
	]
	
	def checkUserIsAdmin() {
		if(!session.user) {
			// i.e. user not logged in
			redirect(controller:'user',action:'login')
			return false
		}
		if(!session.user.admin) {
			// i.e. user not logged in
			flash.error = message(code: 'default.mustbeadmin', default:'You must be an admin user')
			redirect(controller:'users',action:'list')
			return false
		}

	}
	def index() {
		redirect(action: "list", params: params)
	}
	
	def list() {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		
		def users = []
		def p = new Predicate<User>(){
			public boolean apply(User b) {
				users += b;
			}
		};
	 
		def criteria = new UserCriteria();
		
		engine.userListForAdmin(session.user, criteria, p);
		
		[
			userInstanceList: users,
			userInstanceTotal: users.size
		]
	}



	def show() {
		
		def userInstance = engine.getUser(params.id);
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}

		[userInstance: userInstance]
	}

	def edit() {
		def userInstance = engine.getUser(params.id);

		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}

		[userInstance: userInstance]
	}

	def update() {
		def userInstance = engine.getUser(params.id);
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}

		flash.message = message(code: 'not.implemented', args: [message(code: 'user.label', default: 'User'), userInstance.id])
		redirect(action: "show", id: userInstance.id)
	}

	def delete() {
		def userInstance = engine.getUser(params.id);
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
			return
		}

		try {
			dataService.delete(userInstance)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "list")
		}
		catch (Exception e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])
			redirect(action: "show", id: params.id)
		}
	}

}
