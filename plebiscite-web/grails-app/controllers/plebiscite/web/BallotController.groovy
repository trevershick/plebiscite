package plebiscite.web

import org.trevershick.plebiscite.engine.*;
import org.trevershick.plebiscite.model.*;

import com.google.common.base.Predicate;

class BallotController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def beforeInterceptor = [action:this.&checkUser]
	
	def checkUser() {
		if(!session.user) {
			// i.e. user not logged in
			redirect(controller:'user',action:'login')
			return false
		}
	}
	
	
	Engine engine;
	DataService dataService;
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
		
		def ballots = []
		def criteria = new BallotCriteria();
		def p = new Predicate<Ballot>(){
			public boolean apply(Ballot b) {
				ballots += b;
			}
		};
		engine.ballotListForAdmin(session.user, criteria, p);
        [
			ballotInstanceList: ballots,
			ballotInstanceTotal: ballots.size
		]
    }

    def create() {
        [ballotInstance: new Ballot2(params)]
    }

    def save() {
        def ballotInstance = new Ballot2(params)
        if (!ballotInstance.save(flush: true)) {
            render(view: "create", model: [ballotInstance: ballotInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'ballot.label', default: 'Ballot'), ballotInstance.id])
        redirect(action: "show", id: ballotInstance.id)
    }

    def show() {
		
        def ballotInstance = dataService.getBallot(params.id);
        if (!ballotInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "list")
            return
        }

        [ballotInstance: ballotInstance]
    }

    def edit() {
        def ballotInstance = dataService.getBallot(params.id);

        if (!ballotInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ballot.label', default: 'Ballot'), params.id])
            redirect(action: "list")
            return
        }

        [ballotInstance: ballotInstance]
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
