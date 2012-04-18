package plebiscite.web

import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.engine.State;
import org.trevershick.plebiscite.model.Ballot;

import com.google.common.base.Predicate;

class MyBallotsController {

	DataService dataService;
	def ballots = [];
	
    def index() {
		this.ballots = [];
		
		def criteria = new BallotCriteria();
		criteria.addState(State.Open);
		def p = new Predicate<Ballot>(){
			public boolean apply(Ballot b) {
				ballots += b;
			}
		};
		dataService.ballots(criteria, p);
	}
}
