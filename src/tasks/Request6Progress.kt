package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()
    val currentUsers = mutableListOf<User>()
    val users = repos.flatMap { repo ->
        service
            .getRepoContributors(req.org, repo.name)
            .also {
                logUsers(repo, it)
            }
            .bodyList().also {
                currentUsers += it
                updateResults(currentUsers.aggregate(), false)
            }
    }
    updateResults(users, true)
}
