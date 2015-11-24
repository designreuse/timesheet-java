/** timesheetController
 * Created by deinf.rcsilva on 19/11/2015.
 */


angular.module("timesheetModule").controller("timesheetController", function ($http, $location, $scope, apiClient, halClient, resource) { // usamos o resource, definido no resolve
    var data = [300, 500, 200];
    var labels = ["Download Sales", "In-Store Sales", "Mail-Order Sales"];
    $scope.getLabels = function()
    {
        labels.length = 0;
        _.each($scope.resource.projectRows, function(projectRow) {
           labels.push(projectRow.project);
            console.log(projectRow.project);
        });

        return labels;
    }

    $scope.getData = function()
    {
        data.length = 0;
        _.each($scope.resource.projectRows, function(projectRow) {
            var total = 0;
            _.each(projectRow.taskRows, function(taskRow){
                _.each(taskRow.entryCells, function(entryCell){
                    if(entryCell.time){
                        total += entryCell.time
                    }
                })
            })
            data.push(total);
            console.log(projectRow.project);
        });
        return data;
    }

    $scope.$on("searchStarted", function(evento, carga) {
        //evento.stopPropagation()
        console.log(arguments);
    })
    $scope.$on("$routeUpdate", function() { // sempre que mudamos o valor de um parâmetro a função é chamada
        // Callback [hell], Pyramid of doom, futures & promise
        //var promessaRecurso = halClient.$get("/api" + $location.url())
        var promessaRecurso = apiClient.$get($location.url());
        promessaRecurso.then( function (resource) {
            $scope.resource = resource;
        })

        // Paralelo: $q.all(halClient.$get(""), ..., halClient.$get(""))
        //                .then(function(resources) {
        //                })
    }) // recebe dois parametros: nome do evento e uma função a chamar quando o evento acontecer


    $scope.handleSearch = function() {
        console.log("handleSearch called");
    }

    $scope.filterProjectRow = function(projectRow) {
        if ($scope.pfiltro && $scope.projectName) {
            //console.log("in")
            //console.log(projectRow.project.toLowerCase())
            return projectRow.project.toLowerCase().indexOf($scope.projectName.toLowerCase()) >= 0;
        }
        //console.log("out")
        return true
    }                                                                        // $http usado para enviar o patch e o location para saber para onde
    $scope.filterTaskRow = function(taskRow) {
        if ($scope.tfiltro && $scope.taskName) {
            //console.log("in")
            return taskRow.task.toLowerCase().indexOf($scope.taskName.toLowerCase()) >= 0;
        }
        //console.log("out")
        return true
    }
    $scope.saveEntry = function($event, projectRow, taskRow, entryCell) {
        if( $event.keyCode === 13 ) {
            console.log("Blé :P");
            var timesheet = {
                projectRows: [{
                    id:projectRow.id,
                    taskRows: [{
                        id: taskRow.id,
                        entryCells: [{
                            column: entryCell.column,
                            time: entryCell.time
                        }]
                    }]
                }]
            }
            /*
             $http({
             method: "PATCH",
             url: $scope.resource._links.save.href,
             data: timesheet
             })
             */

            resource.$patch("save", null, timesheet);

            console.log(timesheet);
        }
        console.log(arguments);
    }
    //$scope.resource = resource.data
    $scope.resource = resource


    var broker = Stomp.over(new SockJS("/stomp"));

    broker.debug = function (message) {
        console.log('log:' + message); //se quiser inibir logs do stomp basta comentar aqui
    }

    broker.connect({}, function(){
       console.log('conectado...');
        broker.subscribe("/topic/timesheet/patch", function(message){
            var timesheet = JSON.parse(message.body);
            console.log(timesheet);
            console.log(message);


            $scope.$apply(function(){
                $scope.resource.projectRows[0].taskRows[0].entryCells[0].time =
                    timesheet.projectRows[0].taskRows[0].entryCells[0].time;
            })

        });

    });


})
