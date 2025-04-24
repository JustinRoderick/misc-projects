<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
  <head>
    <title>Root User Dashboard</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <style>
      .query-container {
        margin: 20px auto;
        max-width: 1200px;
      }
      .result-container {
        margin-top: 20px;
        overflow-x: auto;
      }
      pre {
        background-color: #f8f9fa;
        padding: 15px;
        border-radius: 5px;
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

    <div class="query-container">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">SQL Command Execution</h5>
          <form id="sqlForm">
            <div class="mb-3">
              <textarea
                class="form-control"
                id="sqlQuery"
                rows="5"
                placeholder="Enter your SQL command here"
              ></textarea>
            </div>
            <div class="mb-3">
              <button type="submit" class="btn btn-primary">
                Execute Command
              </button>
              <button
                type="button"
                class="btn btn-secondary"
                onclick="resetForm()"
              >
                Reset Form
              </button>
              <button
                type="button"
                class="btn btn-warning"
                onclick="clearResults()"
              >
                Clear Results
              </button>
            </div>
          </form>
        </div>
      </div>

      <div id="resultContainer" class="result-container"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      document
        .getElementById("sqlForm")
        .addEventListener("submit", function (e) {
          e.preventDefault();
          const sql = document.getElementById("sqlQuery").value;
          console.log("Executing SQL:", sql);

          fetch("<%= request.getContextPath() %>/root/execute", {
            method: "POST",
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
            body: "sql=" + encodeURIComponent(sql),
          })
            .then((response) => {
              console.log("Response status:", response.status);
              return response.text().then((text) => {
                console.log("Raw response:", text);
                try {
                  return JSON.parse(text);
                } catch (e) {
                  console.error("JSON parse error:", e);
                  throw new Error("Invalid JSON response");
                }
              });
            })
            .then((data) => {
              console.log("Parsed data:", data);
              displayResults(data);
            })
            .catch((error) => {
              console.error("Error:", error);
              displayError(
                "An error occurred while executing the query: " + error.message
              );
            });
        });

      function resetForm() {
        document.getElementById("sqlQuery").value = "";
      }

      function clearResults() {
        document.getElementById("resultContainer").innerHTML = "";
      }

      function displayResults(data) {
        console.log("Displaying results:", data);
        const container = document.getElementById("resultContainer");

        if (!data.success) {
          displayError(data.error);
          return;
        }

        if (data.rowsAffected !== undefined) {
          container.innerHTML =
            '<div class="alert alert-success">Query executed successfully. Rows affected: ' +
            data.rowsAffected +
            "</div>";
          return;
        }

        if (!data.columns || !data.data) {
          container.innerHTML =
            '<div class="alert alert-success">Query executed successfully.</div>';
          return;
        }

        let tableHtml = '<table class="table table-striped"><thead><tr>';
        data.columns.forEach((column) => {
          tableHtml += "<th>" + column + "</th>";
        });
        tableHtml += "</tr></thead><tbody>";

        data.data.forEach((row) => {
          tableHtml += "<tr>";
          row.forEach((cell) => {
            tableHtml += "<td>" + (cell === null ? "null" : cell) + "</td>";
          });
          tableHtml += "</tr>";
        });

        tableHtml += "</tbody></table>";
        container.innerHTML = tableHtml;
      }

      function displayError(message) {
        console.error("Error:", message);
        const container = document.getElementById("resultContainer");
        container.innerHTML =
          '<div class="alert alert-danger">Error: ' + message + "</div>";
      }
    </script>
  </body>
</html>
