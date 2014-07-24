#Orcid Profile Updater

This is a java based application can log users in via ORCID OAuth and push new works into their profiles.  It was built to work with a British Library service called Ethos (http://ethos.bl.uk), but is easily customizable for use with other metadata providers.  

It uses RESTlet on the server side and JQuery/Bootstrap on the client side.  It will work within GAE or a simple servlet container.

The ORCiD client library is available at (https://github.com/TomDemeranville/orcid-java-client)

You can see the full application in action at (http://ethos-orcid.appspot.com/)

##NEW!! Generic DC metadata support for institutional repositories

Tested with theses in the University of Leicester repository, for example: https://lra.le.ac.uk/handle/2381/8436

##NEW!! Built in reporting for publishers & datacentres

Datacentres can now lookup ORCiD users that have regitered their DOIs or other identifiers.  You can look up by DOI, DOI prefix or publisher name.  See (http://ethos-orcid.appspot.com/search)

##User Journey

It's either:

1. user enters an identifier for an external system (our implementation uses an Ethos identifier)
2. user confirms the title is correct
3. user logs in at orcid
4. user confirms profile update

or

1. user arrives at site with ORCID OAuth code parameter and enters work identifier
2. user confrims work title and updates

To use the second workflow, external apps can link directly to the service at `/orcid/requests/{your_work_identifier}?redirect=true` to skip steps 1&2, the "Find your work page" part.  Users will be pushed directly to ORCID for authentication.

##Customization:

The application can be easily modified to support your work metadata of choice in a few steps:

1. Implement the `IsOrcidWorkProvider` interface and return `IsOrcidWork` instances.  It must be thread safe and have a no-arg constructor.  See `EthosMetadataScraper` (https://github.com/TomDemeranville/orcid-update-java/blob/master/src/main/java/uk/bl/odin/orcid/ethos/EthosMetaScraper.java) for an example.
2. Rename web.xml.example to web.xml and modify it to use your `IsOrcidWorkProvider` class name & ORCID credentials.
3. Modify message parameters (title etc) at the top of index.jsp. 

##Servlet Init params

* "OrcidWorkProvider" fully qualified class name for IsOrcidWorkProvider implementation
* "OrcidClientID", "OrcidClientSecret", "OrcidReturnURI" ORCID OAuth params
* "OrcidSandbox" true for sandbox, false to use live api

###Build and deployment:

Two maven goals, `appengine:devserver` and `appengine:update`.  The first runs a local GAE instance, the second pushes it to the cloud.  Maven plugin means it can be deployed/built from eclipse: Right click on the pom.xml run as -> Maven Build.  You will need to modify `appengine-web.xml` to reference your application name.  

It'll also play nicely in tomcat or jetty as a standard WAR file.

##RESTful Routes:
	
* `/orcid/token` convert authorization codes from ORCID into authz tokens
* `/orcid/requests` generate a authz request url (`?redirect=true` to bounce user to ORCID with http redirect)
* `/orcid/requests/{originalRef}` generate a authz request url with originalRef as state param (`?redirect=true` to bounce user to ORCID with http redirect)
* `/orcid/{orcid}/orcid-works/create` create a work by posting OrcidWork XML (requires `?token=` orcid oauth token) 
* `/meta?id=` fetch metadata from external source - use `?json` for raw form (note this is an implementation specific form, not a JSON form of ORCID metadata)
* `/webjars` webjars endpoint - example: `/webjars/bootstrap/3.0.3/css/bootstrap.min.css` also includes JQuery 1.9.0

* `/orcid/search` simple interface to ORCiD search API
* `/orcid/{orcid}` fetch an ORCiD profile
* `/identifier/{type}` fetch lists of valid identifiers - external, worktype, searchfield, searchtype 

* `/report/datatable` endpoint that provides datatable.net compatible reverse lookup of DOIs
* `/doi/prefix` lookup for DOI prefix-> publisher names

##Packages

###uk.bl.odin.orcid
Root package.  Contains setup code for RESTlet, configures routing and pulls configuration from `web.xml` to create injectable dependencies.

###uk.bl.odin.orcid.domain
Core ORCID client logic and interfaces.  Also contains helper classes for things like Bibtex.

###uk.bl.odin.orcid.ethos
Example `isOrcidWorkProvider`.  Fetches metadata from ethos.bl.uk and transforms it into `OrcidWork` documents.

###uk.bl.odin.orcid.rest
RESTlet resources.  Handles incoming requests.

###uk.bl.odin.orcid.rest.report
Datacentre reporting RESTlet resources.  

###uk.bl.odin.orcid.guice
Boilerplate Guice DI classes, taken from the RESTlet org.restlet.ext.guice incubator project.

###uk.bl.odin.orcid.doi
Lookup for DOI prefixes -> publishers

##Other info
Build based on GAE maven archetype
Requires: Google Eclipse Plugin, Maven2 Eclipse plugin. (these can both be removed from pom if not using ecplise)

##TODO:

Externalise configuration for index.jsp

Handle refresh tokens properly

Anything else you feel like.  Pull requests welcome.

##Contact

[@tomdemeranville on twitter](https://twitter.com/tomdemeranville)

[Announcement](http://demeranville.com/orcid-open-source-java-client/)

[My blog](http://demeranville.com)