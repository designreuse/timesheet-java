angular.module("timesheetModule").controller("projectSearchResultController", ["$filter", "$location", "$scope", "resource", function ($filter, $location, $scope, resource) {
	$scope.getLinkClass = function (rel) {
		if (URI($location.url()).equals($filter("hateoasHref")(resource.$href(rel), true))) {
			return "active"
		} else {
			return null
		}
	}

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
}])