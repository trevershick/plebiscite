package plebiscite.web

import org.springframework.dao.DataIntegrityViolationException
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.engine.State;
import org.trevershick.plebiscite.model.Ballot;

import com.google.common.base.Predicate;

class BallotController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	DataService dataService;
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
//		flash.message = "yay"
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
		
		def ballots = []
		def criteria = new BallotCriteria();
//		criteria.addState(State.Open);
//		criteria.addState(State.Open);
		def p = new Predicate<Ballot>(){
			public boolean apply(Ballot b) {
				ballots += b;
			}
		};
		dataService.ballots(criteria, p);

		
        [
			ballotInstanceList: ballots,
			ballotInstanceTotal: ballots.size
	//		ballotInstanceList: Ballot2.list(params), 
	//		ballotInstanceTotal: Ballot2.count()
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

		flash.message = message(code: 'default.created.message', args: [message(code: 'ballot2.label', default: 'Ballot2'), ballotInstance.id])
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
//                          [message(code: 'ballot2.label', default: 'Ballot2')] as Object[],
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
        def ballotInstance = Ballot2.get(params.id)
        if (!ballotInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'ballot2.label', default: 'Ballot2'), params.id])
            redirect(action: "list")
            return
        }

        try {
            ballotInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'ballot2.label', default: 'Ballot2'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'ballot2.label', default: 'Ballot2'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
