/**
 * Created by deinf.rcsilva on 18/11/2015.
 */
angular.module("timesheetModule").directive("searchBox", function() {
    var controlador = function($element, $scope, $timeout) {
        console.log($element.find("input")[0].focus())
        $scope.show = function () {
            $scope.searching = true;

            $scope.onSearch()

            // propaga o evento em direção à raiz
            $scope.$emit("searchStarted", {
                message: "Search started"
            })
            // propaga o evento da raiz para as folhas
            $scope.$broadcast()

            $timeout(function() {
                $element.find("input")[0].focus();
            })
        }
        $scope.hide = function () {
            $scope.searching = false;
        }
    }

    return {
        scope: { // atributos da tag/diretiva são declarados aqui
            label: "@",       // constante
            searching: "=",   // two way var
            modelo: "=",      // two way var
            onSearch: "&"     // callback (uma funcao que será avaliada)
        },
        controller: controlador,
        templateUrl: "html/searchBox.html"
    }
})
