<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	//VERY simple templating. YMMV.
    final String title = "ORCID Datacentre reporting";
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
    <link rel='stylesheet' href='/api/webjars/bootstrap/3.0.3/css/bootstrap.min.css' />
	<link rel='stylesheet' href='/api/webjars/datatables-plugins/ca6ec50/integration/bootstrap/3/dataTables.bootstrap.css' />
    <link rel='stylesheet' href='/typeahead-bootstrap.css' />
</head>
<body>


<div class="container" >
	<div class="navbar navbar-default" role="navigation">
	        <div class="navbar-header">
		        <a class="navbar-brand" href="#"><%= title %><span class='glyphicon glyphicon-tranfer'></span></a>
	          <ul class="nav navbar-nav">
            <li><a href="/">Add Work</a></li>
            <li class="active"><a href="/search">Datacentre reporting</a></li>
	        <li><a href="javascript:$('#helpModal').modal('show')">Help</a></li>
	          </ul>
	        </div>
	</div>
	
	<div id="orciddiv">
		<table cellpadding="0" cellspacing="0" border="0" class="table table-condensed" id="orcidtable"></table>
	</div>
	
	<!-- Help Modal -->
	<div class="modal fade" id="helpModal" tabindex="-1" role="dialog" aria-labelledby="helpModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        <h4 class="modal-title" id="myModalLabel">Lookup ORCiD users by their work DOIs</h4>
	      </div>
	      <div class="modal-body">
	        <p>To search by publisher, start typing the publisher name into the publisher search box.</p>
	        <p>Example: <i>British Hydrological Society</i> or <i>CERN</i></p>
	        <p>Note that these are not always obvious e.g. FIGSHARE DOIs are listed as "CDL.DIGSCI - Digital Science". Some publishers have multiple prefixes and some publishers share DOI prefixes.</p> 
	        <hr/>	        
	        <p>To search by identifier, enter a complete DOI (e.g. "10.9998/abc123") or DOI prefix (e.g."10.9998/") in the search box.
	        You can change the identifier type to search and how to match it using the drop-downs.</p> 
	        <p>>Example: <i>Choose "Other ID" as the identifier type and enter "uk.bl" in the search box to see all Ethos e-theses</i></p>
	        <!-- button type="button" data-dismiss="modal" onClick="javascript:">Try Me</button> -->
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	
	<div class="footer">
	<hr/>
	  <p>Provided by <a href="http://bl.uk/">The British Library</a> as part of the <a href="http://odin-project.eu/">ODIN project</a>.</p>
	  <p>Source code available on <a href="https://github.com/TomDemeranville/orcid-update-java">GitHub</a>. Questions to <a href="http://twitter.com/tomdemeranville">@tomdemeranville</a>.</p>
	</div>
</div>
<!-- fetch our maven managed javascript dependencies last to speed loading-->
<script src="/api/webjars/jquery/1.9.0/jquery.min.js"></script>
<script src="/api/webjars/bootstrap/3.0.3/js/bootstrap.min.js"></script>
<script src="/api/webjars/datatables/1.9.4/media/js/jquery.dataTables.min.js"></script>
<script src="/api/webjars/datatables-plugins/ca6ec50/integration/bootstrap/3/dataTables.bootstrap.js"></script>
<script src="/api/webjars/typeaheadjs/0.9.3/typeahead.min.js"></script>
<!-- 
<script src="/api/webjars/datatables-plugins/ca6ec50/pagination/bootstrap.js"></script>
 -->
