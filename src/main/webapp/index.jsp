<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	//VERY simple templating. YMMV.
    final String title = "ORCID Thesis Import";
	final String introline1 = "Export your E-Thesis from ETHOS and import it into ORCID";
	final String introline2 = "Please enter your ETHOS Thesis ID (<a href=\"http://ethos.bl.uk/\" target=\"_blank\">Find my thesis ID</a>)";
	final String inputPlaceholder = "Example: uk.bl.ethos.398762";
%>

<html>
<head>
    <title><%= title %></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="/api/webjars/html5shiv/3.6.2/html5shiv.js"></script>
      <script src="/api/webjars/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
    
    <!-- fetch our maven managed css dependencies -->
    <link rel='stylesheet' href='/api/webjars/bootstrap/3.0.3/css/bootstrap.min.css'>

</head>
<body>
    
    
<!-- Page that initiated ORCID oauth login and receives auth code -->
<!-- Fetches auth token from server, fetches OrcidWork from server, then posts both back to perform profile update-->
<div class="container" >

	<div class="navbar navbar-default" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-brand" href="#"><%= title %></a>
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Add Work</a></li>
            <li><a href="/search">Datacentre reporting</a></li>
          </ul>
        </div>
      </div>
    </div>
    
	<div class="jumbotron" id="fetch" style="display:none">
	  <h1><%= title %> <span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead" id="welcome"><%= introline1 %></p>
	  <p class="lead"><%= introline2 %></p>
	  <form role="form" action="javascript:orcidapp.fetchWork($('#workid').val());">
	  	<p><input type="text" placeholder="<%= inputPlaceholder %>" class="form-control" id="workid"></p>
	  	<p><button type="submit" class="btn btn-lg btn-primary">Fetch my work <img src="/spin.gif" style="display:none" id="spin"/></button></p>
	  </form>    
	</div>
	
	<div class="jumbotron" id="confirmAndGoToORCID" style="display:none">
	  <h1><%= title %><span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead">Is this the work you're looking for? </p>
	  <div class="alert alert-info">
		  <p class="lead">Title: <b id="work1"></b></p>
	  </div>
	  <p><button class="btn btn-lg btn-primary" onClick="orcidapp.loginToOrcid($('#workid').val());">Log me into ORCID</span></button></p>
	  <p><button class="btn btn-warning" onClick="orcidapp.startAgain();">That's not my work. Start again</button></p>
	</div>
	
	<div class="jumbotron" id="confirmAndUpdate" style="display:none">
	  <h1><%= title %><span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead">Ready to update your profile?</p>
	  <div class="alert alert-info">
		  <p>ORCID: <b id="orcid"></b></p>	  
		  <p class="lead">Title: <b id="work2"></b></p>
	  </div>
	  <p><button onClick="orcidapp.updateProfile();" class="btn btn-lg btn-primary">Update my profile <img src="/spin.gif" style="display:none" id="spin2"/></button></p>	  
	</div>

	<div class="jumbotron" id="finish" style="display:none">
	  <h1><%= title %><span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead">Congratulations. Job done!</p>
	  <p><a class="btn btn-lg btn-primary" href="https://orcid.org/my-orcid" role="button" onClick="orcidapp.goToOrcid()">View my updated ORCID profile</span></a></p>        
	</div>
	
	<div class="jumbotron" id="error" style="display:none">
	  <h1><%= title %><span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <div class="alert alert-danger"><p class="lead" id="errormsg"></p></div>
	  <p><button class="btn btn-lg btn-primary" onClick="javascript:orcidapp.startAgain();">Start again</button></p>        
	</div>
	
	<div class="jumbotron" id="pleasewait" style="display:none">
	  <h1><%= title %><span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <div class="alert alert-success">
		  <p class=lead>Please wait a moment while we fetch your details</p>
		  <p><img src="/spin.gif"/></p>        
	  </div>
	</div>
	
	<div class="footer">
	  <p>Provided by <a href="http://bl.uk/">The British Library</a> as part of the <a href="http://odin-project.eu/">ODIN project</a>.</p>
	  <p>Source code available on <a href="https://github.com/TomDemeranville/orcid-update-java">GitHub</a>. Questions to <a href="http://twitter.com/tomdemeranville">@tomdemeranville</a>.</p>
	</div>

