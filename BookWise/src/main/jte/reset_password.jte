<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reset Password | BookWise</title>

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

        <h2>Reset Password</h2>

        <form method="post" action="/reset_password">

            <label class="auth-label">New Password</label>
            <input class="form-control mb-2" type="password" name="password" required>

            <label class="auth-label">Confirm Password</label>
            <input class="form-control mb-3" type="password" name="confirmPassword" required>

            <button class="btn-primary-custom">Reset Password</button>
        </form>

        <p class="auth-footer">
            <a href="/login">Back to Login</a>
        </p>

    </div>
</div>

<div class="auth-footer">
    <i class="bi bi-bookmark-fill"></i> BookWise.io © 2025 AI Book Recommendations.
</div>

</body>
</html>
