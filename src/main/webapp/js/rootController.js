angular.module("timesheetModule")
    .controller("rootController", function ($scope, halClient) {
        halClient.$get("/api").then(function (resource) {
            $scope.resource = resource;
        })
    })
