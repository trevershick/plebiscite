package plebiscite.web

import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.model.UserStatus;

class EngineService {
	static transactional = false
	
	DataService dataService
	
	
    def authenticateUser(emailAddress, credentials) {
		def user = dataService.getUser(emailAddress);
		if (user && user.getUserStatus() == UserStatus.Banned) {
			// maybe notify the admin too
			throw new BannedUserException();
		}
		return user;
    }
}
