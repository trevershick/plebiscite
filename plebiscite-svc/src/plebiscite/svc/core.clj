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

(defn test1 [& x] 
	(html [:h1.first  "Foo"]
	      [:h2#second "Bar"]
	[:span {:class "foo"} (or x "no x")])
	)

(defn test2 [x req] 
	{
		:status 200
	   	:headers {"Content-Type" "application/json"}
	  	:body 
			(json/encode {
				:x (or x "no x") 
				:z "pdq" 
				:params (req :query-params)})
	})

(defn url-encode [x]
	(. URLEncoder encode (.toString x)))
	
(defn linkable [value] 
	{ :value (.toString value) :encoded (url-encode (.toString value))}
	)


(defn my-ballots [username password] 
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

(defn ballot-status [ballot-id username password]
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



(defroutes api-routes
	(GET "/ballot/:id" [id :as r] (test2 id r))
	(GET "/my-ballots" {{username :u password :p} :params} (my-ballots username password))
	(GET "/ballot-status/:ballot-id" 
		{{username :u password :p id :ballot-id} :params } 
		(ballot-status id username password))
)

(defroutes main-routes
	(context "/api" [] api-routes)
	(GET "/" [] "<h1>Hello World Wide Web!</h1>")
	(GET "/test/" 	[] (test1))
	(GET "/test/:id" [id] (test1 id))
	(route/resources "/")
	(route/not-found "Page not found"))


(def app
  (handler/site main-routes))


