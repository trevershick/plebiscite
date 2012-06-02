(defproject plebiscite-svc "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :aot [plebiscite.svc.core plebiscite.svc.engine]
  :dependencies [
		[org.trevershick.plebiscite/plebiscite-engine "0.0.2.26-SNAPSHOT"]
		[org.clojure/clojure "1.2.1"]
		[org.clojure/clojure-contrib "1.2.0"]
		[compojure "1.0.1"]
		[hiccup "0.3.8"]
		[org.danlarkin/clojure-json "1.2-SNAPSHOT"]]
	:dev-dependencies [[lein-ring "0.5.4"]]
	
	:ring {:handler plebiscite.svc.core/app}
)


