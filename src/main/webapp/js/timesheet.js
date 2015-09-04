(function () {
    "use strict"

    var timesheet = angular.module("timesheet", ["angular-hal", "angular-search-box", "hateoas", "LocalStorageModule", "ngRoute", "sticky", "ui.utils.masks"])

    timesheet.config(["$routeProvider", function ($routeProvider, $sceProvider) {
        var resolve = {
            resource: ["halClient", "$location", function (halClient, $location) {
                return halClient.$get($location.url().substring(1))
            }]
        }

        $routeProvider
            .when("/", {})
            .when("/" + document.location.origin + "/api/projects/search/options/form", {
                controller: "projectSearchOptionsFormController",
                resolve: resolve,
                templateUrl: "html/projectSearchOptionsForm.html"
            })
            .when("/" + document.location.origin + "/api/projects/search/result", {
                controller: "projectSearchResultController",
                resolve: resolve,
                templateUrl: "html/projectSearchResult.html"
            })
            .when("/" + document.location.origin + "/api/projects/:id/form", {
                controller: "projectFormController",
                resolve: resolve,
                templateUrl: "html/projectForm.html"
            })
            .when("/" + document.location.origin + "/api/timesheets/:start", {
                controller: "timesheetController",
                resolve: resolve,
                templateUrl: "html/timesheet.html"
            })
            .when("/ok", {
                templateUrl: "html/ok.html"
            })
            .when("/notFound", {
                templateUrl: "html/notFound.html"
            })
            .otherwise({
                redirectTo: "/notFound"
            })
    }])

    timesheet.controller("rootController", ["halClient", "$routeParams", "$scope", function (halClient, $routeParams, $scope) {
        $scope.$routeParams = $routeParams

        halClient.$get("/api").then(function (resource) {
            $scope.resource = resource
        })
    }])

    timesheet.controller("projectSearchOptionsFormController", ["$scope", "resource", function ($scope, resource) {
        $scope.resource = resource

        resource.$get("tasks").then(function (tasks) {
            $scope.tasks = tasks
        })

        $scope.filterTasks = function (task) {
            if ($scope.filteringTasks && $scope.taskNameSubstring) {
                return task.name.toLowerCase().indexOf($scope.taskNameSubstring.toLowerCase()) >= 0
            }
            return true
        }
    }])

    timesheet.controller("projectSearchResultController", ["$location", "$scope", "resource", function ($location, $scope, resource) {
        $scope.resource = resource

        $scope.rels = []
        var index = 1
        var rel = index.toString()
        while (resource.$has(rel)) {
            $scope.rels.push(rel)
            ++index
            rel = index.toString()
        }

        resource.$get("projects").then(function (projects) {
            $scope.projects = projects
        })

        $scope.getLinkClass = function (rel) {
            if (new URI($location.url().substring(1)).equals($scope.resource.$href(rel))) {
                return "active"
            } else {
                return null
            }
        }
    }])

    timesheet.controller("projectFormController", ["$location", "$routeParams", "$scope", "resource", function ($location, $routeParams, $scope, resource) {
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
            if ($routeParams.id === "new") {
                request = $scope.resource.$post
            } else {
                request = $scope.resource.$put
            }
            request("save", null, $scope.project).then(function () {
                $location.path("ok").search({
                    message: "Project successfully saved"
                })
            })
        }

        $scope.delete = function () {
            $scope.originalProject.$del("self").then(function () {
                $location.path("ok").search({
                    message: "Project successfully deleted"
                }).replace()
            })
        }
    }])

    timesheet.controller("timesheetController", ["$scope", "localStorageService", "resource", function ($scope, localStorageService, resource) {
        $scope.resource = resource

        $scope.saveEntryCell = function ($event, projectRow, taskRow, entryCell) {
            if ($event.keyCode == 13) {
                $scope.resource.$patch("self", null, {
                    projectRows: [{
                        id: projectRow.id,
                        taskRows: [{
                            id: taskRow.id,
                            entryCells: [{
                                id: entryCell.id,
                                time: entryCell.time
                            }]
                        }]
                    }]
                }).then(function () {
                    $event.target.blur()
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

        $scope.filterProjectRow = function(projectRow) {
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

        $scope.filterTaskRow = function(projectRow) {
            return function(taskRow) {
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
    }])
})()