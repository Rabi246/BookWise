@import org.example.bookwise.Exchange
@import java.util.List
@param List<Exchange> history

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>AI Chatbot</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
</head>
<body class="container">
<form method="post">
    <div class="row g-4 m-2">
        @if(history.isEmpty())
            <div class="col-12 text-bg-light p-3 rounded-3">(Start chatting ...)</div>
        @endif
        @for(var exchange : history)
            <div class="col-8 text-bg-light p-3 rounded-3">
                ${exchange.getUserMessage()}
            </div>
            <div class="col-8 offset-4 text-bg-info p-3 rounded-3">
                ${exchange.getAiMessage()}
            </div>
        @endfor
        <div class="col-10 col-lg-7">
            <input name="message" type="text" class="form-control">
        </div>
        <div class="col-2 col-lg-1">
            <button type="submit" class="btn btn-primary">Send</button>
        </div>
    </div>
</form>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI"
        crossorigin="anonymous"></script>
</body>
</html>

</form>
</body>
</html>