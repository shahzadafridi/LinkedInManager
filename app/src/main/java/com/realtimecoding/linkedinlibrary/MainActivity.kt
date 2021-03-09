package com.realtimecoding.linkedinlibrary

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.realtimecoding.linkedinmanager.events.LinkedInManagerResponse
import com.realtimecoding.linkedinmanager.events.LinkedInUserLoginDetailsResponse
import com.realtimecoding.linkedinmanager.events.LinkedInUserLoginValidationResponse
import com.realtimecoding.linkedinmanager.helper.LinkedInRequestManager
import com.realtimecoding.linkedinmanager.models.LinkedInAccessToken
import com.realtimecoding.linkedinmanager.models.LinkedInEmailAddress
import com.realtimecoding.linkedinmanager.models.LinkedInUserProfile


class MainActivity : AppCompatActivity(), LinkedInManagerResponse {

    private var context: Context? = null
    private var activity: Activity? = null
    private var ivImage: AppCompatImageView? = null
    private var ivLogout: ImageView? = null
    private var tvFirstName: TextView? = null
    private var tvLastName: TextView? = null
    private var tvEmailAddress: TextView? = null
    private var btnSignInWithLinkedIn: Button? = null
    private var linkedInRequestManager: LinkedInRequestManager? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initObjects()
    }

    private fun initObjects() {
        context = this@MainActivity
        activity = this@MainActivity
        //Client ID and Client Secret is in the LinkedIn Developer Console
        //provide Redirection URL which is available in developer console. This URL is available in LinkedIn Developer Console
        linkedInRequestManager = LinkedInRequestManager(activity!!, this, "clientID", "clientSecret", "redirectionURL", true)
        initComponents()
    }

    private fun initComponents() {
        ivImage = findViewById(R.id.ivImage)
        ivLogout = findViewById(R.id.ivLogout)
        tvFirstName = findViewById(R.id.tvFirstName)
        tvLastName = findViewById(R.id.tvLastName)
        tvEmailAddress = findViewById(R.id.tvEmailAddress)
        progressBar = findViewById(R.id.progressBar)
        btnSignInWithLinkedIn = findViewById(R.id.btnSignInWithLinkedIn)
        btnSignInWithLinkedIn!!.setOnClickListener{
            startLinkedInSignIn()
        }
        ivLogout!!.setOnClickListener {
            logout()
            ivImage!!.setImageResource(R.drawable.placeholder_image)
            tvFirstName!!.text = ""
            tvLastName!!.text = ""
            tvEmailAddress!!.text = ""
        }
    }

    private fun startLinkedInSignIn() {
        linkedInRequestManager!!.showAuthenticateView(LinkedInRequestManager.MODE_BOTH_OPTIONS)
        //LinkedInRequestManager.MODE_BOTH_OPTIONS - can get email and user profile data with user profile image
        //LinkedInRequestManager.MODE_EMAIL_ADDRESS_ONLY - can get only the user profile email address
        //LinkedInRequestManager.MODE_LITE_PROFILE_ONLY - can get the user profile details with profile image
    }

    private fun closeAuthenticationView() {
        linkedInRequestManager!!.dismissAuthenticateView()
    } //user is not logged into the application//token has been expired. need to obtain a new code

    //Session token is active. can use to get data from linkedin
    fun isUserLoggedIn() {
        linkedInRequestManager!!.isLoggedIn(object : LinkedInUserLoginValidationResponse {
            override fun activeLogin() {
                //Session token is active. can use to get data from linkedin
            }

            override fun tokenExpired() {
                //token has been expired. need to obtain a new code
            }

            override fun notLogged() {
                //user is not logged into the application
            }
        })
    }

    private fun logout() {
        //logout the user
        linkedInRequestManager!!.logout()
    }

    private fun checkUserLoggedPermissions() {
        linkedInRequestManager!!.getLoggedRequestedMode(object : LinkedInUserLoginDetailsResponse {
            override fun loggedMode(mode: Int) {
                //user is already logged in. active token. mode is available
                when (mode) {
                    LinkedInRequestManager.MODE_LITE_PROFILE_ONLY -> {
                    }
                    LinkedInRequestManager.MODE_EMAIL_ADDRESS_ONLY -> {
                    }
                    LinkedInRequestManager.MODE_BOTH_OPTIONS -> {
                    }
                }
            }

            override fun tokenExpired() {
                //token has been expired. need to obtain a new code
            }

            override fun notLogged() {
                //user is not logged into the application
            }
        })
    }

    override fun onGetProfileDataSuccess(linkedInUserProfile: LinkedInUserProfile?) {
        tvFirstName!!.text = linkedInUserProfile!!.userName!!.firstName!!.localized!!.en_US
        tvLastName!!.text = linkedInUserProfile.userName!!.lastName!!.localized!!.en_US
        Glide.with(context!!)
                .load(linkedInUserProfile.imageURL)
                .into(ivImage!!)
        linkedInRequestManager!!.dismissAuthenticateView()
    }

    override fun onGetEmailAddressSuccess(linkedInEmailAddress: LinkedInEmailAddress?) {
        tvEmailAddress!!.text = linkedInEmailAddress!!.emailAddress
        linkedInRequestManager!!.dismissAuthenticateView()
    }

    override fun onGetEmailAddressFailed() {}
    override fun onGetAccessTokenFailed() {}
    override fun onGetAccessTokenSuccess(linkedInAccessToken: LinkedInAccessToken?) {}
    override fun onGetCodeFailed() {}
    override fun onGetCodeSuccess(code: String?) {}
    override fun onGetProfileDataFailed() {}

}
