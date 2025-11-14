@param String username
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Book Search | BookWise</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
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
                <li class="nav-item"><a class="nav-link" href="/mybooks">My Library</a></li>
                <li class="nav-item"><a class="nav-link active" href="/home_page">Recommendations</a></li>
                <li class="nav-item"><a class="nav-link" href="/booksearch">Search</a></li>
            </ul>

            @if(username != null)
                <div class="d-flex align-items-center">
                    <span class="navbar-text text-white me-3">Welcome, ${username}</span>
                    <a class="btn btn-outline-light btn-sm" href="/logout">Logout</a>
                </div>
            @endif
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

        // Show loading spinner
        const resultsDiv = document.getElementById("results");
        resultsDiv.innerHTML =
            '<div class="text-center mt-5"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Searching...</p></div>';

        // NOTE: This fetch still calls the Google API directly as per your previous choice.
        // If you were using the Spring Boot backend, the URL would be: `/api/searchBooks?q=${encodeURIComponent(q)}`
        fetch(`https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(q)}&maxResults=20`)
            .then(res => res.json())
            .then(data => {
                resultsDiv.innerHTML = "";

                if (!data.items) {
                    resultsDiv.innerHTML = "<p class='text-danger'>No results found</p>";
                    return;
                }

                data.items.forEach(book => {
                    const info = book.volumeInfo;
                    const bookId = book.id;

                    const title = info.title || "No Title";
                    const authors = info.authors ? info.authors.join(", ") : "Unknown Author";
                    const img = info.imageLinks?.thumbnail ? info.imageLinks.thumbnail.replace('http:', 'https:') : "https://via.placeholder.com/80x120?text=No+Cover";

                    // --- Book details for saving ---
                    const description = info.description || "";
                    const truncatedDesc = description.length > 150
                        ? description.substring(0, 150) + "..."
                        : description;

                    // Escape quotes for safe insertion into the HTML onclick attribute
                    const safeTitle = title.replace(/'/g, "\\'");
                    const safeAuthors = authors.replace(/'/g, "\\'");
                    const safeDescription = description.replace(/'/g, "\\'");

                    resultsDiv.innerHTML += `
                    <div class="col-md-4">
                        <div class="card shadow-sm h-100 d-flex flex-column">
                            <img src="${img}" class="card-img-top" style="height:250px; object-fit:cover;">
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title">${title}</h5>
                                <p class="text-muted">${authors}</p>

                                <button class="btn btn-success mt-auto"
                                    onclick="addToLibrary('${bookId}', '${safeTitle}', '${safeAuthors}', '${img}', '${safeDescription}')">
                                    <i class="bi bi-plus-lg"></i> Add to Library
                                </button>

                            </div>
                        </div>
                    </div>
                `;
                });
            })
            .catch(err => {
                console.error('Error:', err);
                document.getElementById("results").innerHTML =
                    '<div class="alert alert-danger m-2">Error fetching data. Please try again.</div>';
            });
    }

    // This function is copied directly from your home page logic
    function addToLibrary(bookId, title, authors, thumbnail, description) {
        const book = {
            id: bookId,
            title: title,
            authors: authors,
            thumbnail: thumbnail,
            description: description
        };

        // Find the button that triggered the event to provide feedback
        const button = event.currentTarget;
        const originalHtml = button.innerHTML;
        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Adding...';


        fetch('/api/addBook', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(book)
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    button.classList.remove('btn-success');
                    button.classList.add('btn-secondary');
                    button.innerHTML = '<i class="bi bi-check-lg"></i> Added!';
                } else {
                    alert('Failed to add book: ' + data.message);
                    button.disabled = false;
                    button.innerHTML = originalHtml;
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert('Error adding book to library');
                button.disabled = false;
                button.innerHTML = originalHtml;
            });
    }
</script>
@endraw


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>