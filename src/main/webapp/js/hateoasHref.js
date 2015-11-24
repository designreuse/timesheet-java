angular.module("timesheetModule").filter("hateoasHref", function(){
    return function(href){
        if(href) {
            // http://local:8080/api/timesheets/yyyy-mm-dd
            href = URI(href).resource(); //arranca hhtp, localhost etc
            // /api/timesheets/yyyy-mm-dd
            var index = href.indexOf("/", 1)
            href = href.substring(index)
            //     /timesheets/yyyy-mm-dd
            return "#" + href
        }
        return null
        /*
         return href.replace("/api/","/#/");
         */
    }
})