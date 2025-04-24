<%@ page contentType="text/html;charset=UTF-8" language="java" %> <% if (session
== null || session.getAttribute("authenticated") == null ||
!((Boolean)session.getAttribute("authenticated")) ||
!"dataentry".equals(session.getAttribute("role"))) {
response.sendRedirect(request.getContextPath() + "/index.jsp"); return; } %>
<!DOCTYPE html>
<html>
  <head>
    <title>Data Entry Dashboard</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <style>
      .form-container {
        margin: 20px auto;
        max-width: 1200px;
      }
      .result-container {
        margin-top: 20px;
      }
    </style>
  </head>
  <body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand" href="#">Data Entry Dashboard</a>
        <div class="navbar-nav ms-auto">
          <a class="nav-link" href="<%= request.getContextPath() %>/logout"
            >Logout</a
          >
        </div>
      </div>
    </nav>

    <div class="form-container">
      <ul class="nav nav-tabs" id="entryTabs" role="tablist">
        <li class="nav-item">
          <a
            class="nav-link active"
            id="suppliers-tab"
            data-bs-toggle="tab"
            href="#suppliers"
            role="tab"
            >Suppliers</a
          >
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            id="parts-tab"
            data-bs-toggle="tab"
            href="#parts"
            role="tab"
            >Parts</a
          >
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            id="jobs-tab"
            data-bs-toggle="tab"
            href="#jobs"
            role="tab"
            >Jobs</a
          >
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            id="shipments-tab"
            data-bs-toggle="tab"
            href="#shipments"
            role="tab"
            >Shipments</a
          >
        </li>
      </ul>

      <div class="tab-content mt-3">
        <!-- Suppliers Form -->
        <div class="tab-pane fade show active" id="suppliers" role="tabpanel">
          <form id="supplierForm" onsubmit="submitForm(event, 'suppliers')">
            <div class="mb-3">
              <label for="snum" class="form-label">Supplier Number</label>
              <input
                type="text"
                class="form-control"
                id="snum"
                name="snum"
                required
              />
            </div>
            <div class="mb-3">
              <label for="sname" class="form-label">Supplier Name</label>
              <input
                type="text"
                class="form-control"
                id="sname"
                name="sname"
                required
              />
            </div>
            <div class="mb-3">
              <label for="status" class="form-label">Status</label>
              <input
                type="number"
                class="form-control"
                id="status"
                name="status"
                required
              />
            </div>
            <div class="mb-3">
              <label for="scity" class="form-label">City</label>
              <input
                type="text"
                class="form-control"
                id="scity"
                name="city"
                required
              />
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
            <button type="reset" class="btn btn-secondary">Reset</button>
          </form>
        </div>

        <!-- Parts Form -->
        <div class="tab-pane fade" id="parts" role="tabpanel">
          <form id="partForm" onsubmit="submitForm(event, 'parts')">
            <div class="mb-3">
              <label for="pnum" class="form-label">Part Number</label>
              <input
                type="text"
                class="form-control"
                id="pnum"
                name="pnum"
                required
              />
            </div>
            <div class="mb-3">
              <label for="pname" class="form-label">Part Name</label>
              <input
                type="text"
                class="form-control"
                id="pname"
                name="pname"
                required
              />
            </div>
            <div class="mb-3">
              <label for="color" class="form-label">Color</label>
              <input
                type="text"
                class="form-control"
                id="color"
                name="color"
                required
              />
            </div>
            <div class="mb-3">
              <label for="weight" class="form-label">Weight</label>
              <input
                type="number"
                step="0.01"
                class="form-control"
                id="weight"
                name="weight"
                required
              />
            </div>
            <div class="mb-3">
              <label for="pcity" class="form-label">City</label>
              <input
                type="text"
                class="form-control"
                id="pcity"
                name="city"
                required
              />
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
            <button type="reset" class="btn btn-secondary">Reset</button>
          </form>
        </div>

        <!-- Jobs Form -->
        <div class="tab-pane fade" id="jobs" role="tabpanel">
          <form id="jobForm" onsubmit="submitForm(event, 'jobs')">
            <div class="mb-3">
              <label for="jnum" class="form-label">Job Number</label>
              <input
                type="text"
                class="form-control"
                id="jnum"
                name="jnum"
                required
              />
            </div>
            <div class="mb-3">
              <label for="jname" class="form-label">Job Name</label>
              <input
                type="text"
                class="form-control"
                id="jname"
                name="jname"
                required
              />
            </div>
            <div class="mb-3">
              <label for="numworkers" class="form-label"
                >Number of Workers</label
              >
              <input
                type="number"
                class="form-control"
                id="numworkers"
                name="numworkers"
                required
              />
            </div>
            <div class="mb-3">
              <label for="jcity" class="form-label">City</label>
              <input
                type="text"
                class="form-control"
                id="jcity"
                name="city"
                required
              />
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
            <button type="reset" class="btn btn-secondary">Reset</button>
          </form>
        </div>

        <!-- Shipments Form -->
        <div class="tab-pane fade" id="shipments" role="tabpanel">
          <form id="shipmentForm" onsubmit="submitForm(event, 'shipments')">
            <div class="mb-3">
              <label for="shipsnum" class="form-label">Supplier Number</label>
              <input
                type="text"
                class="form-control"
                id="shipsnum"
                name="snum"
                required
              />
            </div>
            <div class="mb-3">
              <label for="shippnum" class="form-label">Part Number</label>
              <input
                type="text"
                class="form-control"
                id="shippnum"
                name="pnum"
                required
              />
            </div>
            <div class="mb-3">
              <label for="shipjnum" class="form-label">Job Number</label>
              <input
                type="text"
                class="form-control"
                id="shipjnum"
                name="jnum"
                required
              />
            </div>
            <div class="mb-3">
              <label for="quantity" class="form-label">Quantity</label>
              <input
                type="number"
                class="form-control"
                id="quantity"
                name="quantity"
                required
              />
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
            <button type="reset" class="btn btn-secondary">Reset</button>
          </form>
        </div>
      </div>

      <div id="resultContainer" class="result-container mt-3"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      function submitForm(event, table) {
        event.preventDefault();
        const form = event.target;
        const formData = new FormData(form);
        const data = new URLSearchParams();

        data.append("table", table);
        for (let pair of formData.entries()) {
          data.append(pair[0], pair[1]);
        }

        fetch("<%= request.getContextPath() %>/dataentry/execute", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          body: data,
        })
          .then((response) => response.json())
          .then((data) => {
            displayResult(data);
          })
          .catch((error) => {
            console.error("Error:", error);
            displayError("An error occurred while submitting the form");
          });
      }

      function displayResult(data) {
        const container = document.getElementById("resultContainer");

        if (!data.success) {
          displayError(data.error);
          return;
        }

        container.innerHTML = `
                <div class="alert alert-success">
                    Data saved successfully. Rows affected: ${data.rowsAffected}
                </div>`;
      }

      function displayError(message) {
        const container = document.getElementById("resultContainer");
        container.innerHTML = `
                <div class="alert alert-danger">
                    Error: ${message}
                </div>`;
      }
    </script>
  </body>
</html>
