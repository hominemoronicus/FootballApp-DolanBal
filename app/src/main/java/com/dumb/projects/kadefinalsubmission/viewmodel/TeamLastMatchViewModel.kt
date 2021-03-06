package com.dumb.projects.kadefinalsubmission.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.dumb.projects.kadefinalsubmission.api.APIRepository
import com.dumb.projects.kadefinalsubmission.model.Match
import com.dumb.projects.kadefinalsubmission.util.CoroutineContextProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TeamLastMatchViewModel(
    application: Application,
    private val idTeam: String,
    private val repo: APIRepository,
    private val context: CoroutineContextProvider = CoroutineContextProvider()
) : AndroidViewModel(application) {
    private val _lastMatch = MutableLiveData<List<Match>>()
    val lastMatch: LiveData<List<Match>>
        get() = _lastMatch

    private val _navigatedItem = MutableLiveData<Match>()
    val navigatedItem: LiveData<Match>
        get() = _navigatedItem

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int>
        get() = _status

    fun getLastMatch() {
        GlobalScope.launch(context.main) {
            try {
                _status.value = View.VISIBLE
                val lastMatch = repo.getLastMatch(idTeam).results
                lastMatch.forEach {
                    it.homeTeamBadge = repo.getTeamDetail(it.idHomeTeam).teams[0].teamBadge
                    it.awayTeamBadge = repo.getTeamDetail(it.idAwayTeam).teams[0].teamBadge
                }
                _status.value = View.GONE
                _lastMatch.value = lastMatch
            } catch (t: Throwable) {
                _status.value = View.GONE
                _lastMatch.value = listOf()
            }
        }
    }

    fun navigateItem(item: Match) {
        _navigatedItem.value = item
    }

    fun doneNavigateItem() {
        _navigatedItem.value = null
    }
}

class TeamLastMatchViewModelFactory(
    private val application: Application,
    private val idTeam: String,
    private val repo: APIRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamLastMatchViewModel::class.java)) {
            return TeamLastMatchViewModel(application, idTeam, repo) as T
        }
        throw IllegalArgumentException("ViewModel class not found !")
    }
}