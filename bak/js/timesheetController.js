angular.module("timesheetModule").controller("timesheetController", ["$http", "$scope", "$timeout", "localStorageService", "resource", function ($http, $scope, $timeout, localStorageService, resource) {
	$scope.saveEntryCell = function ($event, projectRow, taskRow, entryCell) {
		if ($event.keyCode == 13) {
			var time = typeof entryCell.newTime === 'number' ? entryCell.newTime : null
			var data = {
				projectRows: [{
					id: projectRow.id,
					taskRows: [{
						id: taskRow.id,
						entryCells: [{
							column: entryCell.column,
							time: time
						}]
					}]
				}]
			}
//			resource.$patch("self", null, data)			
			$http({
				method: "PATCH",
				url: resource.$href("self"),
				data: data,
				ignoreErrors: true
			})
			.then(function (response) {
				entryCell.alert = {
					type: "success",
					message: "Saved"					
				}								
				$timeout(function() {
					entryCell.alert.hidden = true					
				})
				entryCell.time = time
				entryCell.newTime = time
				$event.target.blur()
				$scope.updateChart()
			}).catch(function (response) {
				entryCell.alert = {
					type: "danger",
					message: "Error"
				}
			})
		}
	}

	$scope.togglePinning = function ($event) {
		$event.preventDefault()

		$scope.pinning = !$scope.pinning
	}

	function getRowState(key) {
		var state = localStorageService.get(key)
		if (!state) {
			state = {
				visible: true
			}
			localStorageService.set(key, state)
		}
		return state
	}

	function setRowState(key, state) {
		localStorageService.set(key, state)
	}

	function toggleRowVisibility($event, key) {
		$event.preventDefault()

		var state = getRowState(key)
		state.visible = !state.visible
		setRowState(key, state)
	}

	function getProjectRowKey(projectRow) {
		return JSON.stringify(projectRow.id)
	}

	$scope.getProjectRowState = function (projectRow) {
		return getRowState(getProjectRowKey(projectRow))
	}

	$scope.toggleProjectRowVisibility = function ($event, projectRow) {
		toggleRowVisibility($event, getProjectRowKey(projectRow))
	}

	function getTaskRowKey(projectRow, taskRow) {
		return JSON.stringify([projectRow.id, taskRow.id])
	}

	$scope.getTaskRowState = function (projectRow, taskRow) {
		return getRowState(getTaskRowKey(projectRow, taskRow))
	}

	$scope.toggleTaskRowVisibility = function ($event, projectRow, taskRow) {
		toggleRowVisibility($event, getTaskRowKey(projectRow, taskRow))
	}

	$scope.filterProjectRow = function (projectRow) {
		if ($scope.pinning || $scope.getProjectRowState(projectRow).visible) {
			if ($scope.filteringProjectRow && $scope.projectNameSubstring) {
				return projectRow.project.toLowerCase().indexOf($scope.projectNameSubstring.toLowerCase()) >= 0
			} else {
				return true
			}
		} else {
			return false
		}
	}

	$scope.filterTaskRow = function (projectRow) {
		return function (taskRow) {
			if ($scope.pinning || $scope.getTaskRowState(projectRow, taskRow).visible) {
				if ($scope.filteringTaskRow && $scope.taskNameSubstring) {
					return taskRow.task.toLowerCase().indexOf($scope.taskNameSubstring.toLowerCase()) >= 0
				} else {
					return true
				}
			} else {
				return false
			}
		}
	}

	$scope.toggleChartVisibility = function ($event) {
		$event.preventDefault()

		$scope.chart.visible = !$scope.chart.visible
	}

	$scope.updateChart = function () {
		$scope.chart.labels = _.map(resource.projectRows, function (projectRow) {
			return projectRow.project
		})

		$scope.chart.data = []
		_.forEach(resource.projectRows, function (projectRow) {
			var time = 0
			_.forEach(projectRow.taskRows, function (taskRow) {
				_.forEach(taskRow.entryCells, function (entryCell) {
					time += entryCell.time	
				})
			})
			$scope.chart.data.push(time)
		})
	}

	$scope.patchTimesheet = function (timesheet) {
		_.each(timesheet.projectRows, function (projectRow) {
			_.each(projectRow.taskRows, function (taskRow) {
				_.each(taskRow.entryCells, function (entryCell) {
					var date = moment.utc(timesheet.dates[0]).add(entryCell.column, "days").valueOf()
					patchEntryCell(projectRow.id, taskRow.id, date, entryCell.time);
				})
			})
		})

		function patchEntryCell(projectRowId, taskRowId, date, time) {
			_.each(resource.projectRows, function (projectRow) {
				if (projectRow.id === projectRowId) {
					_.each(projectRow.taskRows, function (taskRow) {
						if (taskRow.id === taskRowId) {
							_.each(resource.dates, function (d, index) {
								if (d === date) {
									var entryCell = taskRow.entryCells[index]; 
									entryCell.time = time
									entryCell.newTime = time
									if (!entryCell.alert || entryCell.alert.message !== "Saved") {
										entryCell.alert = {
											type: "info",
											message: "Updated"
										}
									}
									$timeout(function() {
										entryCell.alert.hidden = true					
									})									
									$scope.updateChart()
								}
							})
						}
					})
				}
			})
		}
	}
	
	$scope.handleBlur = function (entryCell) {
		if (entryCell.newTime !== entryCell.time) {
			entryCell.alert = {
				type: "warning",
				message: "Unsaved"
			}
		}
	}
	
	_.each(resource.projectRows, function (projectRow) {
		_.each(projectRow.taskRows, function (taskRow) {
			_.each(taskRow.entryCells, function(entryCell) {
				entryCell.newTime = entryCell.time
			})
		})
	})

	$scope.resource = resource

	$scope.chart = {
		visible: true
	}
	$scope.updateChart()

	var broker = Stomp.over(new SockJS("/stomp"))
	broker.debug = function (message) {
//		console.log(message)
	}

	broker.connect({}, function () {
		broker.subscribe("/topic/timesheet/patch", function (content) {
//		broker.subscribe("/user/queue/timesheet/patch", function(content) {
			$scope.$apply(function () {
				$scope.patchTimesheet(JSON.parse(content.body));
			})
		})
	})
}])