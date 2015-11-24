var POC = {
    /**
     * Performs REST call to specified path
     * @param {string} url to call
     * @param {string} method HTTP method to use, e.g. GET, POST, PUT, etc.
     * @param {object} payLoad payload to supply
     * @param {function=} callback function to call upon result. When null Promise is returned
     * @returns Promise if callback function is not specified and nothing otherwise
     */
    call: function (url, method, payLoad, callback) {
        if (callback) {
            $.ajax((url + "?_ac=" + (new Date()).getTime()), {
                type: method,
                contentType: 'application/json',
                data: JSON.stringify(payLoad),
                dataType: 'json'
            }).done(function (json) {
                callback(json);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                callback(undefined, jqXHR.statusCode().status, errorThrown);
            });
            return undefined;
        } else {
            var dfd = $.Deferred();
            POC.call(url, method, payLoad, function (res) {
                dfd.resolve(res);
            });
            return dfd.promise();
        }
    }
};
