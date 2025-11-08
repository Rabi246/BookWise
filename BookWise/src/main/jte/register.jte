<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - BookWise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container d-flex justify-content-center align-items-center" style="min-height:100vh;">
    <div class="card shadow p-4" style="max-width:420px; width:100%;">
        <h3 class="text-center mb-4">Create an Account</h3>


        <form action="/register" method="post">
            <div class="mb-3">
                <label class="form-label">Username</label>
                <input required name="username" type="text" class="form-control">
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <input required name="password" type="password" class="form-control">
            </div>

            <button type="submit" class="btn btn-success w-100 mt-2">Create Account</button>
        </form>

        <div class="text-center mt-3">
            <small>Already have an account? <a href="/login">Login here</a></small>
        </div>
    </div>
</div>

</body>
</html>