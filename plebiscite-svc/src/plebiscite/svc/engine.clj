;; Namespace for wrapper functions around org.trevershick.plebiscite.engine 
;; to make it easier to use in Clojure
;; @author Trever M. Shick
(ns plebiscite.svc.engine
	(:gen-class)
	(:use 	[compojure.core]
			[hiccup.core])
			
	(comment (:import java.net.URLEncoder))
  	(:import org.springframework.context.support.ClassPathXmlApplicationContext)
	(:import org.trevershick.plebiscite.engine.GathererPredicate)
)

(def
	^{:doc "Reference to the Spring Application Context in plebiscite-engine"} 
	ctx (atom (ClassPathXmlApplicationContext. "classpath:/plebiscite-engine.xml")))

(def 
	^{:doc "Reference to the 'engine' bean within the Spring context"} 
	engine (.getBean @ctx "engine"))


(defn- 
	^{:doc "Converts the Ballot State enum into a usable string value"} 
	fix-state [map-with-state]
	(assoc map-with-state :state (-> map-with-state :state .toString))
)
(defn- 
	^{:doc "Converts a map with :class attribute being a Class.class object into a simple name"}
	fix-class-name [map-with-class]
	;; get the :class from the map, then call .getSimpleName on the result
	;; then call .replaceAll on that to remove 'DynamoDb' from the string class name
	;; -> is a 'threading' macro
	(assoc map-with-class 
			:class (-> map-with-class :class .getSimpleName (.replaceAll "DynamoDb" "")))
)
(defn- 
	^{:doc "Converts a map with :class attribute being a Class.class object into a simple name"}
	fix-policies [mapx]
	;; Iterate over the :policies in the supplied map, fixing their classname and
	;; reassociate the result as :policies
	(assoc mapx :policies (map (comp fix-class-name bean) (:policies mapx)))
)
(defn- 
	^{:doc "Converts a ballot to a simple Clojure map"}
	ballot-to-map [ballot]
	((comp fix-policies fix-state fix-class-name bean) ballot)  ; 'bean' the ballot then call fix-* methods
)


(defn 
	^{:doc "Authenticates the user with the Plebiscite engine. a 'User' object is returned if authenticated"}
	authenticate [email password] 
	(.authenticate engine email password)
)

(defn 
	^{:doc "Simple helper method that converts the authenticate result (User) into a boolean"}
	authenticated [email password]
	(not (nil? ( authenticate email password )))
)


(defn 
	^{:doc "Returns the contents of a ballot as a 'map'"}
	ballot-by-id [the-id]
	(let [ballot (.getBallot engine the-id)]
		(if (nil? ballot) {} (ballot-to-map ballot))
	)
)

(defn 
	^{:doc "The ballots owned by a given user. 'user' is a Plebiscite User object"}
	ballots-i-own [user]
	(let [ p (GathererPredicate.) ]
		(println "ballots-i-own" user)
		(.ballotsIOwn engine user p)
		(map ballot-to-map p)
	)
)


