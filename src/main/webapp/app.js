var app = angular.module('eadApp', ['angularBootstrapNavTree', "ui.ace", "ngSanitize"]);

app.factory("FileService", function() {
	
	var FileService = {};
	
	FileService.connect = function(){
		if(FileService.ws && FileService.ws.readyState == WebSocket.OPEN){
			return;
		}
		
		var wsUrl = "ws://"+document.location.host + "/ead/" + "fileEndPoint";
		var webSocket = new WebSocket(wsUrl);
		webSocket.onopen = function(){
			console.log("CONNECTED TO FILE WEBSOCKET");
		};
		webSocket.onerror = function(){
			console.log("Failed to open a connection to file websocket");
		};
		webSocket.onclose = function(){
			console.log("DISCONNECTED FROM FILE WEBSOCKET");
		};
		webSocket.onmessage = function(message){
			FileService.callback(message.data);
		};
		
		FileService.ws = webSocket;
	};
	
	FileService.subscribe = function(callback){
		FileService.callback = callback;
	};
	
	FileService.getProjects = function(username){
			var json = JSON.stringify({
				action : "getProject",
				username : username
			});
					FileService.ws.send(json);
		
		
	};
	
	FileService.loadProjectFiles = function(project, username){
		var json = JSON.stringify({
			action : "loadProjectFile",
			username: username,
			path : project
		});
		FileService.ws.send(json);
	};
	
	FileService.createFile = function(path, username, type, src){
		var json = JSON.stringify({
			action: "createFile",
			path: path,
			src: src,
			type: type,
			username: username
		});
		FileService.ws.send(json);
	};
	
	return FileService;
	
}).factory("CompileRunService", function(){
	var compileRunService = {};
	
	compileRunService.connect = function(){
		if(compileRunService.ws && compileRunService.ws.readyState == WebSocket.OPEN){
			return;
		}
		var wsUrl = "ws://"+document.location.host + "/ead/" + "compileRunEndpoint";
		var webSocket = new WebSocket(wsUrl);
		
		webSocket.onopen = function(){
			console.log("CONNECTED");
		};
		
		webSocket.onerror = function(){
			console.log("Failed to open a connection");
		};
		
		webSocket.onclose = function(){
			console.log("DISCONNECTED");
		};
		
		webSocket.onmessage = function(message){
			compileRunService.callback(message.data);
		};
		
		compileRunService.ws = webSocket;
	};
	
	compileRunService.subscribe = function(callback){
		compileRunService.callback = callback;
	};
	
	compileRunService.compile = function(path){
		 compileRunService.ws.send(JSON.stringify({
			 action: "compile",
			 path: path
		 }));
	};
	
	compileRunService.compileAndRun = function(path, mainClass, username){
		 compileRunService.ws.send(JSON.stringify({
			 action: "compilerun",
			 path: path,
			 mainClassName: mainClass,
			 username: username
		 }));
	};
	
	compileRunService.Run = function(path, mainClass){
		 compileRunService.ws.send({
			 action: "run",
			 path: path,
			 mainClassPath: mainClass
		 });
	};
	
	return compileRunService;
	
});

