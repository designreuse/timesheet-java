angular.module("timesheetModule").controller("projectFormController", ["$scope", "alertService", "resource", function ($scope, alertService, resource) {
	$scope.hasTask = function (task) {
		return hasTaskUri(task.$href("self"))
	}

	function hasTaskUri(taskUri) {
		return $scope.project && _.contains($scope.project.tasks, taskUri)
	}

	$scope.toggleTask = function (task) {
		var taskUri = task.$href("self")
		if (hasTaskUri(taskUri)) {
			_.pull($scope.project.tasks, taskUri)
		} else {
			$scope.project.tasks.push(taskUri)
		}
	}

	$scope.filterTasks = function (task) {
		if ($scope.filteringTasks && $scope.taskNameSubstring) {
			return task.name.toLowerCase().indexOf($scope.taskNameSubstring.toLowerCase()) >= 0
		}
		return true
	}

	$scope.save = function () {
		var request
		if (resource.$has("delete")) {
			request = resource.$put
		} else {
			request = resource.$post                
		}
		request("save", null, $scope.project).then(function () {
			alertService.addAlert({
				type: alertService.SUCCESS,
				message: "Project successfully saved"
			})
		})
	}

	$scope.delete = function () {
		$scope.originalProject.$del("self").then(function () {
			alertService.addAlert({
				type: alertService.SUCCESS,
				message: "Project successfully deleted"
			})
		})
	}

	$scope.resource = resource

	resource.$get("project").then(function (project) {
		function toDate(milliseconds) {
			if (milliseconds === null) {
				return null
			} else {
				return moment.utc(milliseconds).toDate()
			}
		}

		$scope.originalProject = project
		$scope.project = _.clone(project)
		$scope.project.startDate = toDate($scope.project.startDate)
		$scope.project.endDate = toDate($scope.project.endDate)
	})

	resource.$get("tasks").then(function (tasks) {
		$scope.tasks = tasks
	})
}])