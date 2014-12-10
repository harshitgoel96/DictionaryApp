angular.module('dictionary.controller', ['ionic'])
.controller('DictionaryCtrl',function($scope,dictService,$ionicLoading,$ionicPopup,dictService,$ionicScrollDelegate){
	
	//dictService
	$scope.getMeaning=function(){
		//console.log('we are here');
		$ionicScrollDelegate.scrollTop();
		dictService.getMeaning($scope,$ionicLoading);
		$ionicLoading.show({
		template:'Searching please wait <i class=\'ion-loading-c\'>'
		});
	}
}

)