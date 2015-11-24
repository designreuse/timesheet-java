(function () {
    "use strict"

    angular.module("timesheetModule").factory("errorService", [function () {
        var errors

        var that = {}

        that.setErrors = function (newErrors) {
            errors = newErrors
        }

        that.getErrors = function () {
            return errors
        }

        that.clearErrors = function () {
            errors = null
        }

        that.hasFieldErrors = function (field) {
            return errors && _.some(errors.fieldErrors, function (error) {
                    return error.field === field
                })
        }

        that.getFieldErrors = function (field) {
            return errors && _.filter(errors.fieldErrors, function (error) {
                    return error.field === field
                })
        }

        return that
    }])
})()