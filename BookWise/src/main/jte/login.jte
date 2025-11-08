

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - BookWise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container d-flex justify-content-center align-items-center" style="min-height:100vh;">
    <div class="card shadow p-4" style="max-width:400px; width:100%;">
        <h3 class="text-center mb-3">Welcome to BookWise</h3>
        <h5 class="text-center text-secondary mb-4">Login</h5>


        <form action="/login" method="post">
            <div class="mb-3">
                <label class="form-label">Username</label>
                <input required name="username" type="text" class="form-control">
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <input required name="password" type="password" class="form-control">
            </div>

            <button type="submit" class="btn btn-primary w-100 mt-2">Login</button>
        </form>

        <div class="text-center mt-3">
            <small>Don't have an account? <a href="/register">Register here</a></small>
        </div>
    </div>
</div>

</body>
</html>
