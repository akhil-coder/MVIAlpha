package com.example.mvialpha.ui.main.account

import androidx.lifecycle.LiveData
import com.example.mvialpha.models.AccountProperties
import com.example.mvialpha.repository.main.AccountRepository
import com.example.mvialpha.session.SessionManager
import com.example.mvialpha.ui.DataState
import com.example.mvialpha.ui.main.account.state.AccountStateEvent
import com.example.mvialpha.ui.main.account.state.AccountViewState
import com.example.mvialpha.util.AbsentLiveData
import com.example.mvialpha.util.BaseViewModel
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {
            is AccountStateEvent.GetAccountPropertiesEvent -> {
                return AbsentLiveData.create()
            }

            is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                return AbsentLiveData.create()
            }

            is AccountStateEvent.ChangePasswordEvent -> {
                return AbsentLiveData.create()
            }

            is AccountStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }

    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties){
        val update = getCurrentViewStateOrNew()
        if(update.accountProperties == accountProperties){
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout(){
        sessionManager.logout()
    }
}