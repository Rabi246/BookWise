@params String username

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="/library">📚 BookWise</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="/library">My Library</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/recommend">Recommendations</a>
                </li>
            </ul>

            @if(username != null) {
            <span class="navbar-text text-white me-3">
          Welcome, ${username}
        </span>
            @endif
            <a class="btn btn-outline-light btn-sm" href="/logout">Logout</a>
            }
        </div>
    </div>
</nav>
