@import org.example.bookwise.Exchange
@import java.util.List
@param List<Exchange> history
@param String username

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>BookWise | Home</title>
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
                <span class="navbar-text text-white me-3">Welcome, ${username}</span>
            @endif
        </div>
    </div>
</nav>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

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
                    <form method="post" action="/ask" class="d-flex mt-auto" onsubmit="handleFormSubmit()">
                        <input type="text" name="prompt" id="promptInput" class="form-control me-2"
                               placeholder="Recommend me a book about..." required>
                        <button type="submit" class="btn btn-primary px-4">Send</button>
                    </form>

                </div>
            </div>
        </div>

        <!-- RIGHT PANEL: Book Recommendations -->
        <div class="col-md-6">
            <div class="card shadow-lg h-100">
                <div class="card-header bg-success text-white">
                    📚 Recommended Books
                </div>
                <div class="card-body d-flex flex-column">

                    <!-- Hidden search input (for auto-search functionality) -->
                    <input id="search" type="hidden">

                    <!-- Search Results Window -->
                    <div id="results" style="height:460px; overflow-y:auto; border:1px solid #ddd; padding:10px; background-color: #f8f9fa; border-radius:8px;">
                        <div class="text-muted text-center mt-5">
                            <i class="bi bi-chat-dots" style="font-size: 3rem;"></i>
                            <p class="mt-3">Ask the AI for a book recommendation to see results here!</p>
                        </div>
                    </div>

                </div>
            </div>
        </div>

    </div>
</div>

@raw
<script>
    // Extract book title from AI response
    function extractBookTitle(aiResponse) {
        // Try to match "Title" by Author pattern first
        const titleAuthorPattern = /"([^"]+)"\s+by\s+([^.,]+)/i;
        const match = aiResponse.match(titleAuthorPattern);

        if (match && match[1] && match[2]) {
            return `${match[1].trim()} ${match[2].trim()}`; // Return "Title Author"
        }

        // Fallback to original patterns
        const patterns = [
            /"([^"]+)"/i,
            /\*\*([^*]+)\*\*/,
            /'([^']+)'/i,
        ];

        for (let pattern of patterns) {
            const match = aiResponse.match(pattern);
            if (match && match[1]) {
                return match[1].trim();
            }
        }

        return null;
    }

    // Handle form submission - set flag to trigger auto-search after page reload
    function handleFormSubmit() {
        sessionStorage.setItem('triggerAutoSearch', 'true');
        return true; // Allow form to submit normally
    }

    // Auto-search when page loads after AI response
    window.addEventListener('DOMContentLoaded', function() {
        if (sessionStorage.getItem('triggerAutoSearch') === 'true') {
            sessionStorage.removeItem('triggerAutoSearch');

            // Get the last AI response
            const chatWindow = document.querySelector('.chat-window');
            if (!chatWindow) return;

            const aiMessages = chatWindow.querySelectorAll('.text-success');

            if (aiMessages.length > 0) {
                const lastAiMessageElement = aiMessages[aiMessages.length - 1].parentElement;
                const lastAiMessage = lastAiMessageElement.textContent.replace('AI:', '').trim();
                const bookTitle = extractBookTitle(lastAiMessage);

                if (bookTitle) {
                    // Auto-populate search box
                    document.getElementById('search').value = bookTitle;
                    // Auto-search
                    setTimeout(() => searchBook(), 300);
                }
            }
        }
    });

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
                    const truncatedDesc = description.length > 150
                        ? description.substring(0, 150) + "..."
                        : description;

                    const bookId = book.id;

                    results.innerHTML += `
                        <div class="d-flex mb-3 p-3 bg-white border rounded shadow-sm position-relative">
                            <img src="${thumbnail}" style="width:80px; height:120px; object-fit:cover; margin-right:15px; border-radius:4px;">
                            <div class="flex-grow-1">
                                <div class="fw-bold text-primary fs-6">${title}</div>
                                <div class="text-warning small mb-2">by ${authors}</div>
                                ${truncatedDesc ? `<p class="mb-0 small text-secondary lh-sm">${truncatedDesc}</p>` : ''}
                            </div>
                            <button class="btn btn-success btn-sm position-absolute top-0 end-0 m-2"
                                    onclick="addToLibrary('${bookId}', '${title.replace(/'/g, "\\'")}', '${authors.replace(/'/g, "\\'")}', '${thumbnail}', '${truncatedDesc.replace(/'/g, "\\'")}')">
                                <i class="bi bi-plus-lg"></i> Add
                            </button>
                        </div>
                    `;
                });

                // Scroll to top of results
                results.scrollTop = 0;
            })
            .catch(err => {
                console.error('Error:', err);
                document.getElementById("results").innerHTML =
                    '<div class="alert alert-danger m-2">Error fetching data. Please try again.</div>';
            });
    }

    // Add book to library
    function addToLibrary(bookId, title, authors, thumbnail, description) {
        const book = {
            id: bookId,
            title: title,
            authors: authors,
            thumbnail: thumbnail,
            description: description
        };

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
                    // Show success message
                    alert('✓ Book added to your library!');
                } else {
                    alert('Failed to add book: ' + data.message);
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert('Error adding book to library');
            });
    }
</script>
@endraw

</body>
</html>