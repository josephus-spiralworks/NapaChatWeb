import kotlinx.coroutines.await
import kotlin.js.Promise

@JsFun("window.firebaseAuth.sendLoginLink")
external fun sendLoginLinkJsInternal(email: String): Promise<JsAny>

@JsFun("window.firebaseAuth.logout")
external fun logoutJs(): Promise<JsAny>

@JsFun("response => response.success")
external fun getSuccess(response: JsAny): Boolean

@JsFun("response => response.error")
external fun getError(response: JsAny): String?

@JsFun("window.firebaseAuth.completeSignIn")
external fun completeSignInJs(): Promise<JsAny>

@JsFun("result => result.completed")
external fun getCompleted(result: JsAny): Boolean

@JsFun("result => result.email")
external fun getEmail(result: JsAny): String?

@JsFun("window.firebaseAuth.onAuthChange")
external fun onAuthChange(callback: (JsAny) -> Unit)

@JsFun("result => result.loggedIn")
external fun getLoggedIn(result: JsAny): Boolean

@JsFun("result => result.email")
external fun getUserEmail(result: JsAny): String?

suspend fun sendLoginLinkJs(email: String): Result<String> {
    return try {
        val response: JsAny = sendLoginLinkJsInternal(email).await()

        val success = getSuccess(response)
        val error = getError(response)

        if (success) {
            Result.success("Login link sent!")
        } else {
            Result.failure(Exception(error ?: "Unknown error"))
        }
    } catch (e: Throwable) {
        Result.failure(e)
    }
}