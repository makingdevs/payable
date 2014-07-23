class PayableGrailsPlugin {
  // the version or versions of Grails the plugin is designed for
  def groupId = "com.payable"
  def version = "0.2.23"
  def grailsVersion = "2.2 > *"
  def pluginExcludes = [
  "grails-app/views/error.gsp"
  ]

  //def dependsOn = ['amazon-s3':"* > 0.8",'quartz':"* > 1.0"]

  // TODO Fill in these fields
  def title = "Payable Plugin" // Headline display name of the plugin
  def author = "Your name"
  def authorEmail = ""
  def description = '''\
  Brief summary/description of the plugin.
  '''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/payable"

  // Extra (optional) plugin metadata

  // License: one of 'APACHE', 'GPL2', 'GPL3'
  //    def license = "APACHE"

  // Details of company behind the plugin (if there is one)
  //    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

  // Any additional developers beyond the author specified above.
  //    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

  // Location of the plugin's issue tracker.
  //    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

  // Online location of the plugin's browseable source code.
  //    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

  def doWithWebDescriptor = { xml -> }

  def doWithSpring = {
  }

  def doWithDynamicMethods = { ctx ->
  }

  def doWithApplicationContext = { applicationContext ->
  }

  def onChange = { event ->
  }

  def onConfigChange = { event ->
  }

  def onShutdown = { event ->
  }
}
