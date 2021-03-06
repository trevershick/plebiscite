(ns plebiscite.svc.core
  	(:gen-class :extends javax.servlet.http.HttpServlet)
  	(:use compojure.core
		ring.util.servlet
		hiccup.core)
  	(:require [compojure.handler :as handler]
			[compojure.route :as route]
			[org.danlarkin.json :as json]
			[plebiscite.svc.engine :as e])
	(:import java.net.URLEncoder)
)

(defn 
	^{:doc "Returns all of the ballots i own, username and password are required to auth with the Plebiscite engine"}
	my-ballots [username password] 
	(let [ 	
		un (if (nil? username) "XXX" username)
		pd (if (nil? password) "XXX" password)
		user (plebiscite.svc.engine/authenticate un pd) 
		result (if (nil? user) [] (plebiscite.svc.engine/ballots-i-own user))]
		(println "User Name:" username)
		(println "Password:" password)
		(println result)
	{  
		:status (if (nil? user) 401 200)
		:body (org.danlarkin.json/encode result) }
	))

(defn 
	^{:doc "Returns the status of the ballot identified by 'ballot-id'"}
	ballot-status [ballot-id username password]
	(let [ 	
			un (if (nil? username) "XXX" username)
			pd (if (nil? password) "XXX" password)
			user (plebiscite.svc.engine/authenticate un pd)
			ballot  (if (nil? user) [] (e/ballot-by-id ballot-id)) ]
		(println ballot-id)
		(println ballot)
		{	
			:status (if (nil? user) 401 200)
			:headers {"Content-Type" "application/json"}
			:body (org.danlarkin.json/encode ballot) }
	))


;; Setup the API routes. This is for 'Ring'  Each URL/endpoints maps to 
;; a function with parameters (optionally). All of these endpoints are
;; prefixed with /api, @see main-routes below.
(defroutes api-routes
;; 	(GET "/ballot/:id" [id :as r] (test2 id r))
	(GET "/my-ballots" {{username :u password :p} :params} (my-ballots username password))
	(GET "/ballot-status/:ballot-id" 
		{{username :u password :p id :ballot-id} :params } 
		(ballot-status id username password))
)

;; Setup the main routes. This is required by Ring.
;; This sets up the routes for the root context and delegates setting up the
;; /api routes to the above function 'api-routes'
(defroutes main-routes
	(context "/api" [] api-routes)
	(GET "/" [] "<h1>Test - It Works</h1>")
	(route/resources "/")
	(route/not-found "Page not found"))

;; Define the 'var' app that Ring uses to handle the web requests
(def app
  (handler/site main-routes))