</div>
      
<!-- fetch our maven managed javascript dependencies last to speed loading-->
<script src="/api/webjars/jquery/1.9.0/jquery.min.js"></script>
<script src="/api/webjars/bootstrap/3.0.3/js/bootstrap.min.js"></script>

<script>
var orcidapp = (function (){
	var self = {};

	//fetch the work XML (using the OrcidWork schema)
	self.fetchWork = function(id){
		if (id=="")return;
		$("#spin").show();
		$.ajax({
		    url: "/api/meta/"+id,
		    type: 'GET',
		    contentType: "text/xml",
		    success: function(result) {
				$("#spin").hide();
			    console.log("yay");
			    console.log(result);
			    self.workXML = result;
			    self.workTitle = $(result).find("work-title").text();
				$('#work1').text(self.workTitle);
				$('#work2').text(self.workTitle);
				if (self.authToken && self.orcid)
					self.showPanel("confirmAndUpdate");
				else
					self.showPanel("confirmAndGoToORCID");
			    },
		    error : function (xhr, ajaxOptions, thrownError){  
				$("#spin").hide();
		        console.log(xhr.status);          
		        console.log(thrownError);
		        showError("There was a problem fetching your work, please check you've entered the ID correctly.");		    } 
		}); 
	};

	self.startAgain = function(){
		self.showPanel("fetch");
	};

	self.loginToOrcid = function(id){
		window.location = "/api/orcid/requests/"+id+"?redirect=true";
	};

	//post the OrcidWork and token to update the users profile
	self.updateProfile = function(authCode, id){
		$("#spin2").show();
		$.ajax({
		    url: "/api/orcid/"+self.orcid+"/orcid-works/create?token="+self.authToken,
		    data: self.workXML, 
		    type: 'POST',
		    contentType: "text/xml",
		    dataType: "xml",
		    processData: false, 
		    success: function(result) {
				$("#spin2").hide();
			    console.log("yay");
			    console.log(result);
			    self.showPanel("finish");
			    },
		    error : function (xhr, ajaxOptions, thrownError){  
				$("#spin2").hide();
		        console.log(xhr.status);          
		        console.log(thrownError);
		        showError("Problem updating your profile.");
		    } 
		}); 
	};

	//get the auth token and then the work xml if sucessful (if we have a work code)
	self.fetchAuthToken = function(authcode,work){
		$.getJSON("/api/orcid/token", {
			code: authcode, state: work
		}).done(function(json) {
			console.log(json);
			self.authToken = json.access_token;
			self.orcid = json.orcid;
			$('#orcid').text(self.orcid);
			if (work)
				self.fetchWork(work);
			else{
				$('#welcome').text("You're logged in with the ORCID: "+self.orcid);
				self.showPanel("fetch");
			}
		}).fail(function(response) {
			self.orcid=null;
			self.authToken=null;
			showError("Sorry, you ran out of time.");
		});	
	};	

	self.goToOrcid = function(){
		window.location = "http://orcid.org/"+orcid;
	}

	self.showPanel = function(panelName){
		console.log("showing "+panelName);
		$( ".jumbotron" ).hide();
		$( "#"+panelName ).show();
	}

	showError = function(msg){
		$("#errormsg").text(msg);
		self.showPanel("error");
	}

	//we get these with rest, but bundling it in with the page would it a bit quicker and easier. YMMV.
	self.authToken = null;
	self.orcid = null;
	self.workXML = null;
	self.workTitle = null;

	//extract a query param fromthe URL.
	getUrlVar = function(key){
		var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search); 
		return result && unescape(result[1]) || ""; 
	}
	
	return self;
}());

//STARTUP CODE
$(function() {
	if (getUrlVar("code")){
		orcidapp.showPanel("pleasewait");
		orcidapp.fetchAuthToken(getUrlVar("code"),getUrlVar("state"));
	} else {
		//otherwise show default view
		if (getUrlVar("state"))
			$('#workid').val(getUrlVar("state"));
		orcidapp.startAgain();
	}
});
</script>
</body>
</html>