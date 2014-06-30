angular.module("eadApp")
.controller('MainCtrl', function($scope, FileService, CompileRunService) {
	
  /**
   * Represent the initial value 
   * for the form that create new class or new Folder
   * or new Interface
   */	
  $scope.initial = {
		  name : "",
		  package : "",
		  addMain : false
  };
	
  
  $scope.newElementName = "";
  $scope.newElementPackage = "";
  $scope.addMain = false;
  
  /**
   * The username's session
   */
  $scope.username = "Hanzhi";
  /**
   * The user projects list
   */
  $scope.projects = [];
  /**
   * Variable to know if the user projects are loaded or not
   */
  $scope.projectsLoaded = false;
  /**
   * Variable to start the loading files animation
   */
  $scope.startTreeAnim = false;
  
  /**
   * The url to download the project
   */
  $scope.downloadUrl = "";
  
//message received from server 
FileService.subscribe(function(message){
	var obj = JSON.parse(message);
	switch (obj.action) {
		case "getProject":
			$scope.projects = obj.projects;
			$scope.projectsLoaded = true;
			break;
			
		case "loadProjectFile":
			$scope.files = [obj.files];
			var projectpath = $scope.files[0].path;
			$scope.downloadUrl = "ZipDownloadServlet?user=" +$scope.username + "&path=" + projectpath;
			$scope.startTreeAnim = false;
			break;
		case "createFile":
			console.log(message);

		default:
			break;
		}
		$scope.$apply();
	});

// load project files	
$scope.loadProjectFiles = function(project){
		$scope.startTreeAnim = true;
		FileService.loadProjectFiles(project, $scope.username);
	};
//connection to file websocket	
var connecting = function(){
		FileService.connect();
		CompileRunService.connect();
};
	
connecting();
	
//tri get user project after 5s because the connection can be 
// in connecting state
setTimeout(function(){FileService.getProjects($scope.username);}, 5000);
  
  
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
	var classContent = "";
	if(packagePath == ""){
		newClass.path = $scope.files[0].path + "/" + newClass.label;
	}else{
		newClass.path = $scope.files[0].path + "/" + packagePath + "/" + newClass.label;
		classContent += "package " + packageName + ";\n\n";
	}
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
	$scope.cancel();
  };
  
  $scope.cancel = function(){
	  $scope.newElementName = $scope.initial.name;
	  $scope.newElementPackage = $scope.initial.package;
	  $scope.addMain = $scope.initial.addMain;
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
		$scope.cancel();
  };
  
  $scope.selectedFile = undefined;
  
  $scope.remove = function(){
	  //check if file is open and close it
	  var file = $scope.selectedFile;
	  if(file == undefined){
		  return;
	  }
	  var fileIndex = -1;
	  for(var i = 0; i < $scope.openFiles.length; i++){
		  var openFile = $scope.openFiles[i];
		  if(file.path = openFile.path){
			  fileIndex = i;
			  break;
		  }
	  }
	  if(fileIndex != -1){
		  $scope.openFiles.splice(fileIndex, 1);
	  }
	  //remove file from project file
	  //1- check if project to delete
	  if(file.path == $scope.files[0].path){
		  $scope.files = [];
	  }else{
		  
		  removeFileInTree(file, $scope.files[0].children);	
		  
	  }
	  
	  FileService.removeFile(file.path, $scope.username);
	  $scope.selectedFile = undefined;
	  
  };
  
  var removeFileInTree = function(file, tree){
	  var pathParts = file.path.split("/");
	  var children = tree;
	  for (var i = 1; i < pathParts.length - 1; i++) {
		var folderIndex = -1;
		for (var j = 0; j < children.length; j++) {
			if(children[j].label == pathParts[i]){
				folderIndex = j;
				break;
			}
		}
		
		children = children[folderIndex].children;
		
	  }
	  
	  var fileindex = -1;
	  for(var i = 0; i < children.length; i++){
		  if(children[i].label == file.label){
			  fileindex = i;
			  break;
		  }
	  }
	  
	  children.splice(fileindex, 1);
	  
  }
  
  $scope.loadFile= function(file){
	  $scope.selectedFile  = file;
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
		var projectpath = $scope.files[0].path;
		$scope.downloadUrl = "ZipDownloadServlet?user=" +$scope.username + "&path=" + projectpath;
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
	
	$scope.compile = function(){
		$scope.compileResult.push("Compiling...");
		var path = $scope.files[0].path;
		CompileRunService.compile(path,null, $scope.username);
	};
	
	$scope.compileandrun = function(){
		$scope.compileResult = [];
		$scope.compileResult.push("Compiling...");
		var path = $scope.files[0].path;
		var mainClassName = $scope.openFiles[$scope.selectedIndex].path;
		CompileRunService.compileAndRun(path, mainClassName, $scope.username);
	};
	
	$scope.stop = function(){
		console.log("Receiving stop event");
		CompileRunService.stop();
	};
	
	$scope.compileResult = [];
	
	CompileRunService.subscribe(function(message){
		if($scope.compileResult.length == 2 && $scope.compileResult[1] === "Compilation Succeed!"){
			$scope.compileResult = [];
		}
		var msg = message.split("\n");
		for (var i = 0; i < msg.length; i++) {
			$scope.compileResult.push(msg[i].replace(/\n/g, "<br />"));
		}
		$scope.$apply();
	});
}).controller("loginCtrl", function($scope, $http) {
	$scope.user = {};	
	$scope.login = function(){
		$http({
	        method  : 'POST',
	        url     : 'LoginServlet',
	        data    : $.param($scope.user),  // pass in data as strings
	        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  // set the headers so angular passing info as form data (not request payload)
	    })
	        .success(function(data) {
	            console.log(data);

//	            if (!data.success) {
//	            	// if not successful, bind errors to error variables
//	                $scope.errorName = data.errors.name;
//	                $scope.errorSuperhero = data.errors.superheroAlias;
//	            } else {
//	            	// if successful, bind success message to message
//	                $scope.message = data.message;
//	            }
	        });
	};
});