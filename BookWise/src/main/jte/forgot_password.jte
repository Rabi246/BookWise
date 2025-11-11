<!DOCTYPE html>
<html>
<head>
    <title>Forgot Password</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>

<body class="d-flex justify-content-center align-items-center" style="height:100vh;background:#eef4ff;">
<div class="p-4 bg-white shadow rounded" style="width:350px;">

    <h3 class="text-center mb-3">Forgot Password</h3>


    <form method="post" action="/forgot_password">
        <div class="mb-2">
            <label class="form-label">Username</label>
            <input name="username" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Email</label>
            <input name="email" type="email" class="form-control" required>
        </div>

        <button class="btn btn-primary w-100">Verify Account</button>
    </form>

    <div class="text-center mt-2">
        <a href="/login">Back to Login</a>
    </div>

</div>
</body>
</html>
