angular.module("timesheetModule").controller("rootController", ["$http", "$scope", "halClient", function ($http, $scope, halClient) {
	halClient.$get("/api").then(function (resource) {
		$scope.resource = resource
	})
}])