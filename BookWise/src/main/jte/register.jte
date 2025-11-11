<!DOCTYPE html>
<html>
<head>
    <title>Create Account</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>

<body class="d-flex justify-content-center align-items-center"
      style="height:100vh; background:#f4f8ff">

<div class="p-4 bg-white shadow rounded" style="width:350px;">
    <h3 class="text-center mb-3">Create Account</h3>


    <form method="post" action="/register">

        <div class="mb-2">
            <label class="form-label">Full Name</label>
            <input name="name" class="form-control" required>
        </div>

        <div class="mb-2">
            <label class="form-label">Username</label>
            <input name="username" class="form-control" required>
        </div>

        <div class="mb-2">
            <label class="form-label">Email</label>
            <input name="email" type="email" class="form-control" required>
        </div>

        <div class="mb-2">
            <label class="form-label">Password</label>
            <input name="password" type="password" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Confirm Password</label>
            <input name="confirmPassword" type="password" class="form-control" required>
        </div>

        <button class="btn btn-success w-100">Register</button>
    </form>

    <div class="text-center mt-2">
        <a href="/login">Already have an account?</a>
    </div>
</div>

</body>
</html>
