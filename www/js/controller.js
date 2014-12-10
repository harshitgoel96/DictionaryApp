angular.module('dictionary.controller', ['ionic'])
.controller('DictionaryCtrl',function($scope,dictService,$ionicLoading,$ionicPopup,dictService,$ionicScrollDelegate,x2js,$http){
	
	//dictService
	$scope.getMeaning=function(){
		//console.log('we are here');
		$ionicScrollDelegate.scrollTop();
		dictService.getMeaning($scope,$ionicLoading,x2js,$http);
		
	}
}

)