@import org.example.bookwise.model.Book
@import java.util.List
@param String username
@param List<Book> books

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Books | BookWise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
</head>

<body class="bg-light">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="/home_page">📚 BookWise</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link active" href="/mybooks">My Library</a></li>
                <li class="nav-item"><a class="nav-link " href="/home_page">Recommendations</a></li>
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

<div class="container">

    <div class="text-center mb-4">
        <h2 class="fw-bold">📘 My Library</h2>
        <p class="text-muted">Your saved books collection</p>
    </div>

    @if(books != null && !books.isEmpty())
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            @for(var book : books)
                <div class="col">
                    <div class="card h-100 shadow-sm">
                        <div class="row g-0">
                            <div class="col-4">
                                <img src="${book.getThumbnail()}"
                                     class="img-fluid rounded-start h-100"
                                     style="object-fit: cover; max-height: 200px;"
                                     alt="Book cover">
                            </div>
                            <div class="col-8">
                                <div class="card-body d-flex flex-column h-100">
                                    <h6 class="card-title text-primary fw-bold">${book.getTitle()}</h6>
                                    <p class="card-text text-warning small mb-2">by ${book.getAuthors()}</p>
                                    @if(book.getDescription() != null && !book.getDescription().isEmpty())
                                        !{var desc = book.getDescription();}
                                        !{var truncated = desc.length() > 100 ? desc.substring(0, 100) + "..." : desc;}
                                        <p class="card-text small text-secondary flex-grow-1">${truncated}</p>
                                    @endif
                                    <button class="btn btn-danger btn-sm mt-auto"
                                            onclick="removeBook('${book.getId()}')">
                                        <i class="bi bi-trash"></i> Remove
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            @endfor
        </div>
    @else
        <!-- Empty State -->
        <div class="card shadow-sm mt-4">
            <div class="card-body text-center py-5">
                <i class="bi bi-book text-secondary" style="font-size: 4rem;"></i>
                <h5 class="text-secondary mt-3">Your library is empty</h5>
                <p class="text-muted">Start adding books from recommendations or search!</p>
                <a href="/home_page" class="btn btn-primary mt-3">
                    <i class="bi bi-search"></i> Browse Books
                </a>
            </div>
        </div>
    @endif

</div>

@raw
<script>
    function removeBook(bookId) {
        if (!confirm('Remove this book from your library?')) {
            return;
        }

        fetch(`/api/removeBook?bookId=${encodeURIComponent(bookId)}`, {
            method: 'POST'
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    // Reload page to show updated library
                    window.location.reload();
                } else {
                    alert('Failed to remove book: ' + data.message);
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert('Error removing book from library');
            });
    }
</script>
@endraw

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>