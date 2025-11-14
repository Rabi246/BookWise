<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Book Search | BookWise</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/home_page">📚 BookWise</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                <li class="nav-item"><a class="nav-link " href="/mybooks">My Library</a></li>
                <li class="nav-item"><a class="nav-link"  href="/home_page">Recommendations</a></li>
                <li class="nav-item"><a class="nav-link active" href="/booksearch">Book Search</a></li>
                </li>
            </ul>
        </div>

    </div>
</nav>


<div class="container mt-4">
    <h2 class="mb-3 fw-bold">🔎 Google Books Search</h2>

    <div class="input-group mb-3">
        <input id="searchBox" type="text" class="form-control" placeholder="Search for a book...">
        <button class="btn btn-primary" onclick="searchBook()">Search</button>
    </div>

    <div id="results" class="row g-3"></div>
</div>


@raw
<script>
    function searchBook() {
        const q = document.getElementById("searchBox").value.trim();
        if (!q) return;

        fetch(`https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(q)}&maxResults=20`)
            .then(res => res.json())
            .then(data => {
                const resultsDiv = document.getElementById("results");
                resultsDiv.innerHTML = "";

                if (!data.items) {
                    resultsDiv.innerHTML = "<p class='text-danger'>No results found.</p>";
                    return;
                }

                data.items.forEach(book => {
                    const info = book.volumeInfo;

                    const img = info.imageLinks?.thumbnail || "/static/img/placeholder_book.png";
                    const title = info.title || "No Title";
                    const authors = info.authors ? info.authors.join(", ") : "Unknown Author";

                    resultsDiv.innerHTML += `
                    <div class="col-md-4">
                        <div class="card shadow-sm h-100">
                            <img src="${img}" class="card-img-top" style="height:250px; object-fit:cover;">
                            <div class="card-body">
                                <h5 class="card-title">${title}</h5>
                                <p class="text-muted">${authors}</p>
                            </div>
                        </div>
                    </div>
                `;
                });
            });
    }
</script>
@endraw


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
