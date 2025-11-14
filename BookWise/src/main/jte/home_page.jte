@import org.example.bookwise.Exchange
@import java.util.List
@param List<Exchange> history

    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>BookWise | Home</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="/home_page">📚 BookWise</a>
        </div>
    </nav>

    <div class="container-fluid px-4">
        <div class="row g-4">

            <!-- LEFT PANEL: AI Chatbot -->
            <div class="col-md-6">
                <div class="card shadow-lg h-100">
                    <div class="card-header bg-primary text-white">
                        🤖 AI Book Assistant
                    </div>
                    <div class="card-body d-flex flex-column">

                        <!-- Chat History Window -->
                        <div class="chat-window mb-3" style="height:400px; overflow-y:auto; border:1px solid #ddd; padding:15px; background-color: #f8f9fa; border-radius:8px;">
                            @if(history != null && !history.isEmpty())
                                @for(var exchange : history)
                                    <div class="mb-2">
                                        <strong class="text-primary">You:</strong>
                                        <span>${exchange.getUserMessage()}</span>
                                    </div>
                                    <div class="mb-3 p-2 bg-white rounded">
                                        <strong class="text-success">AI:</strong>
                                        <span>${exchange.getAiMessage()}</span>
                                    </div>
                                @endfor
                            @else
                                <div class="text-muted">👋 Hi! Ask me for book recommendations!</div>
                            @endif
                        </div>

                        <!-- Chat Input Form -->
                        <form method="post" action="/ask" class="d-flex mt-auto">
                            <input type="text" name="prompt" class="form-control me-2"
                                   placeholder="Recommend me a book about..." required>
                            <button type="submit" class="btn btn-primary px-4">Send</button>
                        </form>

                    </div>
                </div>
            </div>

            <!-- RIGHT PANEL: Google Books Search -->
            <div class="col-md-6">
                <div class="card shadow-lg h-100">
                    <div class="card-header bg-success text-white">
                        🔍 Search Google Books
                    </div>
                    <div class="card-body d-flex flex-column">

                        <!-- Search Input -->
                        <div class="d-flex mb-3">
                            <input id="search" type="text" class="form-control me-2"
                                   placeholder="Search for books..."
                                   onkeypress="if(event.key==='Enter') searchBook()">
                            <button class="btn btn-success px-4" onclick="searchBook()">Search</button>
                        </div>

                        <!-- Search Results Window -->
                        <div id="results" style="height:400px; overflow-y:auto; border:1px solid #ddd; padding:10px; background-color: #f8f9fa; border-radius:8px;">
                            <div class="text-muted text-center mt-5">
                                Search for books to see results here
                            </div>
                        </div>

                    </div>
                </div>
            </div>

        </div>
    </div>

    @raw
    <script>
        function searchBook() {
            const q = document.getElementById("search").value.trim();

            if (!q) {
                document.getElementById("results").innerHTML =
                    '<div class="alert alert-warning m-2">Please enter a search term</div>';
                return;
            }

            // Show loading spinner
            document.getElementById("results").innerHTML =
                '<div class="text-center mt-5"><div class="spinner-border text-success" role="status"></div><p class="mt-2">Searching...</p></div>';

            fetch(`/api/searchBooks?q=${encodeURIComponent(q)}`)
                .then(res => res.json())
                .then(data => {
                    const results = document.getElementById("results");
                    results.innerHTML = "";

                    if (!data.items || data.items.length === 0) {
                        results.innerHTML = '<div class="alert alert-info m-2">No results found</div>';
                        return;
                    }

                    data.items.forEach(book => {
                        const info = book.volumeInfo;
                        const title = info.title || "No Title";
                        const authors = info.authors ? info.authors.join(", ") : "Unknown Author";
                        const thumbnail = info.imageLinks && info.imageLinks.thumbnail
                            ? info.imageLinks.thumbnail.replace('http:', 'https:')
                            : "https://via.placeholder.com/80x120?text=No+Cover";
                        const description = info.description || "";
                        const truncatedDesc = description.length > 120
                            ? description.substring(0, 120) + "..."
                            : description;

                        results.innerHTML += `
                        <div class="d-flex mb-3 p-2 bg-white border rounded">
                            <img src="${thumbnail}" style="width:80px; height:120px; object-fit:cover; margin-right:12px; border-radius:4px;">
                            <div class="flex-grow-1">
                                <div class="fw-bold text-primary">${title}</div>
                                <div class="text-muted small fst-italic mb-1">${authors}</div>
                                ${truncatedDesc ? `<p class="mb-0 small text-secondary">${truncatedDesc}</p>` : ''}
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
    </script>
    @endraw

    </body>
    </html>