POC = $.extend(POC, {
    deposit: function (accountId) {
        $('.result').css('display', 'none');
        var request = {
            accountId: accountId,
            amount: $('#deposit-amount').val(),
            currencyId: $('#deposit-currency').val()
        };
        POC.call('/rest/atm/deposit', 'put', request, function (response) {
            if (response) {
                var resultDiv = '#operation-success';
            } else {
                resultDiv = '#operation-fail-unknown'
            }

            $(resultDiv).css('display', 'block');
        });
    },
    withdraw: function (accountId) {
        var request = {
            accountId: accountId,
            amount: $('#withdraw-amount').val(),
            currencyId: $('#withdraw-currency').val()
        };
        $('.result').css('display', 'none');
        POC.call('/rest/atm/withdraw', 'put', request, function (response, code) {
            if (response) {
                var resultDiv = '#operation-success';
            } else {
                if (code == 422) {
                    resultDiv = '#operation-fail'
                } else {
                    resultDiv = '#operation-fail-unknown'
                }
            }

            $(resultDiv).css('display', 'block');
        });
    }
});