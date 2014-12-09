angular.module('dictionary.controller', ['ionic'])
.controller('DictionaryCtrl',function($scope,dictService,$ionicLoading,$ionicPopup,dictService){
	
	//dictService
	$scope.getMeaning=function(){
		//console.log('we are here');
		dictService.getMeaning($scope);
	}
}

)