app.controller('MainCtrl', function($scope, FileService, CompileRunService) {


  $scope.newClassName = "";
  $scope.newClassPackage = "";
  $scope.addMain = false;
  
  $scope.username = "Hanzhi";
  $scope.projects = [];
  $scope.projectsLoaded = false;
  $scope.startTreeAnim = false;
	//message received from server 
	FileService.subscribe(function(message){
//		console.log(message);
		var obj = JSON.parse(message);
		switch (obj.action) {
		case "getProject":
			$scope.projects = obj.projects;
			$scope.projectsLoaded = true;
			console.log($scope.projects.length);
			break;
			
		case "loadProjectFile":
			$scope.files = [obj.files];
			$scope.startTreeAnim = false;
			break;
		case "createFile":
			console.log(message);

		default:
			break;
		}
		$scope.$apply();
	});
	
	$scope.loadProjectFiles = function(project){
		$scope.startTreeAnim = true;
		FileService.loadProjectFiles(project, $scope.username);
	};
	
	var connecting = function(){
		FileService.connect();
		CompileRunService.connect();
	};
	
	connecting();
	setTimeout(function(){FileService.getProjects($scope.username);}, 2000);
  
  
  var addFileInTree = function(file){
	  var pathParts = file.path.split("/");
	  var children = $scope.files[0].children;
	  for (var i = 1; i < pathParts.length - 1; i++) {
		var folderIndex = -1;
		for (var j = 0; j < children.length; j++) {
			if(children[j].label == pathParts[i]){
				folderIndex = j;
				break;
			}
		}
		
		//if folder not exist create it
		if(folderIndex == -1){
			var folder = {};
			folder.label = pathParts[i];
			var folderPath = "";
			for(var k = 0; k <= i; k++){
				if(folderPath.length > 0){
					folderPath = folderPath + "/";
				}
				folderPath += pathParts[k];
			}
			folder.path = folderPath;
			folder.children = [];
			children.push(folder);
			children = folder.children;
		}
		//folder exist retrieve his children
		else{
			children = children[folderIndex].children;
		}
		
	  }
	  
	  children.push(file);
  };
  
  $scope.addClass = function(name, packageName, addMain){
	var newClass = {};
	newClass.label = name + ".java";
	var packagePath = packageName.replace(/\./g,'/');
	if(packagePath == ""){
		newClass.path = $scope.files[0].path + "/" + newClass.label;
	}else{
		newClass.path = $scope.files[0].path + "/" + packagePath + "/" + newClass.label;
	}
	var classContent = "package " + packageName + ";\n\n";
	classContent += "public class "+  name + " {\n\n";
	if(addMain){
		classContent += "\t" + "public static void main(String[] args){\n";
		classContent += "\t\t" + "System.out.println(\" Hello world\");\n";
		classContent += "\t" + "}\n\n";
	}
	classContent += "}";
	newClass.src = classContent;
	
	addFileInTree(newClass);
	//communicate with serveur  createFile = function(path, username, type, src)
	FileService.createFile(newClass.path, $scope.username, "file", newClass.src);
	$scope.loadFile(newClass);
	$scope.apply();
  };
  
  $scope.cancel = function(){
	  $scope.newClassName = "";
	  $scope.newClassPackage = "";
	  $scope.addMain = false;
	  $scope.$apply();
  };
  
  $scope.addInterface = function(newInterfaceName, newInterfacePackage){
	  var newInterface = {};
	  newInterface.label = newInterfaceName + ".java";
	  var packagePath = newInterfacePackage.replace(/\./g,'/');
	  if(packagePath == ""){
		  newInterface.path = $scope.files[0].path + "/" + newInterface.label;
		}else{
			newInterface.path = $scope.files[0].path + "/" + newInterface + "/" + newClass.label;
		}
	  var classContent = "package " + newInterfacePackage + ";\n\n";
		classContent += "public interface "+  newInterfaceName + " {\n\n";
		classContent += "}";
		newInterface.src = classContent;
		addFileInTree(newInterface);
  };
  
  $scope.loadFile= function(file){
		if(file.src){
			var index = 0;
			var found = false;
			for(var i = 0; i < $scope.openFiles.length; i++){
				var openFile = $scope.openFiles[i];
				if(file.path == openFile.path){
					found = true;
					break;
				}
				index++;
			}
			
			$scope.selectedIndex = index;
			if(!found){
				$scope.openFiles.push(file);
			}
		}
	};
	
	$scope.createProject = function(projectName){
		var project = {};
		project.label = projectName;
		project.path = projectName;
		project.children = [];
		//communicate with serveur createProject to server createFile = function(path, username, type, src)
		FileService.createFile(project.path, $scope.username, "directory");
		$scope.files = [project];
		
	};
  
  
  
  $scope.files = [];
  
  $scope.openFiles = [];
  
  $scope.selectedIndex = 0;
  
  $scope.setSelectedIndex= function(index){
		$scope.selectedIndex = index;
	};
	
	$scope.editorClass = function(file){
		return file.path == $scope.openFiles[$scope.selectedIndex].path ? 'editor-tab-current' : '';
	};
	
	$scope.closeTab = function(index){
		$scope.openFiles.splice(index, 1);
		if(index >= $scope.openFiles.length){
			$scope.selectedIndex = $scope.openFiles.length - 1;
		};
	};
	
	
	$scope.aceLoaded = function(_editor){
		$scope.editor = _editor;
		// Editor part
	    var _session = _editor.getSession();
	    var _renderer = _editor.renderer;

	    // Options
	    _session.setUndoManager(new ace.UndoManager());
	    _renderer.setShowGutter(true);
	    _renderer.setTheme("ace/theme/twilight");
	    _session.setMode("ace/mode/java");

	    // Events 
	    _editor.on("change", function(e){ 
	    	if (_editor.curOp && _editor.curOp.command.name){
	    		if($scope.openFiles[$scope.selectedIndex].label.indexOf('*') == -1){
	    			$scope.openFiles[$scope.selectedIndex].label = "*" + $scope.openFiles[$scope.selectedIndex].label;
	    		}
	    	}
	    });
	    
	    //Command
	    _editor.commands.addCommand({
	    	name: 'saveFile',
	    	bindKey: {
	    	win: 'Ctrl-S',
	    	mac: 'Command-S',
	    	sender: 'editor|cli'
	    	},
	    	exec: function(env, args, request) {
	    		$scope.saveFile();
	    	}
	    });
	};
	
	$scope.zoominEditor = function(){
		var size = parseInt($scope.editor.getFontSize(), 10) || 12;
		$scope.editor.setFontSize(size + 1);
	};
	
	$scope.zoomOutEditor = function(){
		var size = parseInt($scope.editor.getFontSize(), 10) || 12;
		console.log(size);
		console.log($scope.editor);
		$scope.editor.setFontSize(size - 1);
	};
	
	
	$scope.saveFile = function(){
		if($scope.openFiles[$scope.selectedIndex].label.indexOf('*') != -1){
			$scope.openFiles[$scope.selectedIndex].label = $scope.openFiles[$scope.selectedIndex].label.replace("*", "");
		}
		//communicate with serveur  createFile = function(path, username, type, src)
		FileService.createFile($scope.openFiles[$scope.selectedIndex].path, $scope.username, "file", $scope.openFiles[$scope.selectedIndex].src);
		
	};
	
	$scope.showEditor = function(){
		return $scope.openFiles.length > 0;
	};
	
	$scope.compileandrun = function(){
		$scope.compileResult = "Compiling...";
		var path = $scope.files[0].path;
		var mainClassName = $scope.openFiles[$scope.selectedIndex].path;
		CompileRunService.compileAndRun(path, mainClassName, $scope.username);
	};
	
	$scope.compileResult = "";
	
	CompileRunService.subscribe(function(message){
		$scope.compileResult = message.replace(/\n/g, "<br />");
		$scope.$apply();
		console.log($scope.compileResult);
	});
});