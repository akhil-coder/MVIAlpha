package com.example.mvialpha.repository.auth

import AccountPropertiesDao
import AuthTokenDao
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mvialpha.models.AuthToken
import com.example.mvialpha.api.auth.OpenApiAuthService
import com.example.mvialpha.api.auth.networkresponses.LoginResponse
import com.example.mvialpha.api.auth.networkresponses.RegistrationResponse
import com.example.mvialpha.models.AccountProperties

import com.example.mvialpha.repository.NetworkBoundResource
import com.example.mvialpha.session.SessionManager
import com.example.mvialpha.ui.DataState
import com.example.mvialpha.ui.Response
import com.example.mvialpha.ui.ResponseType
import com.example.mvialpha.ui.auth.state.AuthViewState
import com.example.mvialpha.ui.auth.state.LoginFields
import com.example.mvialpha.ui.auth.state.RegistrationFields
import com.example.mvialpha.util.AbsentLiveData
import com.example.mvialpha.util.ApiSuccessResponse
import com.example.mvialpha.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.example.mvialpha.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.mvialpha.util.GenericApiResponse
import com.example.mvialpha.util.PreferenceKeys
import com.example.mvialpha.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefEditor: SharedPreferences.Editor
) {
    private val TAG = "AuthRepository"
    private var repositoryJob: Job? = null

    @InternalCoroutinesApi
    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>>{
        val loginFieldsErrors = LoginFields(email, password).isValidForLogin()
        if(loginFieldsErrors != LoginFields.LoginError.none()){
            return returnErrorResponse(loginFieldsErrors, ResponseType.Dialog())
         }
        return object: NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )
                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )
                if(result < 0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }
            /*    saveAuthenticatedUserToPrefs(email)*/
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            // Not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<AuthViewState>> {
        return object: LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }

    fun cancelActiveJobs(){
        Log.d(TAG, "cancelActiveJobs:  Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }


    @InternalCoroutinesApi
    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldsErrors = RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if(!registrationFieldsErrors.equals(RegistrationFields.RegistrationError.none())){
            return returnErrorResponse(registrationFieldsErrors, ResponseType.Dialog())
        }
        return object: NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true
        ){
            // Not used in this case
            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )
                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )
                if(result < 0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }
                 saveAuthenticatedUserToPrefs(email)
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                TODO("Not yet implemented")
            }

            override fun setJob(job: Job) {
                TODO("Not yet implemented")
            }

        }.asLiveData()
    }
    @InternalCoroutinesApi
    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>>{
        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if(previousAuthUserEmail.isNullOrBlank()){
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found...")
            return returnNoTokenFound()
        }
        return object: NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false
        ){
            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(TAG, "createCacheRequestAndReturn: Searching for token: $accountProperties")

                    accountProperties?.let {
                        if(accountProperties.pk > -1){
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                if(authToken != null){
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
                    Log.d(TAG, "createCacheRequestAndReturn: AuthToken Not found...")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }
            }

            // Not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
                TODO("Not yet implemented")
            }

            // Not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object: LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None())
                )
            }
        }
    }

    private fun saveAuthenticatedUserToPrefs(email: String) {
        sharedPrefEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefEditor.apply()
    }
}