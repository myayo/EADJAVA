angular.module("eadApp").directive("tabResize", function() {
	return function(scope, element, attrs){
		
		scope.$watch('openFiles', function() {
			var tabs = $('.editor-tabs').children();
		    var margin = (parseInt(tabs.first().css('marginLeft'), 10) + parseInt(tabs.first().css('marginRight'), 10)) || 0;
		    var parentwidth = $('.editor-tabs').parent().width() - 50;
		    var width = (parentwidth / tabs.length) - margin;
		    width = Math.max(parseInt(tabs.first().css('min-width'), 10), Math.min(parseInt(tabs.first().css('max-width'), 10), width));
		    scope.tabWidth = width;
		}, true);
	};
});