<script>
	//STARTUP CODE
	var oTable; // our handle on the datatable
	var idType = "doi";
	var matchType = "prefix";
	
	$(function() {
		 oTable = $('#orcidtable').dataTable( {
		        "sDom":
	                "r<'row'<'col-md-4'<'publishers'>><'col-md-4'f><'col-md-4'<'extra'>>>"+
	                "t"+
	                "<'row'<'col-xs-6'i><'col-xs-6'p>>",
		        "bProcessing": true,
		        "bServerSide": true,
		        "sAjaxSource": "/api/report/datatable",
		        "fnServerParams": function ( aoData ) {
			        //this is called every request!
		            aoData.push( {"name":"searchtype", "value":matchType} );
		            aoData.push( {"name":"idtype","value":idType });
		        },
		        "aoColumns": [
		                      { "mData": "name","sTitle":"name","bSortable": "false" },
		                      //{ "mData": "orcid","sTitle":"orcid","bSortable": "false" },
		                      { "mData": "link","sTitle":"orcid","bSortable": "false" }
		                  ],
                "oLanguage": { "sSearch": "","sZeroRecords":"There are no ORCiDs to display." },
                "iDeferLoading": [ 0, 0 ],
                "bDeferLoading":true,
                "bSort":false
                //"bStateSave":true
		         //"oSearch": {"sSearch": "10."}            
		        
		    } );

		 //three character minimum search
		 $('.dataTables_filter input')
		    .unbind('keypress keyup')
		    .bind('keypress keyup', function(e){
		      if ($(this).val().length < 2 && e.keyCode != 13) return;
		      oTable.fnFilter($(this).val());
		    });

		//TODO: export as CSV
		 
		 //style the input elements see http://datatables.net/forums/discussion/comment/52857
		 $('#orcidtable_length label select').addClass('form-control');
		 $('#orcidtable_filter label input').addClass('form-control');
		 var searchBox =  $('.dataTables_filter input').attr('placeholder', 'ID search: 10.9998 or uk.bl');

		 //TODO: fetch the list from /identifiers/external
		 //create a drop down for id type
		 $('<select></select>')
			.attr("id", "idtype")
	        .append('<option value="doi">DOI</option>')
	        .append('<option value="isbn">ISBN</option>')
	        .append('<option value="other-id">Other ID</option>')
	        .on("change", function(){
		        idType = this.value;
		        oTable.fnClearTable(0);
		        searchBox.keydown();
        	    searchBox.keyup();
	        })
	        .addClass('form-control')
	        .appendTo($('.extra'));

		 //TODO: fetch the list from /identifiers/searchtype
		 //create a drop down for search type
		 $('<select></select>')
			.attr("id", "prefix")
	        .append('<option value="prefix">Prefix Match</option>')
	        .append('<option value="exact">Exact Match</option>')
	        .append('<option value="solr">Solr Syntax</option>')
	        .on("change", function(){
		        matchType = this.value;
		        oTable.fnClearTable(0);
		        searchBox.keydown();
        	    searchBox.keyup();
		        //oTable.fnDraw();
	        })
	        .addClass('form-control')
	        .appendTo($('.extra'));

		 //publisher typeahead
		$('<input></input>')
		 .attr("id", "publishers")
		 .addClass('form-control')
		 .addClass('typeahead')
		 .attr('placeholder', 'Publisher search:')
		 .appendTo($('.publishers'));
		 $('#publishers')
		 .typeahead([
             {
               name: 'publishers2',
               header: '<h4>Publishers</h4>',
               prefetch: '/api/doiprefix/publishers'
             },
             {
                 name: 'datacentres',
                 header: '<h4>Datacentres</h4>',
                 prefetch: '/api/doiprefix/datacentres'
               },
             {/* don't want this here, but heyho for now */
                 name: 'other',
                 header: '<h4>Other providers</h4>',
                 local: [{"value":"EThOS - UK E-Thesis service","doi":"uk.bl","type":"other-id"}]
               }
             
           ]).on('typeahead:selected', function (obj, datum) {
        	    console.log(obj);
        	    console.log(datum);
        	    if (datum.type)
	       	    	 $('#idtype').val(datum.type).change();
	       	    else
	        	    $('#idtype').val("doi").change();

        	    searchBox.val(datum.doi);
        	    searchBox.keydown();
        	    searchBox.keyup();
        	});
		 $('.tt-hint').addClass('form-control');
          
	});
	</script>
</body>
</html>