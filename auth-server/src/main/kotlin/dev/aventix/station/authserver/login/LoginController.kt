package dev.aventix.station.authserver.login

import dev.aventix.station.authserver.user.ChangePasswordFormData
import dev.aventix.station.authserver.user.UserService
import dev.aventix.station.authserver.user.spring.StationUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

// use controller instead of rest controller to make the strings load mvc templates
// instead of being returned as strings
@Controller
class LoginController(
    private val userService: UserService,
) {
    @GetMapping("/login")
    fun login(model: Model): String {
        return "login"
    }

    @GetMapping("/change-password")
    fun changePasswordView(model: Model): String {
        model.addAttribute("data", ChangePasswordFormData("", ""))
        return "change_password"
    }

    @PostMapping("/change-password")
    fun changePassword(@AuthenticationPrincipal user: StationUserDetails, model: Model, @ModelAttribute("data") data: ChangePasswordFormData): String {
        println("Changing password submitted.")

        if (data.password != data.repeatedPassword) {
            return "redirect:/change-password?error"
        }

        // change password in database
        // this also automatically sets the passwordChanged flag to true
        // and allows the next login flow to redirect back to the frontend.
        userService.changePassword(user.username, data.password)

        return "redirect:/login?changePasswordSuccess"
    }

}