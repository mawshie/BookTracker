package org.example.booktracker.security;

import org.example.booktracker.domain.User;
import org.example.booktracker.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model){

        if (error != null){
            model.addAttribute("error","Invalid username or password");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String pwd,
                               @RequestParam String confirmPwd,
                               @RequestParam(required = false) String firstName,
                               @RequestParam(required = false) String lastName,
                               RedirectAttributes redirectAttributes){
        try{
            if (username == null || username.trim().isEmpty()){
                throw new RuntimeException("Username is required");
            }

            if (pwd == null || pwd.length() < 4){
                throw new RuntimeException("Password must be at least 4 characters");
            }

            if (!pwd.equals(confirmPwd)){
                throw new RuntimeException("Passwords don't match");
            }

            userService.registerUser(username.trim(), pwd, firstName, lastName);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Log in.");
            return "redirect:/login";

        } catch (Exception e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model){
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("currentUser", currentUser);
        return "dashboard";
    }
}
