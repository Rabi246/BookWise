@params org.example.bookwise.model.User user

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Library - BookWise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

@template.fragments.navbar(username = user.getUsername())

<div class="container mt-4">
    <h2 class="mb-4">My Library</h2>

    <div class="alert alert-info">
        <strong>Your saved books will appear here.</strong><br>
        Once you get recommendations, you'll be able to click
        <span class="badge bg-success">Add to Library</span> on books.
    </div>

    <div class="row">
        <!-- Placeholder card until we add book saving later -->
        <div class="col-md-4 mb-3">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">📕 No Books Yet</h5>
                    <p class="card-text text-muted">
                        Your library is empty — start exploring recommendations!
                    </p>
                    <a href="/recommend" class="btn btn-primary btn-sm">Get Recommendations</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
