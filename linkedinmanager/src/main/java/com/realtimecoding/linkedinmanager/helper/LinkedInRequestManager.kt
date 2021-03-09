package com.realtimecoding.linkedinmanager.helper


import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.google.gson.Gson
import com.realtimecoding.linkedinmanager.R
import com.realtimecoding.linkedinmanager.util.*
import com.realtimecoding.linkedinmanager.models.*
import com.realtimecoding.linkedinmanager.events.*
import org.json.JSONObject


class LinkedInRequestManager(private val activity: Activity, linkedInManagerResponse: LinkedInManagerResponse, clientID: String, clientSecret: String, redirectionURL: String, allowCancelDialogPrompt: Boolean) {

    private var dialog: Dialog? = null
    private val linkedInManagerResponse: LinkedInManagerResponse
    private val clientID: String
    private val clientSecret: String
    private val redirectionURL: String
    private val linkedInUserProfile: LinkedInUserProfile = LinkedInUserProfile()
    private val linkedInEmailAddress: LinkedInEmailAddress = LinkedInEmailAddress()
    private val linkedInUser: LinkedInUser = LinkedInUser()
    private var mode = 0
    private val allowCancelDialogPrompt: Boolean
    private var progressBar: ProgressBar? = null

    init {
        this.linkedInManagerResponse = linkedInManagerResponse
        this.clientID = clientID
        this.clientSecret = clientSecret
        this.redirectionURL = redirectionURL
        this.allowCancelDialogPrompt = allowCancelDialogPrompt
    }

    companion object {
        const val MODE_EMAIL_ADDRESS_ONLY = 0
        const val MODE_LITE_PROFILE_ONLY = 1
        const val MODE_BOTH_OPTIONS = 2
        private const val SCOPE_LITE_PROFILE = "r_liteprofile"
        private const val SCOPE_EMAIL_ADDRESS = "r_emailaddress"
        private const val CODE = "code"
        private const val ERROR = "error"
    }

    fun dismissAuthenticateView() {
        if(dialog != null){
            dialog!!.dismiss()
        }
    }

    fun showAuthenticateView(mode: Int) {
        this.mode = mode
        var strlinkedInLoggedTokenBean = PreferenceManager.getInstance(activity.applicationContext).getString(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN, "")
        if(!TextUtils.isEmpty(strlinkedInLoggedTokenBean)){
            val linkedInLoggedTokenBean: LinkedInLoggedTokenBean = Gson().fromJson(strlinkedInLoggedTokenBean,LinkedInLoggedTokenBean::class.java)
            if (linkedInLoggedTokenBean.loggedIn && linkedInLoggedTokenBean.requestMode == mode) {
                isValidToken(linkedInLoggedTokenBean.linkedInAccessToken, mode)
            } else {
                if (PreferenceManager.getInstance(activity.applicationContext).removePreferenceKey(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN)) {
                    startAuthenticate(mode)
                }
            }
        }else{
            if (PreferenceManager.getInstance(activity.applicationContext).removePreferenceKey(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN)) {
                startAuthenticate(mode)
            }
        }
    }

    fun logout(): Boolean {
        return PreferenceManager.getInstance(activity.applicationContext).removePreferenceKey(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN)
    }

    fun isLoggedIn(linkedInUserLoginValidationResponse: LinkedInUserLoginValidationResponse) {
        var strlinkedInLoggedTokenBean = PreferenceManager.getInstance(activity.applicationContext).getString(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN, "")
        if(!TextUtils.isEmpty(strlinkedInLoggedTokenBean)){
            val linkedInLoggedTokenBean: LinkedInLoggedTokenBean = Gson().fromJson(strlinkedInLoggedTokenBean,LinkedInLoggedTokenBean::class.java)
            if (linkedInLoggedTokenBean.loggedIn && linkedInLoggedTokenBean.requestMode == mode) {
                LinkedInControl().checkAccessTokenValidity(URLManager.URL_FOR_ACCESS_TOKEN_VALIDATION, "client_id=" + clientID + "&client_secret=" + clientSecret + "&token=" + linkedInLoggedTokenBean.linkedInAccessToken, object : LinkedInAccessTokenValidationResponse {
                    override fun onValidationSuccess() {
                        linkedInUserLoginValidationResponse.activeLogin()
                    }

                    override fun onValidationFailed() {
                        linkedInUserLoginValidationResponse.tokenExpired()
                    }
                })
            } else {
                logout()
                linkedInUserLoginValidationResponse.notLogged()
            }
        }else{
            logout()
            linkedInUserLoginValidationResponse.notLogged()
        }
    }

