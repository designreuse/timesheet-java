angular.module("timesheetModule").controller("projectSearchOptionsFormController", ["$scope", "resource", function ($scope, resource) {
	$scope.filterTasks = function (task) {
		if ($scope.filteringTasks && $scope.taskNameSubstring) {
			return task.name.toLowerCase().indexOf($scope.taskNameSubstring.toLowerCase()) >= 0
		}
		return true
	}

	$scope.resource = resource

	resource.$get("tasks").then(function (tasks) {
		$scope.tasks = tasks
	})
}])