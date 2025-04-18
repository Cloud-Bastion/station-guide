package dev.aventix.station.authserver.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {

    @GetMapping("/login")
    fun login(): String {
        return "login" // Name of the HTML template (e.g., login.html)
    }

    // Optional: A page to redirect to after successful social login
    // if not part of an ongoing OAuth2 authorization flow.
    // @GetMapping("/logged-in-successfully")
    // public String loggedInSuccess() {
    //    return "logged_in";
    // }


}