    fun getLoggedRequestedMode(linkedInUserLoginDetailsResponse: LinkedInUserLoginDetailsResponse) {
        try {
            val linkedInLoggedTokenBean: LinkedInLoggedTokenBean = Gson().fromJson(PreferenceManager.getInstance(activity.applicationContext).getString(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN, ""),LinkedInLoggedTokenBean::class.java)
            if (linkedInLoggedTokenBean.loggedIn) {
                LinkedInControl().checkAccessTokenValidity(URLManager.URL_FOR_ACCESS_TOKEN_VALIDATION, "client_id=" + clientID + "&client_secret=" + clientSecret + "&token=" + linkedInLoggedTokenBean.linkedInAccessToken, object : LinkedInAccessTokenValidationResponse {
                    override fun onValidationSuccess() {
                        linkedInUserLoginDetailsResponse.loggedMode(linkedInLoggedTokenBean.requestMode)
                    }

                    override fun onValidationFailed() {
                        linkedInUserLoginDetailsResponse.tokenExpired()
                    }
                })
            } else {
                logout()
                linkedInUserLoginDetailsResponse.notLogged()
            }
        } catch (e: Exception) {
            ExceptionManager.exceptionLog(e)
            logout()
            linkedInUserLoginDetailsResponse.notLogged()
        }
    }

    private fun isValidToken(accessToken: String?, mode: Int) {
        LinkedInControl().checkAccessTokenValidity(URLManager.URL_FOR_ACCESS_TOKEN_VALIDATION, "client_id=$clientID&client_secret=$clientSecret&token=$accessToken", object : LinkedInAccessTokenValidationResponse {
            override fun onValidationSuccess() {
                val linkedInAccessToken = LinkedInAccessToken()
                linkedInAccessToken.access_token = accessToken
                getRelevantData(linkedInAccessToken)
            }

            override fun onValidationFailed() {
                startAuthenticate(mode)
            }
        })
    }

    private fun startAuthenticate(mode: Int) {
        var url = URLManager.URL_FOR_AUTHORIZATION_CODE + "?response_type=code&client_id=" + clientID + "&redirect_uri=" + redirectionURL + "&state=aRandomString&scope="
        url += when (mode) {
            MODE_LITE_PROFILE_ONLY -> SCOPE_LITE_PROFILE
            MODE_BOTH_OPTIONS -> "$SCOPE_LITE_PROFILE,$SCOPE_EMAIL_ADDRESS"
            MODE_EMAIL_ADDRESS_ONLY -> SCOPE_EMAIL_ADDRESS
            else -> SCOPE_EMAIL_ADDRESS
        }
        dialog = Dialog(activity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(allowCancelDialogPrompt)
        dialog!!.setContentView(R.layout.linkedin_popup_layout)
        val window = dialog!!.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val linkedInWebView = dialog!!.findViewById<WebView>(R.id.linkedInWebView)
        val settings = linkedInWebView.settings
        try {
            settings.allowFileAccess = false
        } catch (e: Exception) {
            ExceptionManager.exceptionLog(e)
        }
        linkedInWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        try {
            linkedInWebView.settings.allowFileAccess = false
        } catch (e: Exception) {
            ExceptionManager.exceptionLog(e)
        }
        linkedInWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)

                val error = uri.getQueryParameters(ERROR).getOrNull(0)
                if (error != null) {
                    linkedInManagerResponse.onGetCodeFailed()
                    return false
                }

                val authCode = uri.getQueryParameters(CODE).getOrNull(0)

                if (authCode != null) {
                    getAccessToken(authCode)
                    linkedInManagerResponse.onGetCodeSuccess(authCode)
                    return true
                }else{
                    return false
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar!!.visibility = View.GONE
            }
        }

        progressBar = dialog!!.findViewById(R.id.prog_auth_dialog)
        dialog!!.findViewById<ImageView>(R.id.cancel_auth_dialog).setOnClickListener {
            dialog!!.dismiss()
        }

