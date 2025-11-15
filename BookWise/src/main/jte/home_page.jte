@param String username

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>BookWise | Ask the Librarian</title>
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
    <div class="row g-4">

        <!-- LEFT: Chat -->
        <div class="col-md-6">
            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="bi bi-chat-dots"></i> Conversation</h5>
                </div>
                <div class="card-body p-0">
                    <div class="chat-messages" id="chatMessages">
                        <div class="message ai-message">
                            <strong class="text-success">📚 Librarian:</strong>
                            <p class="mb-0 mt-2">Hello! What are you feeling like reading today?</p>
                        </div>
                    </div>
                    <div class="p-3">
                        <div class="input-group">
                            <input type="text" id="chatInput" class="form-control"
                                   placeholder="What are you feeling like reading?"
                                   onkeypress="if(event.key==='Enter') sendMessage()">
                            <button class="btn btn-primary" onclick="sendMessage()">
                                <i class="bi bi-send-fill"></i> Send
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- RIGHT: Books -->
        <div class="col-md-6">
            <div class="card shadow-lg">
                <div class="card-header bg-success text-white">
                    <h5 class="mb-0"><i class="bi bi-book-half"></i> Recommended Books</h5>
                </div>
                <div class="card-body p-0">
                    <div class="results-panel" id="results">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-books" style="font-size: 4rem; opacity: 0.3;"></i>
                            <p class="mt-3">Start chatting to get recommendations!</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>


@raw
<script>
    function sendMessage() {
        const input = document.getElementById('chatInput');
        const message = input.value.trim();

        if (!message) return;

        addMessage('user', message);
        input.value = '';

        addMessage('ai', '<em class="text-muted">Thinking...</em>', 'loading');

        document.getElementById('results').innerHTML = `
            <div class="text-center py-5">
                <div class="spinner-border text-success"></div>
                <p class="mt-3">Finding books...</p>
            </div>
        `;

        fetch('/api/ai/recommendations', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `query=${encodeURIComponent(message)}&count=5`
        })
            .then(res => res.json())
            .then(data => {
                document.querySelector('.loading')?.remove();

                if (data.aiResponse) {
                    const formatted = data.aiResponse
                        .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
                        .replace(/\n/g, '<br>');
                    addMessage('ai', formatted);
                }

                if (data.items && data.items.length > 0) {
                    displayBooks(data.items);
                } else {
                    document.getElementById('results').innerHTML = `
                    <div class="alert alert-warning m-3">No books found!</div>
                `;
                }
            })
            .catch(err => {
                document.querySelector('.loading')?.remove();
                addMessage('ai', 'Sorry, something went wrong!');
                console.error(err);
            });
    }

    function addMessage(sender, text, className = '') {
        const chat = document.getElementById('chatMessages');
        const div = document.createElement('div');
        div.className = `message ${sender === 'user' ? 'user-message' : 'ai-message'} ${className}`;

        if (sender === 'user') {
            div.innerHTML = `<strong class="text-primary">You:</strong><p class="mb-0 mt-2">${text}</p>`;
        } else {
            div.innerHTML = `<strong class="text-success">📚 Librarian:</strong><div class="mt-2">${text}</div>`;
        }

        chat.appendChild(div);
        chat.scrollTop = chat.scrollHeight;
    }

    function displayBooks(books) {
        const results = document.getElementById('results');
        results.innerHTML = '';

        books.forEach(book => {
            const info = book.volumeInfo;
            const title = info.title || "No Title";
            const authors = info.authors ? info.authors.join(", ") : "Unknown";
            const thumbnail = info.imageLinks?.thumbnail?.replace('http:', 'https:') ||
                "https://via.placeholder.com/128x190?text=No+Cover";
            const desc = info.description || "";
            const shortDesc = desc.length > 150 ? desc.substring(0, 150) + "..." : desc;

            results.innerHTML += `
                <div class="card mb-3">
                    <div class="row g-0">
                        <div class="col-3">
                            <img src="${thumbnail}" class="img-fluid rounded-start" style="height:100%; object-fit:cover;">
                        </div>
                        <div class="col-7 p-3">
                            <h6 class="text-primary mb-1">${title}</h6>
                            <p class="text-warning small mb-2">by ${authors}</p>
                            <p class="small text-secondary mb-0">${shortDesc}</p>
                        </div>
                        <div class="col-2 d-flex align-items-center justify-content-center">
                            <button class="btn btn-success" onclick="addToLibrary('${book.id}', \`${title}\`, \`${authors}\`, '${thumbnail}', \`${desc}\`)">
                                <i class="bi bi-plus-circle"></i><br><small>Add</small>
                            </button>
                        </div>
                    </div>
                </div>
            `;
        });
    }

    function addToLibrary(id, title, authors, thumb, desc) {
        fetch('/api/addBook', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id: id,
                title: title,
                authors: authors,
                thumbnail: thumb,
                description: desc
            })
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    event.target.innerHTML = '<i class="bi bi-check"></i><br><small>Added!</small>';
                    event.target.classList.replace('btn-success', 'btn-secondary');
                    event.target.disabled = true;
                }
            })
            .catch(err => console.error(err));
    }
</script>
@endraw
</body>
</html>