/**
 * Created by deinf.rcsilva on 18/11/2015.
 */
angular.module("timesheetModule").directive("hateoasA", function() {
    return {
        scope: { // atributos da tag/diretiva
            resource: "=", // = significa que o parâmetro é um objeto
            rel: "@" // @ significa que o parâmetro é uma constante
        },
        templateUrl: "html/hateoasA.html",
        transclude: true // trazer o que estiver entre o abre e fecha tag para o template.
                         // esse código vai parar no ng-transclude
    }
})
