#Orcid Profile Updater

This is a java based application can log users in via ORCID OAuth and push new works into their profiles.  It was built to work with a British Library service called Ethos, but is easily customizable for other metadata providers.

The user journey is:

1. user enters an identifier for an external system
2. user confirms the title is correct
3. user logs in at orcid
4. user confirms profile update

Alternatively, external apps can link directly to the service with orcid/requests/{your_work_identifier}?redirect=true and skip the "Find your thesis page" steps 1&2.  Users will be pushed directly to ORCID for authentication.

It uses RESTlet on the server side and JQuery/Bootstrap on the client side.  It will work within GAE or a simple servlet container.

##Customization:

The application can be easily modified to support your work metadata of choice in a few steps:

1. Implement the IsOrcidWorkProvider and return isOrcidWork instances.  It must be thread safe and have a no-arg constructor.  See EthosMetadataScraper for an example.
2. Rename web.xml.example to web.xml and enter your IsOrcidWorkProvider class name & ORCID credentials.
3. Modify the wording of index.jsp to reflect your use case. (this will be configurable soon)

##Deployment:

Two maven goals, appengine:devserver and appengine:update.  The first runs a local GAE instance, the second pushes it to the cloud.  To deploy from eclipse: Right click on the pom.xml run as -> Maven Build.  You will need to modify appengine-web.xml to reference your application name.  It'll also play nicely in tomcat or jetty.

##Packages

###uk.bl.odin.orcid
Root package.  Contains setup code for RESTlet, configures routing and pulls configuration from web.xml

###uk.bl.odin.orcid.domain
Core ORCID client logic and interfaces.  Also contains helper classes for things like Bibtex.

###uk.bl.odin.orcid.ethos
Example isOrcidWorkProvider.  Fetches metadata from ethos.bl.uk and transforms it into OrcidWork documents.

###uk.bl.odin.orcid.rest
RESTlet resources.  Handles incoming requests.

###uk.bl.odin.schema.orcid.messages.onepointone
JAXB generated XML bindings for the ORCID v1.1 xml message schema.  Generated with Java 1.6 and JVM version specific.  For other JVM versions it may need regenerating.

##Other info
Build based on GAE maven archetype
Requires: Google Eclipse Plugin, Maven2 Eclipse plugin. (these can both be removed from pom if not using ecplise)

##TODO:
Add configuration for index.jsp
Move to Guice DI for configuration.
Anything else you feel like.  Pull requests welcome.

##Contact
@tomdemeranville
