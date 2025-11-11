<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Account | BookWise</title>

    <!-- ✅ Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- ✅ Your CSS file -->
    <link rel="stylesheet" href="/css/style.css">

    <!-- ✅ Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
</head>

<body>

<div class="auth-container">
    <div class="auth-card">

        <div class="top-logo">
            <i class="bi bi-bookmark-fill"></i>
            BookWise
        </div>

        <h2>Create Account</h2>

        <form method="post" action="/register">

            <label class="auth-label">Full Name</label>
            <input class="form-control mb-2" name="name" required>

            <label class="auth-label">Username</label>
            <input class="form-control mb-2" name="username" required>

            <label class="auth-label">Email</label>
            <input class="form-control mb-2" type="email" name="email" required>

            <label class="auth-label">Password</label>
            <input class="form-control mb-2" type="password" name="password" required>

            <label class="auth-label">Confirm Password</label>
            <input class="form-control mb-3" type="password" name="confirmPassword" required>

            <button class="btn-primary-custom">Create Account</button>
        </form>

        <p class="auth-footer">
            Already have an account? <a href="/login">Login here</a>
        </p>

    </div>
</div>

<div class="auth-footer">
    <i class="bi bi-bookmark-fill"></i> BookWise.io © 2025 AI Book Recommendations.
</div>

</body>
</html>
