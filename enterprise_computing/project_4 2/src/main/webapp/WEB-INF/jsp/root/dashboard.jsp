<%@ page contentType="text/html;charset=UTF-8" language="java" %> <% if (session
== null || session.getAttribute("authenticated") == null ||
!((Boolean)session.getAttribute("authenticated")) ||
!"root".equals(session.getAttribute("role"))) {
response.sendRedirect(request.getContextPath() + "/index.jsp"); return; } %>
<!DOCTYPE html>
<html>
  <head>
    <title>Root Dashboard</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <style>
      .container {
        margin-top: 20px;
      }
      .sql-form {
        margin-bottom: 20px;
      }
      .result-container {
        margin-top: 20px;
      }
      table {
        width: 100%;
        margin-top: 10px;
      }
      textarea {
        font-family: monospace;
      }
    </style>
  </head>
  <body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand" href="#">Root Dashboard</a>
        <div class="navbar-nav ms-auto">
          <a class="nav-link" href="<%= request.getContextPath() %>/logout"
            >Logout</a
          >
        </div>
      </div>
    </nav>

    <div class="container">
      <div class="card">
        <div class="card-body">
          <div class="sql-form">
            <div class="mb-3">
              <label for="sqlCommand" class="form-label">SQL Command</label>
              <textarea
                class="form-control"
                id="sqlCommand"
                rows="4"
                placeholder="Enter your SQL command here..."
              ></textarea>
            </div>
            <div class="btn-group" role="group">
              <button onclick="executeSql()" class="btn btn-primary">
                Execute Command
              </button>
              <button onclick="resetForm()" class="btn btn-secondary">
                Reset Form
              </button>
              <button onclick="clearResults()" class="btn btn-warning">
                Clear Results
              </button>
            </div>
          </div>
          <div id="resultContainer" class="result-container">
            <!-- Results will be displayed here -->
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      function executeSql() {
        const sql = document.getElementById("sqlCommand").value;
        if (!sql) {
          displayError("Please enter a SQL command");
          return;
        }

        const data = new URLSearchParams();
        data.append("sql", sql);

        fetch("<%= request.getContextPath() %>/root/execute", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          body: data,
        })
          .then((response) => response.json())
          .then((data) => {
            if (data.success) {
              if (data.columns && data.data) {
                displayResults(data);
              } else if (data.rowsAffected !== undefined) {
                displayMessage(
                  `Command executed successfully. Rows affected: ${data.rowsAffected}`
                );
              } else {
                displayMessage("Command executed successfully.");
              }
            } else {
              displayError(data.error);
            }
          })
          .catch((error) => {
            console.error("Error:", error);
            displayError("An error occurred while executing the command");
          });
      }

      function resetForm() {
        document.getElementById("sqlCommand").value = "";
      }

      function clearResults() {
        document.getElementById("resultContainer").innerHTML = "";
      }

      function displayResults(data) {
        const container = document.getElementById("resultContainer");

        let html = '<table class="table table-striped table-bordered">';

        html += '<thead class="table-dark"><tr>';
        data.columns.forEach((column) => {
          html += "<th>" + column + "</th>";
        });
        html += "</tr></thead>";

        // Add data rows
        html += "<tbody>";
        data.data.forEach((row) => {
          html += "<tr>";
          row.forEach((cell) => {
            html += "<td>" + (cell === null ? "" : cell) + "</td>";
          });
          html += "</tr>";
        });
        html += "</tbody></table>";

        container.innerHTML = html;
      }

      function displayMessage(message) {
        const container = document.getElementById("resultContainer");
        container.innerHTML =
          '<div class="alert alert-success">' + message + "</div>";
      }

      function displayError(message) {
        const container = document.getElementById("resultContainer");
        container.innerHTML =
          '<div class="alert alert-danger">' + "Error: " + message + "</div>";
      }
    </script>
  </body>
</html>
