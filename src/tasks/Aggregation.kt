package tasks

import contributors.User

/*
 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.
*/
fun List<User>.aggregate(): List<User> {
    val userMap = HashMap<String, Int>()
    val newList = ArrayList<User>()
    this.forEach { user ->
        if (userMap.contains(user.login)) {
            var oldValue = userMap[user.login]!!
            oldValue += user.contributions
            userMap[user.login] = oldValue
        }
        else{
            userMap[user.login] = user.contributions
        }
    }
    userMap.forEach { element ->
        newList.add(User(element.key, element.value))
    }
    return newList
}