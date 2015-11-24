(function () {
    "use strict"

    angular.module("timesheetModule", ["angular-hal", "angular-loading-bar", "angular-search-box", "chart.js", "LocalStorageModule", "ngAnimate", "ngRoute", "sticky", "ui.utils.masks"])

    angular.module("timesheetModule").factory("interceptors", ["$locale", "$q", "alertService", "authenticationService", "errorService", function ($locale, $q, alertService, authenticationService, errorService) {
        return {
            request: function (request) {
                alertService.clearAlerts()
                errorService.clearErrors()
                if (!request.headers["Accept-Language"]) {
                    var id = $locale.id
                    var index = id.indexOf("-")
                    if (index < 0) {
                        request.headers["Accept-Language"] = id
                    } else {
                        request.headers["Accept-Language"] = id.substring(0, index) + "-" + id.substring(index + 1).toUpperCase()
                    }
                }
                return request
            },

            responseError: function (rejection) {
                if (rejection.status == 401) {
                    if (!rejection.config.bypassAuthenticationInterceptor) {
                        return authenticationService.$http(rejection.config)
                    } else {
                        return $q.reject(rejection)
                    }
                } else {                    
                    if (!rejection.config.ignoreErrors) {
                        if (rejection.data) {
                            errorService.setErrors(rejection.data)
                            _.each(rejection.data.globalErrors, function (error) {
                                alertService.addAlert({
                                    type: alertService.DANGER,
                                    message: error.message
                                })
                            })
                        } else {
                            alertService.addAlert({
                                type: alertService.DANGER,
                                message: "An unknown error has occurred"
                            })
                        }                        
                    }
                    return $q.reject(rejection)
                }
            }
        }
    }])

    angular.module("timesheetModule").config(["$httpProvider", "$routeProvider", "cfpLoadingBarProvider", function ($httpProvider, $routeProvider, cfpLoadingBarProvider) {
        $httpProvider.interceptors.push("interceptors");

        var resolve = {
            resource: ["$location", "halClient", function ($location, halClient) {
                return halClient.$get(document.location.origin + "/api" + $location.url())
            }]
        }

        $routeProvider
            .when("/", {})
            .when("/projects/search/options/form", {
                controller: "projectSearchOptionsFormController",
                resolve: resolve,
                templateUrl: "html/project/searchOptionsForm.html"
            })
            .when("/projects/search/result", {
                controller: "projectSearchResultController",
                resolve: resolve,
                templateUrl: "html/project/searchResult.html"
            })
            .when("/projects/:id/form", {
                controller: "projectFormController",
                resolve: resolve,
                templateUrl: "html/project/form.html"
            })
            .when("/timesheets/:start", {
                controller: "timesheetController",
                resolve: resolve,
                templateUrl: "html/timesheet.html"
            })
            .when("/notFound", {
                templateUrl: "html/notFound.html"
            })
/*            
            .otherwise({
                redirectTo: "/notFound"
            })
*/            

        cfpLoadingBarProvider.includeSpinner = false
    }])
})()