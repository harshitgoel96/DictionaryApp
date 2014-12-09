angular.module('dictionary.services',['ionic','dictionary.controller'])
.factory('dictService',function(){
	return{
		getMeaning : function($scope,$ionicLoading){
			var wordStr=$scope.word;
			//here will come the cordova exec code
			//alert(wordStr);
			cordova.exec(
			function success(data){
				//$scope.definitions=data.DATA;
				alert(data.DATA);
			},
			function error(err){
			
			},
			'dictionaryPlugin',
			'define',
			[wordStr]
			);
			
		}
	}
})