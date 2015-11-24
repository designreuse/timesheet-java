(function () {
    "use strict"

    angular.module("timesheetModule").directive("hateoasAction", ["$filter", "$location", "$timeout", function ($filter, $location, $timeout) {
        return {
            link: function (scope, element, attrs) {
                var action
                attrs.$observe("action", function (newAction) {
                    action = newAction
                })
                element.on("submit", function (event) {
                    event.preventDefault()
                    scope.$apply(function () {
                        var form = URI("?" + $(element).serialize()).search(true)
                        $location.search(form).replace()
                        $timeout(function () {
                            var search = URI(action).search(true)
                            _.forEach(form, function (value, name) {
                                search[name] = value
                            })
                            $location
                                .url($filter("hateoasHref")(URI(action), true))
                                .search(search)
                        })
                    })
                })
            }
        }
    }])

    angular.module("timesheetModule").directive("hateoasValue", ["$route", function ($route) {
        return {
            link: function (scope, element, attrs) {
                var value = $route.current.params[attrs.name]
                if (value !== undefined && value !== null) {
                    element[0].value = value
                }
            }
        }
    }])

    angular.module("timesheetModule").directive("hateoasChecked", ["$route", function ($route) {
        return {
            link: function (scope, element, attrs) {
                attrs.$observe("value", function (value) {
                    if (_.contains($route.current.params[attrs.name], value)) {
                        element[0].checked = true
                    }
                })
            }
        }
    }])

    angular.module("timesheetModule").filter("hateoasHref", [function () {
        return function(href, withoutHash) {
            if (href) {
                var resource = URI(href).resource()
                if (resource.length > 0) {
                    var index = resource.indexOf("/", 1)
                    if (index > 0) {
                        resource = resource.substring(index)
                        if (withoutHash) {
                            return resource
                        } else {
                            return "#" + resource    
                        }
                        
                    }                                            
                }
            }
            return href
        }
    }])
})()