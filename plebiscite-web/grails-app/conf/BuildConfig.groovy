grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file="target/plebiscite-web-${grails.util.Environment.current.name}-${appVersion}.war"

//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
		excludes (
			"ehcache-core"
			//"aopalliance",
			//"aspectjweaver",
			//"aspectjrt",
			//"cglib-nodep",
			//"jta",
			//"spring-jdbc",
			//"spring-jms",
			//"spring-tx",
			//"spring-orm", 
			//"commons-dbcp",
			//"hibernate-jpa-2.0-api",
			//"h2",
			//"grails-datastore-gorm",
			//"ejb3-persistence",
			//"grails-gorm",
			//"grails-hibernate"
			)
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        mavenLocal()
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		compile("org.trevershick.plebiscite:plebiscite-engine:0.0.2.23-SNAPSHOT") {
			changing = true
		}

        // runtime 'mysql:mysql-connector-java:5.1.16'

    }

    plugins {
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
		
        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"
		//runtime ":hibernate:$grailsVersion"

        build ":tomcat:$grailsVersion"
    }
}
