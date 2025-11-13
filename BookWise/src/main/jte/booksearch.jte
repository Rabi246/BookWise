
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Book Search - Google Books API</title>

    <style>
        body { font-family: Arial; padding: 20px; }
        input { padding: 8px; width: 300px; }
        button { padding: 8px 12px; }
        .book {
            border: 1px solid #ccc;
            padding: 10px;
            margin-top: 10px;
            display: flex;
            gap: 10px;
            align-items: center;
        }
        img { height: 120px; }
    </style>
</head>
<body>

<h2>Google Books Test Page</h2>

<input id="search" type="text" placeholder="Search for a book...">
<button onclick="searchBook()">Search</button>

<div id="results"></div>

@raw
<script>
    function searchBook() {
        const q = document.getElementById("search").value.trim();
        const cleanQ = q.replace(/[']/g, ""); // remove apostrophes

        fetch(`/api/searchBooks?q=${encodeURIComponent(cleanQ)}`)
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
                    const thumbnail = info.imageLinks && info.imageLinks.thumbnail
                        ? info.imageLinks.thumbnail
                        : "";

                    results.innerHTML += `
<div class="book">
  <img src="${thumbnail}" alt="cover" />
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
