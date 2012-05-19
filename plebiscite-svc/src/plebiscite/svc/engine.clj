(ns plebiscite.svc.engine
	(:gen-class)
	(:use 	[compojure.core]
			[hiccup.core])
  	(:require [compojure.handler :as handler]
			[compojure.route :as route]
			[ring.adapter.jetty :as jetty]
			[org.danlarkin.json :as json]
			[clojure.java.jmx :as jmx]
			[clojure.contrib.singleton :as s])
			
	(comment (:import java.net.URLEncoder))
  	(:import org.springframework.context.support.ClassPathXmlApplicationContext)
	(:import org.trevershick.plebiscite.engine.GathererPredicate)
)

(def ctx (atom (ClassPathXmlApplicationContext. "classpath:/plebiscite-engine.xml")))

(def engine (.getBean @ctx "engine"))



(defn- fix-state [map-with-state]
	(assoc map-with-state :state (-> map-with-state :state .toString))
)
(defn- fix-class-name [map-with-class]
	(assoc map-with-class 
			:class (-> map-with-class :class .getSimpleName (.replaceAll "DynamoDb" "")))
)
(defn- fix-policies [mapx]
	(assoc mapx :policies (map (comp fix-class-name bean) (:policies mapx)))
)
(defn- ballot-to-map [ballot]
	((comp fix-policies fix-state fix-class-name bean) ballot)
)







(defn authenticate [email password] 
	(.authenticate engine email password)
)

(defn authenticated [email password]
	(not (nil? ( authenticate email password )))
)


(defn ballot-by-id [the-id]
	(let [ballot (.getBallot engine the-id)]
		(if (nil? ballot) {} (ballot-to-map ballot))
	)
)

(defn ballots-i-own [user]
	(let [ p (GathererPredicate.) ]
		(println "ballots-i-own" user)
		(.ballotsIOwn engine user p)
		(map ballot-to-map p)
	)
)


