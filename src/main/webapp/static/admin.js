/**
 * Extensions to POC namespace related to customer actions.
 *
 * Implemented as set of statics due to POC nature. Normally JS framework should be defined, when each page of UI is
 * backed by separate JavaScript class, performing handling all actions on the page
 *
 * POC-specific usages (things normally restricted for UI development, however used in this POC):
 * Tables are used for rendering simplicity. Div/CSS3 classes should be used instead.
 * Direct CSS manipulation is used. CSS classes manipulation should be used instead.
 */
POC = $.extend(POC, {
    /**
     * Performs AJAX search for customer according to given criteria
     * @param {string} criteria to search
     */
    searchUser: function (criteria) {
        POC.call('/rest/user/search/' + criteria, 'get', {}, function (result) {
            POC.renderUsers(result);
        });
    },
    /**
     * Renderer for customer
     * @param users
     */
    renderUsers: function (users) {
        var ph = $('#search-results');
        ph.html('');
        $.each(users, function (i, r) {
            ph.append($('<p><a href="customers/details?id=' + r.id + '">' + r.name + '</a></p>'))
        });
    },
    /**
     * Performs AJAX search for accounts according to criteria from the from
     * Data is fetched directly from form fields.
     */
    searchAccounts: function () {
        var criteria = {
            accountId: $('#criteria-account').val() || null,
            currencyCode: $('#criteria-currency').val() || null,
            userCriteria: $('#criteria-user').val() || null
        };
        POC.call('/rest/account/search', 'post', criteria, function (result) {
            POC.renderAccounts(result)
        });
    },
    /**
     * Renderer for accounts
     * @param accounts
     */
    renderAccounts: function (accounts) {
        var ph = $('#search-results'),
            table = $('<table></table>');
        table.append('<tr><th>Customer</th><th>Name</th><th>Account</th><th>Amount</th><th>Actions</th></tr>');
        ph.html('').append(table);
        $.each(accounts, function (i, account) {
            var data = [];
            data.push('<tr>');
            data.push('<td>' + account.user.id + "</td>");
            data.push('<td>' + account.user.name + "</td>");
            data.push('<td>' + account.id + "</td>");
            data.push('<td>' + account.amount + ' ' + account.currency.uiCode + "</td>");
            data.push('<td><a href="/admin/transactions/?id=' + account.id + '">show transactions..</a></td>');
            data.push('</tr>');

            table.append(data.join(''));
        });
    },
    /**
     * Perform AJAX fetch of transactions and currencies for transactions view
     * @param {number} accountId account id to fetch transactions for
     */
    listTransactions: function (accountId) {
        $.when(
            POC.call('/rest/bank/currencies', 'get', {}),
            POC.call('/rest/account/' + accountId + '/tx', 'get', {})
        ).then(function (currencies, transactions) {
            POC.renderTransactions(currencies, transactions);
        });
    },
    /**
     * Renderer for transactions list
     * @param currencies array of currencies
     * @param transactions array of transactions
     */
    renderTransactions: function (currencies, transactions) {
        var ph = $('#transactions-list'),
            ac = {},
            types = ['transfer', 'withdraw', 'deposit'];

        // arrange currencies
        $.each(currencies, function (i, c) {
            ac[c.code] = c.uiCode;
        });
        ph.append('<tr><th>Date</th><th>Type</th><th>State</th><th>Amount</th>' +
            '<th>Origin</th><th>Amount</th><th>Recipient</th><th>Amount</th></tr>');

        // list transactions & inject into page
        $.each(transactions, function (i, t) {
            var data = [];
            data.push('<tr>');
            data.push('<td>' + new Date(t.createdOn).toLocaleString() + '</td>');
            data.push('<td>' + types[t.txType] + '</td>');
            data.push('<td>' + ((t.state == 0) ? 'OK' : 'FAIL') + '</td>');
            data.push('<td>' + t.txAmount + ' ' + ac[t.txCurrency] + '</td>');
            data.push('<td>' + (t.originId ? '<a href="/admin/accounts/details?id=' + t.originId + '">' + t.originId + '</a>' : '') + '</td>');
            data.push('<td>' + (t.originId ? (t.originAmount + ' ' + ac[t.originCurrency] ) : '') + '</td>');
            data.push('<td>' + (t.recipientId ? '<a href="/admin/accounts/details?id=' + t.recipientId + '">' + t.recipientId + '</a>' : '') + '</td>');
            data.push('<td>' + (t.recipientId ? (t.recipientAmount + ' ' + ac[t.recipientCurrency] ) : '') + '</td>');
            data.push('</tr>');

            ph.append(data.join(''));
        })
    }
});