        linkedInWebView.loadUrl(url)
        dialog!!.show()
    }

    private fun getAccessToken(code: String) {
        val accessTokenURL = (URLManager.URL_FOR_ACCESS_TOKEN + "?"
                + "client_id=" + clientID
                + "&client_secret=" + clientSecret
                + "&grant_type=authorization_code"
                + "&redirect_uri=" + redirectionURL
                + "&code=" + code)
        LinkedInControl().getAccessToken(accessTokenURL, object : LinkedInAccessTokenResponse {
            override fun onAuthenticationSuccess(jsonObject: JSONObject?) {
                val gson = Gson()
                val linkedInAccessToken: LinkedInAccessToken = gson.fromJson(jsonObject.toString(), LinkedInAccessToken::class.java)
                linkedInManagerResponse.onGetAccessTokenSuccess(linkedInAccessToken)
                getRelevantData(linkedInAccessToken)
            }

            override fun onAuthenticationFailed() {
                linkedInManagerResponse.onGetAccessTokenFailed()
            }
        })
    }

    private fun getLinkedInProfileData(linkedInAccessToken: LinkedInAccessToken): LinkedInUserProfile {
        LinkedInControl().getProfileData(URLManager.URL_FOR_PROFILE_DATA + linkedInAccessToken.access_token, object : LinkedInProfileDataResponse {
            override fun onRequestSuccess(jsonObject: JSONObject?) {
                try {
                    val gson = Gson()
                    val linkedInNameBean: LinkedInNameBean = gson.fromJson(jsonObject.toString(), LinkedInNameBean::class.java)
                    linkedInUserProfile.userName = linkedInNameBean
                    try {
                        val imageURL = jsonObject!!
                                .getJSONObject("profilePicture")
                                .getJSONObject("displayImage~")
                                .getJSONArray("elements")
                                .getJSONObject(3)
                                .getJSONArray("identifiers")
                                .getJSONObject(0)
                                .getString("identifier")
                        linkedInUserProfile.imageURL = imageURL
                    } catch (e: Exception) {
                        ExceptionManager.exceptionLog(e)
                        linkedInUserProfile.imageURL = ""
                    }
                    try {
                        PreferenceManager.getInstance(activity.applicationContext).putString(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN, Gson().toJson(LinkedInLoggedTokenBean(linkedInAccessToken.access_token,mode,true)))
                    } catch (e: Exception) {
                        ExceptionManager.exceptionLog(e)
                    }
                    linkedInManagerResponse.onGetProfileDataSuccess(linkedInUserProfile)
                } catch (e: Exception) {
                    ExceptionManager.exceptionLog(e)
                    linkedInManagerResponse.onGetProfileDataFailed()
                }
            }

            override fun onRequestFailed() {
                linkedInManagerResponse.onGetProfileDataFailed()
            }
        })
        return linkedInUserProfile
    }

    private fun getLinkedInEmailAddress(linkedInAccessToken: LinkedInAccessToken): LinkedInEmailAddress {
        LinkedInControl().getEmailAddress(URLManager.URL_FOR_EMAIL_ADDRESS + linkedInAccessToken.access_token, object : LinkedInEmailAddressResponse {
            override fun onSuccessResponse(jsonObject: JSONObject?) {
                try {
                    val emailAddress = jsonObject!!
                            .getJSONArray("elements")
                            .getJSONObject(0)
                            .getJSONObject("handle~")
                            .getString("emailAddress")
                    try {
                        PreferenceManager.getInstance(activity.applicationContext).putString(CommonInfo.STRING_COMMON_PREFERENCE, CommonInfo.STRING_ACCESS_TOKEN, Gson().toJson(LinkedInLoggedTokenBean(linkedInAccessToken.access_token,mode,true)))
                    } catch (e: Exception) {
                        ExceptionManager.exceptionLog(e)
                    }
                    linkedInEmailAddress.emailAddress = emailAddress
                    linkedInManagerResponse.onGetEmailAddressSuccess(linkedInEmailAddress)
                } catch (e: Exception) {
                    ExceptionManager.exceptionLog(e)
                    linkedInEmailAddress.emailAddress = ""
                    linkedInManagerResponse.onGetEmailAddressFailed()
                }
            }

         override fun onFailedResponse() {
                linkedInManagerResponse.onGetEmailAddressFailed()
            }
        })
        return linkedInEmailAddress
    }

    private fun getRelevantData(linkedInAccessToken: LinkedInAccessToken): LinkedInUser {
        when (mode) {
            MODE_LITE_PROFILE_ONLY -> linkedInUser.userProfile = getLinkedInProfileData(linkedInAccessToken)
            MODE_BOTH_OPTIONS -> {
                linkedInUser.linkedInEmailAddress = getLinkedInEmailAddress(linkedInAccessToken)
                linkedInUser.userProfile = getLinkedInProfileData(linkedInAccessToken)
            }
            MODE_EMAIL_ADDRESS_ONLY -> linkedInUser.linkedInEmailAddress = getLinkedInEmailAddress(linkedInAccessToken)
            else -> linkedInUser.linkedInEmailAddress = getLinkedInEmailAddress(linkedInAccessToken)
        }
        return linkedInUser
    }
}
