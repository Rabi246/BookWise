@param String username

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Books | BookWise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="/home_page">📚 BookWise</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link active" href="/mybooks">My Library</a></li>
                <li class="nav-item"><a class="nav-link"  href="/home_page">Recommendations</a></li>
                <li class="nav-item"><a class="nav-link" href="/booksearch">Search</a></li>
            </ul>

            @if(username != null) {
            <span class="navbar-text text-white me-3">Welcome, ${username}</span>
            }
            @endif
        </div>
    </div>
</nav>


<div class="container">

    <div class="text-center">
        <h2 class="fw-bold">📘 My Books</h2>
        <p class="text-muted">Your saved and purchased books will appear here.</p>
    </div>

    <!-- Placeholder Card -->
    <div class="card shadow-sm mt-4">
        <div class="card-body text-center py-5">
            <h5 class="text-secondary">You don’t have any books yet.</h5>
            <p class="text-muted">Start browsing and adding books to your library!</p>

            <a href="/home_page" class="btn btn-primary mt-3">Search for Books</a>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
