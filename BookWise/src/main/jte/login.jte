@param String error

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login | BookWise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/style.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <div class="top-logo"><i class="bi bi-bookmark-fill"></i> BookWise</div>
        <h2>Login</h2>

        @if(error != null) {
        <div class="alert alert-danger mb-3">${error}</div>
        }
        @endif
        <form method="post" action="/login">
            <label class="auth-label">Username</label>
            <input class="form-control mb-3" name="username" required>
            <label class="auth-label">Password</label>
            <input class="form-control mb-2" type="password" name="password" required>
            <div class="text-end mb-3"><a href="/forgot_password">Forgot password?</a></div>
            <button class="btn-primary-custom">Submit</button>
        </form>

        <p class="auth-footer">New to BookWise? <a href="/register">Create an account</a></p>
    </div>
</div>
<div class="auth-footer"><i class="bi bi-bookmark-fill"></i> BookWise.io © 2025 AI Book Recommendations.</div>
</body>
</html>

