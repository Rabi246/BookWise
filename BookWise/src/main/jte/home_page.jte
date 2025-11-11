@param String joke
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
<!-- going to write Please tell a joke about and going to have a dropdown menu with the following options: politics, math sports and a button go -->
<form method="post">
    <label for="category">Please tell a joke about:</label>
    <select name="category" id="category">
        <option value="politics">Politics</option>
        <option value="math">Math</option>
        <option value="sports">Sports</option>
    </select>
    <button type="submit">Go</button><br>
    <!-- html -->
    <!-- Simple link (works if controller accepts GET) -->
    <a href="/logout" class="btn">Log out</a>



</form>
</body>
</html>