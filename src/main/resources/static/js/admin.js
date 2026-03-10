document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            fetch(`api/registration/delete/` + id, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    const row = btn.closest('tr');
                    if (row) {
                        row.remove();
                    }
                }
            })
        });
    });

});
$(function() {
    $('.create-btn').click(function () {
        const userId = $(this).data('id');
        const username = $(this).data('user');
        $('#userId').val(userId);
        $('#userNameId').val(username);
    });
});

$(document).ready(function () {
    const rowsPerPage = 15;
    let filteredRows = [];

    const filterData = () => {
        filteredRows = $('#requestTable tbody tr').filter((index, row) => {
            return checkRowFilters(row);
        });
        showPage(1);
    };

    const checkRowFilters = (row) => {
        const employee = $(row).find('td:nth-child(1)').text().toLowerCase();
        const role = $(row).find('td:nth-child(3)').text().toLowerCase();
        const chatId = $(row).find('td:nth-child(4)').text().toLowerCase();

        return (
            employee.includes($('#employee').val().toLowerCase()) &&
            role.includes($('#role').val().toLowerCase()) &&
            chatId.includes($('#chatId').val().toLowerCase())
        );
    };

    const showPage = (page) => {
        $('#requestTable tbody tr').hide();
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        filteredRows.slice(start, end).show();
        renderPagination(page);
    };

    const renderPagination = (currentPage) => {
        $('#pagination').empty();
        const totalFilteredRows = filteredRows.length;
        const totalFilteredPages = Math.ceil(totalFilteredRows / rowsPerPage);
        for (let i = 1; i <= totalFilteredPages; i++) {
            const pageLink = $('<button>')
                .text(i)
                .addClass('btn btn-secondary mx-1')
                .click(() => showPage(i));
            if (i === currentPage) pageLink.addClass('active');
            $('#pagination').append(pageLink);
        }
    };

    $('#employee, #role, #chatId').on('keyup change', filterData);
    filterData();

    $('.toggleInput').on('click', function () {
        $(this).next('.inputContainer').toggle();
    });

    $(document).on('click', function (e) {
        if (!$(e.target).closest('.inputContainer').length && !$(e.target).closest('.toggleInput').length) {
            $('.inputContainer').hide();
        }
    });
    $(function () {
        $('.clear').click(function () {
            document.getElementById('employee').value = '';
            document.getElementById('active').value = '';
            document.getElementById('role').value = '';
            document.getElementById('chatId').value = '';
            filterData();
        })
    });
});

