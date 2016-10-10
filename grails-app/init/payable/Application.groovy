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
class Application extends GrailsAutoConfiguration {
  
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

}
