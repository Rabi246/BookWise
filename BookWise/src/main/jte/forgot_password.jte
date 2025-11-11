<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Forgot Password | BookWise</title>

    <!-- ✅ Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- ✅ Your CSS -->
    <link rel="stylesheet" href="/css/style.css">

    <!-- ✅ Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
</head>

<body>

<div class="auth-container">
    <div class="auth-card">

        <div class="top-logo">
            <i class="bi bi-bookmark-fill"></i>
            BookWise
        </div>

        <h2>Forgot Password</h2>

        <form method="post" action="/forgot_password">

            <label class="auth-label">Username</label>
            <input class="form-control mb-2" name="username" required>

            <label class="auth-label">Email</label>
            <input class="form-control mb-3" type="email" name="email" required>

            <button class="btn-primary-custom">Verify Account</button>
        </form>

        <p class="auth-footer">
            Remember your password? <a href="/login">Go back to Login</a>
        </p>

    </div>
</div>

<div class="auth-footer">
    <i class="bi bi-bookmark-fill"></i> BookWise.io © 2025 AI Book Recommendations.
</div>

</body>
</html>
