/**
 * Created by deinf.rcsilva on 20/11/2015.
 */
angular.module("timesheetModule")
    .factory("apiClient", function(halClient) {
        var that = {}

        that.$get = function (href) {
            return halClient.$get("/api" + href)
        }

        return that;
    })