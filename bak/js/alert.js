(function () {
    "use strict"

    angular.module("timesheetModule").factory("alertService", [function () {
        var alerts = []

        var that = {
            DANGER: "danger",
            INFO: "info",
            SUCCESS: "success",
            WARNING: "warning"
        }

        that.getAlerts = function () {
            return alerts
        }

        that.addAlert = function (alert) {
            alerts.push(alert)
        }

        function addAlert(type, message) {
            that.addAlert({
                type: type,
                message: message
            })
        }

        that.addDangerAlert = function (message) {
            addAlert(that.DANGER, message)
        }

        that.addInfoAlert = function (message) {
            addAlert(that.INFO, message)
        }

        that.addSuccessAlert = function (message) {
            addAlert(that.SUCCESS, message)
        }

        that.addWarningAlert = function (message) {
            addAlert(that.WARNING, message)
        }

        that.clearAlerts = function () {
            alerts = []
        }

        return that
    }])
})()