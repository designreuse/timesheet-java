(function () {
    "use strict"

    angular.module("timesheetModule").factory("authenticationService", ["$httpParamSerializer", "$injector", "$q", function ($httpParamSerializer, $injector, $q) {
        var authenticated = true
        var hasAuthenticationFailed
        var deferred

        var that = {}

        that.isAuthenticated = function () {
            return authenticated
        }

        that.hasAuthenticationFailed = function () {
            return hasAuthenticationFailed
        }

        that.$http = function (config) {
            if (!deferred) {
                authenticated = false
                hasAuthenticationFailed = false
                deferred = $q.defer()
            }
            return deferred.promise.then(function() {
                return $injector.invoke(["$http", function ($http) {
                    return $http(config)
                }])
            })
        }

        that.authenticate = function (username, password) {
            hasAuthenticationFailed = false
            $injector.invoke(["$http", function ($http) {
                $http({
                    method: "POST",
                    url: "/api/authentications",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: $httpParamSerializer({
                        username: username,
                        password: password
                    }),
                    bypassAuthenticationInterceptor: true
                }).then(function (response) {
                    if (response.status === 201) {
                        authenticated = true
                        if (deferred) {
                            deferred.resolve()
                            deferred = null
                        }
                    } else {
                        hasAuthenticationFailed = true
                    }
                })
            }])
        }

        return that
    }])

    angular.module("timesheetModule").controller("authenticationController", ["$scope", "authenticationService", function ($scope, authenticationService) {
        $scope.authenticate = function (username, password) {
            authenticationService.authenticate(username, password)
        }

        $scope.hasAuthenticationFailed = function () {
            return authenticationService.hasAuthenticationFailed()
        }
    }])

    angular.module("timesheetModule").directive("authenticated", ["authenticationService", function (authenticationService) {
        var controller = ["$scope", function ($scope) {
            $scope.isAuthenticated = function () {
                return authenticationService.isAuthenticated()
            }
        }]

        return {
            controller: controller,
            replace: true,
            template: "<div ng-show='isAuthenticated()'><ng-transclude></ng-transclude></div>",
            transclude: true
        }
    }])

    angular.module("timesheetModule").directive("nonAuthenticated", ["authenticationService", function (authenticationService) {
        var controller = ["$scope", function ($scope) {
            $scope.isAuthenticated = function () {
                return authenticationService.isAuthenticated()
            }
        }]

        return {
            controller: controller,
            replace: true,
            template: "<div ng-hide='isAuthenticated()'><ng-transclude></ng-transclude></div>",
            transclude: true
        }
    }])
})()