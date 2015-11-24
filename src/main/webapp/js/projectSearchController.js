angular.module("timesheetModule")
    .controller("projectSearchController", function ($location, $route, $scope, apiClient, resource) { // usamos o resource, definido no resolve

        $scope.search = function () {
            $scope.resource.options.page = 0
            $scope.resource.options.t    = new Date().getTime()
            $location.search($scope.resource.options) // seta a querystring em forma de mapa
            //$route.reload()
        }

        function getResults() {
            var search = $location.search() //recupera a querystring em forma de mapa
            $scope.resource.options.name        = search.name
            $scope.resource.options.description = search.description
            $scope.resource.options.tasks       = search.tasks

            $scope.tes = "wwww";
            if(search.page !== undefined) { // page = 0 resolve p false

                $scope.resource.$get("otherResults", search)
                    .then(function(resultsResource){
                        $scope.projects = resultsResource.projects;
                        console.log(resultsResource);
                    })
                console.log("Getting results")
            }
            }

        function setResource(resource) {
            $scope.resource = resource

            $scope.resource.$get("tasks")
                    .then(function(tasks){
                    $scope.tasks = tasks;
                })

            getResults()
        }
        setResource(resource)

        $scope.$on("$routeUpdate", getResults)
    })