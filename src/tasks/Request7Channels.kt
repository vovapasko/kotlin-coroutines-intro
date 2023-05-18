package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    coroutineScope {
        val repos = service
            .getOrgRepos(req.org)
            .also { logRepos(req, it) }
            .bodyList()
        val channel = Channel<List<User>>()
        for (repo in repos) {
            launch {
                log("starting loading for ${repo.name}")
                val users = service.getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList()
                channel.send(users)
            }
        }
        val currentUsers = mutableListOf<User>()
        repeat(repos.size){
            val users  = channel.receive()
            currentUsers += users
            updateResults(currentUsers.aggregate(), false)
        }
        updateResults(currentUsers.aggregate(), true)
    }
}
