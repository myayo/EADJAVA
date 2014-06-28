  function fakeNgModel(initValue){
        return {
            $setViewValue: function(value){
                this.$viewValue = value;
            },
            $viewValue: initValue
        };
 }

angular.module("eadApp").directive("tabResize", function() {
	return function(scope, element, attrs){
		
		scope.$watch('openFiles', function() {
			var tabs = $('.editor-tabs').children();
			if(tabs.length == 1){
				return 180;
			}
		    var margin = (parseInt(tabs.first().css('marginLeft'), 10) + parseInt(tabs.first().css('marginRight'), 10)) || 0;
		    var parentwidth = $('.editor-tabs').parent().width() - 50;
		    var width = (parentwidth / tabs.length) - margin;
		    width = Math.max(parseInt(tabs.first().css('min-width'), 10), Math.min(parseInt(tabs.first().css('max-width'), 10), width));
		    scope.tabWidth = width;
		}, true);
	};
}).directive('scrollGlue', function(){
    return {
        priority: 1,
        require: ['?ngModel'],
        restrict: 'A',
        link: function(scope, $el, attrs, ctrls){
            var el = $el[0],
                ngModel = ctrls[0] || fakeNgModel(true);

            function scrollToBottom(){
                el.scrollTop = el.scrollHeight;
            }

            function shouldActivateAutoScroll(){
                // + 1 catches off by one errors in chrome
                return el.scrollTop + el.clientHeight + 1 >= el.scrollHeight;
            }

            scope.$watch(function(){
                if(ngModel.$viewValue){
                    scrollToBottom();
                }
            });

            $el.bind('scroll', function(){
                var activate = shouldActivateAutoScroll();
                if(activate !== ngModel.$viewValue){
                    scope.$apply(ngModel.$setViewValue.bind(ngModel, activate));
                }
            });
        }
    };
});