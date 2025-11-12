@import org.example.bookwise.Exchange
@import java.util.List
@param List<Exchange> history

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>BookWise | Recommendations</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="/home_page">📚 BookWise</a>
    </div>
</nav>

<div class="container">
    <div class="row g-4">

        <!-- LEFT: Chatbot -->
        <div class="col-md-6">
            <div class="card shadow-lg h-100">
                <div class="card-header bg-primary text-white">
                    🤖 AI Book Assistant
                </div>
                <div class="card-body d-flex flex-column">

                    <div class="chat-window mb-3" style="height:350px; overflow-y:auto; border:1px solid #ddd; padding:10px;">
                        @if(history != null && !history.isEmpty()) 
                            @for(var exchange : history)
                                <div class="mb-2"><strong>You:</strong> ${exchange.getUserMessage()}</div>
                                <div class="mb-3 text-success"><strong>AI:</strong> ${exchange.getAiMessage()}</div>
                            @endfor
                        @else
                            <div>👋 Hi! Ask me for book recommendations!</div>
                        @endif
                    </div>

                    <form method="post" action="/ask" class="d-flex mt-auto">
                        <input type="text" name="prompt" class="form-control me-2"
                               placeholder="Recommend me a book about..." required>
                        <button class="btn btn-primary">Send</button>
                    </form>


                </div>
            </div>
        </div>

        <!-- RIGHT: Google Books Search -->
        <div class="col-md-6">
            <div class="card shadow-lg h-100">
                <div class="card-header bg-success text-white">
                    🔍 Search Google Books
                </div>
                <div class="card-body">

                    <div class="d-flex mb-2">
                        <input id="search" type="text" class="form-control me-2" placeholder="Search books...">
                        <button class="btn btn-success" onclick="searchBook()">Search</button>
                    </div>

                    <div id="results" style="height:350px; overflow-y:auto;"></div>

                </div>
            </div>
        </div>

    </div>
</div>

@raw
<script>
    function searchBook() {
        const q = document.getElementById("search").value;

        fetch(`/api/searchBooks?q=${encodeURIComponent(q)}`)
            .then(res => res.json())
            .then(data => {
                const results = document.getElementById("results");
                results.innerHTML = "";

                if (!data.items) {
                    results.innerHTML = "<p>No results found</p>";
                    return;
                }

                data.items.forEach(book => {
                    const info = book.volumeInfo;
                    const title = info.title || "No Title";
                    const authors = info.authors ? info.authors.join(", ") : "Unknown Author";
                    const thumbnail = info.imageLinks && info.imageLinks.thumbnail ? info.imageLinks.thumbnail : "";

                    results.innerHTML += `
                    <div class="d-flex mb-3 border p-2 rounded">
                        <img src="${thumbnail}" style="height:100px; margin-right:10px;">
                        <div>
                            <strong>${title}</strong><br>
                            <em>${authors}</em>
                        </div>
                    </div>
                `;
                });
            });
    }
</script>
@endraw
</body>
</html>