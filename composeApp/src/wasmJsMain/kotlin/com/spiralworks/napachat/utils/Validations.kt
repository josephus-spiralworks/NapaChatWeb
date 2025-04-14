val MAX_PHONE_LENGTH = 15
val MAX_EMAIL_LENGTH = 254

fun isPhoneValid(phone: String): Boolean {
    return phone.length in 7..MAX_PHONE_LENGTH
}

fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(email)
}