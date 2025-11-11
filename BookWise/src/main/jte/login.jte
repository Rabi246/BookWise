<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">

    <style>
        body {
            background-color: #f4f8ff;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            font-family: Arial, sans-serif;
        }

        .login-card {
            width: 350px;
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .login-card h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #2d4a7c;
        }

        .login-card button {
            width: 100%;
        }

        .error-message {
            color: red;
            text-align: center;
            margin-bottom: 10px;
            font-size: 0.9rem;
        }
    </style>
</head>

<body>
<div class="login-card">
    <h2>Login</h2>


    <form method="post" action="/login">
        <div class="mb-3">
            <label class="form-label">Username</label>
            <input name="username" type="text"
                   class="form-control" placeholder="Enter username" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Password</label>
            <input name="password"
                   type="password" class="form-control"
                   placeholder="Enter password" required>
        </div><br>
        <div class="mt-3 text-center">
            <a href="/register">Create an Account</a>
        </div>
        <div class="mt-3 text-center">
            <a href="/forgot_password">forgot password?</a>
        </div>



        <button class="btn btn-primary" type="submit">Log In</button>
    </form>
</div>
</body>
</html>
