/**
 * Created by deinf.rcsilva on 12/11/2015.
 */
(function () {
    var module = angular.module("timesheetModule", ["angular-hal","ngRoute", "chart.js", "ngAnimate","sticky","ui.utils.masks"]); // lembre do ng-app da página? Não esqueça da lista de dependências também! Temos pelo menos o "ngRoute", "ui.util.masks" (do rafaelassis, lembra dele?)
    //nganimate eh so para a barra de rolagem ficar fixa. so adicionar a referencia ja faz esse efeito



    module.config(function($routeProvider) {
        var resolve = { // neste mapa eu digo que requisições eu quero que o Angular faça: HATEOS para cada rota existe apenas um recurso no servidor
            resource: function ($http, $location, apiClient) { // resource é um callback
                /*
                return $http({
                    method: "GET",
                    url: "/api" + $location.url()
                });
                */
                return apiClient.$get($location.url())
                //return halClient.$get("/api" + $location.url())
            }// o apelido para a minha pagina se chama recurso, poderia ser qualquer coisa
        };
        $routeProvider
            .when("/", {
                template: ""
            })
            .when("/projects/search/form", {
                controller: "projectSearchController",
                reloadOnSearch: false, // parametro do angular
                resolve: resolve,
                templateUrl: "html/projectSearch.html"
            })
            .when("/timesheet", {
                controller: "timesheetController",
                reloadOnSearch: false, // parametro do angular
                resolve: resolve,
                templateUrl: "html/timesheet.html"
            })
            .otherwise({
            redirectTo: "/"
        })
    })
})();
