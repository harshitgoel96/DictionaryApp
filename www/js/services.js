angular.module('dictionary.services',['ionic','dictionary.controller'])
.factory('dictService',function(){
	return{
		getMeaning : function($scope,$ionicLoading){
			var wordStr=$scope.word;
			//here will come the cordova exec code
			/*
			cordova.exec(
			function success(data){
			
			},
			function error(err){
			
			},
			'dictionaryPlugin',
			'getMeaning',
			[]
			);
			*/
		}
	}
})