<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is authenticated and has accountant role
    if (session == null || session.getAttribute("authenticated") == null || 
        !((Boolean)session.getAttribute("authenticated")) || 
        !"theaccountant".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Accountant Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .report-container {
            margin: 20px auto;
            max-width: 1200px;
        }
        .result-container {
            margin-top: 20px;
            overflow-x: auto;
        }
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

    <div class="report-container">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Generate Reports</h5>
                <div class="mb-3">
                    <label for="reportType" class="form-label">Select Report Type</label>
                    <select class="form-select" id="reportType">
                        <option value="">Select a report...</option>
                        <option value="supplier_shipments">Supplier Shipments Report</option>
                        <option value="job_parts">Job Parts Report</option>
                        <option value="city_suppliers">City Suppliers Report</option>
                    </select>
                </div>
                <button onclick="generateReport()" class="btn btn-primary">Generate Report</button>
                <button onclick="clearResults()" class="btn btn-warning">Clear Results</button>
            </div>
        </div>

        <div id="resultContainer" class="result-container"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function generateReport() {
            const reportType = document.getElementById('reportType').value;
            
            if (!reportType) {
                displayError('Please select a report type');
                return;
            }
            
            fetch('<%= request.getContextPath() %>/accountant/execute', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'report=' + encodeURIComponent(reportType)
            })
            .then(response => response.json())
            .then(data => {
                displayResults(data);
            })
            .catch(error => {
                console.error('Error:', error);
                displayError('An error occurred while generating the report');
            });
        }

        function clearResults() {
            document.getElementById('resultContainer').innerHTML = '';
        }

        function displayResults(data) {
            const container = document.getElementById('resultContainer');
            
            if (!data.success) {
                displayError(data.error);
                return;
            }

            if (!data.columns || !data.data) {
                container.innerHTML = `
                    <div class="alert alert-success">
                        Report generated successfully.
                    </div>`;
                return;
            }

            let tableHtml = '<table class="table table-striped"><thead><tr>';
            data.columns.forEach(column => {
                tableHtml += `<th>${column}</th>`;
            });
            tableHtml += '</tr></thead><tbody>';

            data.data.forEach(row => {
                tableHtml += '<tr>';
                row.forEach(cell => {
                    tableHtml += `<td>${cell}</td>`;
                });
                tableHtml += '</tr>';
            });

            tableHtml += '</tbody></table>';
            container.innerHTML = tableHtml;
        }

        function displayError(message) {
            const container = document.getElementById('resultContainer');
            container.innerHTML = `
                <div class="alert alert-danger">
                    Error: ${message}
                </div>`;
        }
    </script>
</body>
</html>
