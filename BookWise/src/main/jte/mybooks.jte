@param String username
@param java.util.List<org.example.bookwise.model.Library> entries
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Books | BookWise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/style.css">
    <link rel="stylesheet" href="/css/mybooks.css"><!-- scoped styles for this page -->
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
                <li class="nav-item"><a class="nav-link" href="/home_page">Recommendations</a></li>
                <li class="nav-item"><a class="nav-link" href="/booksearch">Search</a></li>
            </ul>

            @if(username != null)
                <div class="d-flex align-items-center">
                    <div class="dropdown">
                        <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle" style="font-size: 2rem"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li><span class="dropdown-item-text fw-bold">${username}</span></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="/logout">Logout</a></li>
                        </ul>
                    </div>
                </div>
            @endif
        </div>
    </div>
</nav>

<div class="container-fluid px-4 page-content">
    <div class="text-center mb-4">
        <h2 class="fw-bold">📘 My Library</h2>
        <p class="text-muted">Your saved books collection</p>
    </div>

    @if(entries != null && !entries.isEmpty())

        <div class="mylibrary-page">
            <div id="library-container">
                <!-- LEFT: covers grid -->
                <div id="library-left">
                    <h2>My Library</h2>
                    <div class="book-grid">
                        @for(var entry : entries)
                            <div
                                    class="book-card"
                                    onclick="showDetails(this)"
                                    data-id="${entry.getBook().getId()}"
                                    data-name="${entry.getBook().getTitle()}"
                                    data-author="${entry.getBook().getAuthors()}"
                                    data-summary="${entry.getBook().getDescription() == null ? "" : entry.getBook().getDescription()}"
                                    data-picture="${entry.getBook().getThumbnail()}">

                                <img src="${entry.getBook().getThumbnail()}" class="book-cover" alt="Cover">
                            </div>
                        @endfor
                    </div>
                </div>

                <!-- RIGHT: details panel -->
                <div id="details-panel" class="hidden">
                    <button onclick="hideDetails()" class="close-btn" aria-label="Close">✖</button>
                    <div id="details-content">Select a book...</div>
                </div>
            </div>
        </div>
    @else
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

    function highlightStars(value) {
        document.querySelectorAll('.rating .star').forEach(function(star) {
            if (Number(star.dataset.value) <= Number(value)) {
                star.setAttribute('data-selected', '1');
            } else {
                star.removeAttribute('data-selected');
            }
        });
    }

    function showDetails(el) {
        var data = el.dataset;
        var panel = document.getElementById('details-panel');
        var content = document.getElementById('details-content');
        panel.classList.remove('hidden');

        var name = data.name || 'Untitled';
        var author = data.author || 'Unknown';
        var summary = data.summary || 'No description';
        var picture = data.picture || '';
        var id = data.id || '';

        var html = ''
            + '<img src="' + picture + '" style="width:250px;height:350px;object-fit:cover;border-radius:8px;"><br><br>'
            + '<h2>' + name + '</h2>'
            + '<p><strong>Author:</strong> ' + author + '</p>'
            + '<p><strong>Summary:</strong> ' + summary + '</p>'
            + '<div class="rating" data-book="' + id + '">'
            + '  <div style="margin-top:8px;margin-bottom:4px;">'
            + '    <strong>Your rating:</strong> '
            + '    <span class="stars" aria-label="Rate this book">'
            + '      <button type="button" class="star" data-value="1">★</button>'
            + '      <button type="button" class="star" data-value="2">★</button>'
            + '      <button type="button" class="star" data-value="3">★</button>'
            + '      <button type="button" class="star" data-value="4">★</button>'
            + '      <button type="button" class="star" data-value="5">★</button>'
            + '    </span>'
            + '  </div>'
            + '</div>'
            + '<button onclick="removeBook(\'' + id + '\')" class="btn btn-danger mt-3">Remove From My Library</button>';

        content.innerHTML = html;

        wireRatingHandlers();

        // Load rating from DB
        fetch('/api/getRating?bookId=' + encodeURIComponent(id))
            .then(r => r.json())
            .then(data => {
                let rating = data.rating || 0;
                highlightStars(rating);
            });
    }

    function wireRatingHandlers() {
        document.querySelectorAll('.rating .star').forEach(function(btn) {
            btn.addEventListener('click', function() {
                var value = btn.dataset.value;
                var wrap = btn.closest('.rating');
                var bookId = wrap.dataset.book;

                // update UI instantly
                highlightStars(value);

                // save to DB
                fetch('/api/rateBook?bookId=' + encodeURIComponent(bookId) + '&value=' + value, {
                    method: 'POST'
                }).then(r => r.json())
                    .then(data => {
                        if (!data.success) {
                            alert("Error saving rating");
                        }
                    });
            });
        });
    }

    function hideDetails() {
        document.getElementById('details-panel').classList.add('hidden');
    }

    function removeBook(bookId) {
        if (!confirm('Remove this book from your library?')) return;

        fetch('/api/removeBook?bookId=' + encodeURIComponent(bookId), { method: 'POST' })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data && data.success) window.location.reload();
                else alert('Failed to remove book' + (data && data.message ? (': ' + data.message) : ''));
            })
            .catch(function() { alert('Error removing book from library'); });
    }

</script>
@endraw


<div class="auth-footer"><i class="bi bi-bookmark-fill"></i> BookWise.io © 2025 AI Book Recommendations.</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>