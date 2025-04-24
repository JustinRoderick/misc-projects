<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% if (session == null || session.getAttribute("authenticated") == null ||
    !((Boolean)session.getAttribute("authenticated")) ||
    !"theaccountant".equals(session.getAttribute("role"))) {
    response.sendRedirect(request.getContextPath() + "/index.jsp");
    return;
} %>
<!DOCTYPE html>
<html>
<head>
    <title>Accountant Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .container { margin-top: 20px; }
        .report-container { margin-top: 20px; }
        table { width: 100%; margin-top: 10px; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">Accountant Dashboard</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="<%= request.getContextPath() %>/logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="row">
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        Available Reports
                    </div>
                    <div class="card-body">
                        <div class="list-group">
                            <button onclick="runReport('max_supplier_status')" class="list-group-item list-group-item-action">
                                Maximum Supplier Status
                            </button>
                            <button onclick="runReport('total_parts_weight')" class="list-group-item list-group-item-action">
                                Total Weight of All Parts
                            </button>
                            <button onclick="runReport('total_shipments')" class="list-group-item list-group-item-action">
                                Total Number of Shipments
                            </button>
                            <button onclick="runReport('max_workers_job')" class="list-group-item list-group-item-action">
                                Job with Most Workers
                            </button>
                            <button onclick="runReport('supplier_status_list')" class="list-group-item list-group-item-action">
                                Supplier Status List
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        Report Results
                    </div>
                    <div class="card-body">
                        <div id="resultContainer">
                            <p class="text-muted">Select a report to view results</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function runReport(reportType) {
            const data = new URLSearchParams();
            data.append('report_type', reportType);

            fetch('<%= request.getContextPath() %>/accountant/execute', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: data
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    displayResults(data);
                } else {
                    displayError(data.error);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                displayError('An error occurred while running the report');
            });
        }

        function displayResults(data) {
            const container = document.getElementById('resultContainer');
            
            // Create table
            let html = '<table class="table table-striped table-bordered">';
            
            // Add headers
            html += '<thead class="table-dark"><tr>';
            data.columns.forEach(column => {
                html += '<th>' + column + '</th>';
            });
            html += '</tr></thead>';
            
            // Add data rows
            html += '<tbody>';
            data.data.forEach(row => {
                html += '<tr>';
                row.forEach(cell => {
                    html += '<td>' + (cell === null ? '' : cell) + '</td>';
                });
                html += '</tr>';
            });
            html += '</tbody></table>';
            
            container.innerHTML = html;
        }

        function displayError(message) {
            const container = document.getElementById('resultContainer');
            container.innerHTML = 
                '<div class="alert alert-danger">' +
                    'Error: ' + message +
                '</div>';
        }
    </script>
</body>
</html>
