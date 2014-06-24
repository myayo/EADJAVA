angular.module("eadApp")
.controller("AppCtrl", function($scope,$rootScope, FileService){
	
	$scope.username = "Marcel";
	$scope.projects = [];
	//message received from server 
	FileService.subscribe(function(message){
		console.log(message);
		var obj = JSON.parse(message);
		switch (obj.action) {
		case "getProject":
			$scope.projects = obj.projects;
			break;
			
		case "loadProjectFile":
			$rootScope.$broadcast("projectFiles", {files: obj.files});
			break;

		default:
			break;
		}
		$scope.$apply();
	});
	
	$scope.loadProjectFiles = function(project){
		FileService.loadProjectFiles(project, $scope.username);
	};
	
	var connecting = function(){
		FileService.connect();
	};
	
	connecting();
	
	//setTimeout(function(){FileService.getProjects($scope.username);}, 2000);

	
})
.controller("editorCtrl", function($scope){
	
	//the file explorer should add file here
	$scope.openFiles = [];
	
	$scope.selectedIndex = 0;
	
	$scope.aceLoaded = function(_editor){
		// Editor part
	    var _session = _editor.getSession();
	    var _renderer = _editor.renderer;

	    // Options
	    _session.setUndoManager(new ace.UndoManager());
	    _renderer.setShowGutter(true);
	    _renderer.setTheme("ace/theme/twilight");
	    _session.setMode("ace/mode/java");

	    // Events
	    _editor.on("change", function(){ 
	    	if (_editor.curOp && _editor.curOp.command.name) console.log("user change");
	    });
	    
	    //
	};
	
	$scope.showEditor = function(){
		return $scope.openFiles.length > 0;
	};
	
	$scope.closeTab = function(index){
		$scope.openFiles.splice(index, 1);
		
	};
	
	$scope.addTab = function(file){
		$scope.openFiles.push({name: file.name});
	};
	
	$scope.setSelectedIndex= function(index){
		$scope.selectedIndex = index;
	};
	
	$scope.$on("loadFile", function(events, args) {
		console.log("receive broadcast");
		var index = 0;
		var found = false;
		for(var i = 0; i < $scope.openFiles.length; i++){
			var file = $scope.openFiles[i];
			if(file.path == args.file.path){
				found = true;
				break;
			}
			index++;
		}
		
		$scope.selectedIndex = index;
		if(!found){
			$scope.openFiles.push(args.file);
		}
		
	});
	
	$scope.editorClass = function(file){
		return file.path == $scope.openFiles[$scope.selectedIndex].path ? 'editor-tab-current' : '';
	};
	
})
.controller("explorerCtrl", function($scope, $rootScope, CompileRunService){
	
	$scope.files=[];
	
	$scope.selectedFile = {};
	
	$scope.username = "Marcel";
	
	$scope.loadFile= function(file){
		if(file.src){
			$scope.selectedFile = file;
			$rootScope.$broadcast("loadFile", {file: file});
		}
	};
	
	$scope.$on("projectFiles", function(events, args) {
		$scope.files = [args.files];
		CompileRunService.connect();
		$scope.$apply();
		
	});
	
	$scope.compileResult = "";
	
	$scope.compileandrun = function(){
		var path = $scope.files[0].path;
		var mainClassName = $scope.selectedFile.label;
		CompileRunService.compileAndRun(path, mainClassName, $scope.username);
	};
	
	//message received from server 
	CompileRunService.subscribe(function(message){
		console.log(message);
		$scope.compileResult = message;
		$scope.showConsole = true;
		$scope.$apply();
	});
	
	$scope.showConsole = false;
	

	
});