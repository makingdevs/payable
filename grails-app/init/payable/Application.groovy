package payable

import grails.boot.*
import grails.boot.config.GrailsAutoConfiguration
import grails.plugins.metadata.*

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

@PluginSource
class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment){
      def configBase = new File("${System.getProperty('user.home')}/.grails/md-grain-${environment.activeProfiles[0]}-config.groovy")

      if(configBase.exists()) {
        println "Loading external configuration from Groovy: ${configBase.absolutePath}"
        def config = new ConfigSlurper().parse(configBase.toURL())
        environment.propertySources.addFirst(new MapPropertySource("externalGroovyConfig", config))
      } else {
        println "External config could not be found, checked ${configBase.absolutePath}"
      }
    }
}
