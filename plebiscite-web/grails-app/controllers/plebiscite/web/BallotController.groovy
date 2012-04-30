package plebiscite.web

import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.User;


import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.trevershick.plebiscite.engine.*;
import org.trevershick.plebiscite.model.*;

import com.google.common.base.Predicate;

class BallotController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def beforeInterceptor = [action: this.&checkUser]
	
	LinkGenerator grailsLinkGenerator
	def checkUser() {
		if(!session.user) {
			// i.e. user not logged in
			redirect(controller:'auth',action:'login')
			return false
		}
		if (!session.user.emailVerified) {
			flash.message = message(code:'please.verify.link', 
				args:[ grailsLinkGenerator.link(controller:"auth", action:"reverify") ], 
					default:"Please verify your e-mail <a href=\"{0}\">here</a>" )
		}
	}
	
	
	
	Engine engine;
	DataService dataService;
	
    def index() {
        redirect(action: "list", params: params)
    }

	def openballots() {
		def ballots = []
		def p = new Predicate<Ballot>(){
			public boolean apply(Ballot b) {
				ballots += b;
			}
		};
		try {
			engine.ballotsThatAreOpen(p);
		} catch (SecurityException se) {
			flash.error = message(code: 'default.security.message', default:"You don't have access to this... sorry.")
			return redirect(uri:'/')
		}
		[
			ballotInstanceList: ballots,
			ballotInstanceTotal: ballots.size
		]
	}
	
	def ivotedon() {
		def ballots = []
		def p = new Predicate<Ballot>(){
			public boolean apply(Ballot b) {
				ballots += b;
			}
		};
		try {
			engine.ballotsIVotedOn(session.user, p);
		} catch (SecurityException se) {
			flash.error = message(code: 'default.security.message', default:"You don't have access to this... sorry.")
			return redirect(uri:'/')
		}
		[
			ballotInstanceList: ballots,
			ballotInstanceTotal: ballots.size
		]

	}

	
	def my() {
		def ballots = []
		def p = new Predicate<Ballot>(){
			public boolean apply(Ballot b) {
				ballots += b;
			}
		};
		try {
			engine.ballotsIOwn(session.user, p);
		} catch (SecurityException se) {
			flash.error = message(code: 'default.security.message', default:"You don't have access to this... sorry.")
			return redirect(uri:'/')
		}
		[
			ballotInstanceList: ballots,
			ballotInstanceTotal: ballots.size
		]

	}
	
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
		
		session.states = params.list('states')
		if (!session.states) session.states = ['Open','Closed'] 
		
		
		def ballots = []
		def criteria = new BallotCriteria();
		session.states.each { criteria.addState (BallotState.valueOf(it))}
		
		
		
		
		def p = new Predicate<Ballot>(){
			public boolean apply(Ballot b) {
				ballots += b;
			}
		};
		try {
			engine.ballotListForAdmin(session.user, criteria, p);
		} catch (SecurityException se) {
			flash.error = message(code: 'default.security.message', default:"You don't have access to this... sorry.")
			return redirect(uri:'/')
		}
		
        [
			ballotInstanceList: ballots,
			ballotInstanceTotal: ballots.size
		]
    }

    def create() {


    }

    def save() {
		def b = engine.createBallot(session.user, params.title)
		b.setDescription(params.description ?: "No Description");
		b.openBallot = params.openBallot ?: false
		b.voteChangeable = params.voteChangeable ?: false
		b.expirationDate = params.expirationDate
		engine.updateBallot(session.user, b)

		flash.message = message(code: 'default.created.message', args: [message(code: 'ballot.label', default: 'Ballot'), b.id])
        redirect(action: "edit", id: b.id)
    }

    def show() {
		
        def ballotInstance = dataService.getBallot(params.id);
        if (!ballotInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "list")
            return
        }

		def votes = []
		def p = new Predicate<Vote>(){
			public boolean apply(Vote b) {
				votes += b;
			}
		};
		engine.votes(ballotInstance, p);
		
        [ballotInstance: ballotInstance,
			votes: votes]
    }

	def cancel() {
		def ballot = engine.getBallot(params.id)
		if (!ballot.owner == session.user.emailAddress) {
            flash.error = message(code: 'default.notyours.error', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "show")
		}
		
		engine.cancel(ballot)
		redirect(action: "list")
	}
	
    def edit() {
        def ballotInstance = engine.getBallot(params.id);

        if (!ballotInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "list")
            return
        }
		if (request.method == "POST") {
			apply(ballotInstance)
		}
		def votes = []
		def p = new Predicate<Vote>(){
			public boolean apply(Vote b) {
				votes += b;
			}
		};
		engine.votes(ballotInstance, p);

        [ballotInstance: ballotInstance,
			votes:votes]
    }
	
	def apply(ballotInstance) {
		if (params.email) {
			engine.addUserToBallot(ballotInstance, params.email, params.emailRequired ? true : false)
		}
		params.list('removeEmail').each {
			engine.removeUserFromBallot(ballotInstance, it)
		}
		
	}

    def update() {
        def ballotInstance = dataService.getBallot(params.id);
		if (!ballotInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "list")
            return
        }

//        if (params.version) {
//            def version = params.version.toLong()
//            if (ballotInstance.version > version) {
//                ballotInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
//                          [message(code: 'ballot.label', default: 'Ballot')] as Object[],
//                          "Another user has updated this Ballot2 while you were editing")
//                render(view: "edit", model: [ballotInstance: ballotInstance])
//                return
//            }
//        }
//
//        ballotInstance.properties = params

//        if (!ballotInstance.save(flush: true)) {
//            render(view: "edit", model: [ballotInstance: ballotInstance])
//            return
//        }
		flash.message = message(code: 'not.implemented', args: [message(code: 'ballot.label', default: 'Ballot'), ballotInstance.id])
//		flash.message = message(code: 'default.updated.message', args: [message(code: 'ballot.label', default: 'Ballot'), ballotInstance.id])
        redirect(action: "show", id: ballotInstance.id)
    }

    def delete() {
        def ballotInstance = dataService.getBallot(params.id);
        if (!ballotInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "list")
            return
        }

        try {
            dataService.delete(ballotInstance)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "list")
        }
        catch (Exception e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
