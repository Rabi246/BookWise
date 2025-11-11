<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>

<body class="d-flex justify-content-center align-items-center" style="height:100vh;background:#eef4ff;">
<div class="p-4 bg-white shadow rounded" style="width:350px;">

    <h3 class="text-center mb-3">Reset Password</h3>


    <form method="post" action="/reset_password">
        <div class="mb-2">
            <label class="form-label">New Password</label>
            <input name="password" type="password" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Confirm Password</label>
            <input name="confirmPassword" type="password" class="form-control" required>
        </div>

        <button class="btn btn-success w-100">Reset Password</button>
    </form>

</div>
</body>
</html>
