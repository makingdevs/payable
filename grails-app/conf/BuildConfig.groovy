grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.repos.md.url = "http://makingdevs.com:8081/nexus/content/repositories/thirdparty"
grails.project.repos.md.username = "deployment"
grails.project.repos.md.password = "d3pl0ym3nt"

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
  repositories {
    grailsCentral()
    mavenCentral()
    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    //mavenLocal()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
    mavenRepo name: "MakingDevs", root: "http://makingdevs.com:8081/nexus/content/repositories/thirdparty"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
    // runtime 'mysql:mysql-connector-java:5.1.21'
    test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
  }

  plugins {
    build(":tomcat:$grailsVersion",
      ":release:2.2.1",
      ":rest-client-builder:1.0.3") {
        export = false
      }
    compile ":amazon-s3:0.8.2"
    compile ":quartz:1.0-RC9"
    test(":spock:0.7") {
      exclude "spock-grails-support"
    }
    compile 'com.makingdevs:profile:latest.integration'
  }
}
