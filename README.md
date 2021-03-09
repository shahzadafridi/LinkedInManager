# LinkedInManager  [![](https://jitpack.io/v/shahzadafridi/LinkedInManager.svg)](https://jitpack.io/#shahzadafridi/LinkedInManager)
Android Library to Authenticate with LinkedIn

## What is LinkedInManager
LinkedInManager is an android library which can be used to implement LinkedIn Sign in for any android application.

```
onGetCodeSuccess(code: String) - will return the authorization code to obtain the access token

onGetCodeFailed() - Failed situation when trying to get the authorization code which is used to obtain the access token

onGetAccessTokenSuccess(linkedInAccessToken: LinkedInAccessToken) - will return the LinkedIn Access token for services which is mentioned in the scope
  
onGetAccessTokenFailed() - Failed situation when trying to get the access token

onGetProfileDataSuccess(linkedInUserProfile: LinkedInUserProfile) - will return the user's profile data including first name, last name, profile ID and user image
  
onGetProfileDataFailed - Failed situation when trying to get the profile data

onGetEmailAddressSuccess(linkedInEmailAddress: LinkedInEmailAddress) - will return the user's email address

onGetEmailAddressFailed() - Failed situation when trying to get the profile data
```

## V1.0 Added Features
* Converted to kotlin
* Fixed login in with linkedin auth token
* add progress bar while loading
* modified demo screen.


## Implementation

### Step : 1 - Add the JitPack repository to your project root build.gradle file
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step : 2- Add the dependency
```
dependencies {
	implementation 'com.github.shahzadafridi:LinkedInManager:1.0'
}
```

### Step : 3 - Implement linkedInManagerResponse in activity.
```
    class MainActivity : AppCompatActivity(), LinkedInManagerResponse {}
```

### Step : 4 - Create a LinkedInRequestManager instance and initialize it.
```
    var linkedInRequestManager: LinkedInRequestManager = new LinkedInRequestManager(Activity, LinkedInManagerResponse, "CLIENT ID", "CLIENT SECRET", "REDIRECTION URL", allowCancelDialogPrompt);
```

CLIENT ID, CLIENT SECRET and REDIRECTION URL is available at LinkedIn Developer Console. variable allowCancelDialogPrompt is a boolean

### Step : 5 - invoke the showAuthenticateView() with a mode to start the sign in process
```
    linkedInRequestManager.showAuthenticateView(mode);
```

### Available modes
```
    LinkedInRequestManager.MODE_EMAIL_ADDRESS_ONLY - will return only the email address
    LinkedInRequestManager.MODE_LITE_PROFILE_ONLY - will return only the profile data
    LinkedInRequestManager.MODE_BOTH_OPTIONS - will return both email address and profile data
```

## To get the user's First name, last name and profile ID, use the following overrided method in your activity which is inherited from LinkedInManagerResponse
```
    override fun onGetProfileDataSuccess(linkedInUserProfile: LinkedInUserProfile?) {
        linkedInUserProfile.imageURL
        inkedInUserProfile.userName!!.firstName!!.localized!!.en_US 
        inkedInUserProfile.userName!!.lastName!!.localized!!.en_US 
        linkedInUserProfile.userName!!.id // User's profile ID
    }
```
## To get the user's email address, use the following overrided method in your activity which is inherited from LinkedInManagerResponse
```
    override fun onGetEmailAddressSuccess(linkedInEmailAddress: LinkedInEmailAddress?) {
        // User's email address
        linkedInEmailAddress!!.emailAddress
    }
		
```
# OPTIONAL 
## If you need to get the User's Access token, use the following overrided method in your activity which is inherited from LinkedInManagerResponse
```
@Override
public void onGetAccessTokenSuccess(LinkedInAccessToken linkedInAccessToken) {
        linkedInAccessToken.getAccess_token(); //User's access token
}
```

## If you need to get the User's authorization code, user the following overrided method in your activity which is inherited from LinkedInManagerResponse
```
    override fun onGetAccessTokenSuccess(linkedInAccessToken: LinkedInAccessToken?) {
        
    }
```

## If you need to check whether the user is already logged in or not, use the following code segment inside your activity.
```
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
```

## If you need to check whether the user is already logged in and logged mode, user the following code segment inside your activity.
```
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
```

## To close authentication dialog manually, use below code line.
```
linkedInRequestManager.dismissAuthenticateView()
```

## To logout the user manually, use below code line.
```
linkedInRequestManager.logout()
```

## Orignal repository
Thanks to Sumudu Sahan for creating the basic repository of linkedin authenticaiton. In future we can enhance the features and optmizing code with latest jetpack libraries.
```
https://github.com/Sumudu-Sahan/LinkedInManager
```
