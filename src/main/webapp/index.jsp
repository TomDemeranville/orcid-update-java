<%@ page import="com.google.apphosting.api.ApiProxy" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ORCID Import</title>
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

	<div class="jumbotron" id="fetch" style="display:none">
	  <h1>ORCID Import <span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead">Export your E-Thesis from ETHOS and import it into ORCID</p>
	  <p class="lead">Please enter your ETHOS Thesis ID (<a href="http://ethos.bl.uk/">Find my thesis ID</a>)</p>
	  <form role="form" action="javascript:orcidapp.fetchThesis($('#thesisid').val(),'confirm');">
	  	<p><input type="text" placeholder="Example: uk.bl.ethos.398762" class="form-control" id="thesisid"></p>
	  	<p><button type="submit" class="btn btn-lg btn-primary">Step 1: Fetch my Thesis <img src="/spin.gif" style="display:none" id="spin"/></button></p>
	  </form>    
	</div>
	
	<div class="jumbotron" id="confirm" style="display:none">
	  <h1>ORCID Import <span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead">Is this the thesis you're looking for? </p>
	  <div class="alert alert-info">
		  <p class="lead">Thesis: <b id="thesis">thesis details ...</b></p>
	  </div>
	  <p><button class="btn btn-lg btn-primary" onClick="orcidapp.loginToOrcid($('#thesisid').val());">Step 2: Log me into ORCID</span></button></p>
	  <p><button class="btn btn-warning" onClick="orcidapp.startAgain();">That's not my thesis. Start again</button></p>
	</div>
	
	<div class="jumbotron" id="update" style="display:none">
	  <h1>ORCID Import <span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead">Welcome back, ready to update your profile?</p>
	  <div class="alert alert-info">
		  <p>ORCID: <b id="orcid">orcid number ..</b></p>	  
		  <p class="lead">Thesis: <b id="thesis2">thesis details ...</b></p>
	  </div>
	  <p><button onClick="orcidapp.updateProfile();" class="btn btn-lg btn-primary">Step 3: Update my profile <img src="/spin.gif" style="display:none" id="spin2"/></button></p>	  
	</div>

	<div class="jumbotron" id="finish" style="display:none">
	  <h1>ORCID Import <span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <p class="lead">Congratulations. Job done!</p>
	  <p><a class="btn btn-lg btn-primary" href="https://sandbox-1.orcid.org/my-orcid" role="button" onClick="orcidapp.goToOrcid()">View my updated ORCID profile</span></a></p>        
	</div>
	
	<div class="jumbotron" id="error" style="display:none">
	  <h1>ORCID Import <span class="glyphicon glyphicon-cloud-upload"></span></h1>
	  <div class="alert alert-danger"><p class="lead" id="errormsg"></p></div>
	  <p><button class="btn btn-lg btn-primary" onClick="javascript:orcidapp.startAgain();">Start again</button></p>        
	</div>
	
	<div class="jumbotron" id="pleasewait" style="display:none">
	  <h1>ORCID Import <span class="glyphicon glyphicon-cloud-upload"></span></h1>
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

	//fetch the thesis XML (using the OrcidWork schema)
	self.fetchThesis = function(id,nextPanel){
		console.log("fetching thesis "+id+" : "+nextPanel);
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
			    self.thesisXML = result;
			    self.thesisTitle = $(result).find("work-title").text();
				$('#thesis').text(self.thesisTitle);
				$('#thesis2').text(self.thesisTitle);
			    self.showPanel(nextPanel);
			    },
		    error : function (xhr, ajaxOptions, thrownError){  
				$("#spin").hide();
		        console.log(xhr.status);          
		        console.log(thrownError);
		        showError("There was a problem fetching your thesis from Ethos, check you've entered the ID correctly.");		    } 
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
		    data: self.thesisXML, 
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

	//get the auth token and then the thesis xml if sucessful (if we have a thesis code)
	self.fetchAuthToken = function(authcode,thesis){
		$.getJSON("/api/orcid/token", {
			code: authcode, state: thesis
		}).done(function(json) {
			console.log(json);
			self.authToken = json.access_token;
			self.orcid = json.orcid;
			$('#orcid').text(self.orcid);
			if (thesis)
				self.fetchThesis(thesis,"update");
		}).fail(function(response) {
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
	self.theisXML = null;
	self.theisTitle = null;

	//extract a query param fromthe URL.
	getUrlVar = function(key){
		var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search); 
		return result && unescape(result[1]) || ""; 
	}
	
	return self;
}());

//STARTUP CODE
$(function() {
	if (getUrlVar("code") && getUrlVar("state")){
		orcidapp.showPanel("pleasewait");
		//if we have a code & state, attempt to get auth token & thesis
		orcidapp.fetchAuthToken(getUrlVar("code"),getUrlVar("state"));
	}else{
		//otherwise show default view
		orcidapp.startAgain();
	}
});
</script>
</body>
</html>