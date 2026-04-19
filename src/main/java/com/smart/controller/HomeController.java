package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title","home-smart contact");
		
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title","about-smart contact");
		
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","Register-smart contact");
		 if(!model.containsAttribute("user")) {
		        model.addAttribute("user", new User());
		    }		
		return "signup";
	}
	
	// handler for registering user
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user ,BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,RedirectAttributes redirectAttributes, Model model) {
		
		try {
			
			if(!agreement) {
				System.out.println("you have not agreeed the terms and condition");
				throw new Exception("you have not agreeed the terms and condition");
			}
			if(result1.hasErrors()) {
				
				System.out.println("ERROR"+result1.toString());
				model.addAttribute("user"+user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			
			System.out.println("Agreement"+agreement);
			System.out.println("USER"+user);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			User result = this.userRepository.save(user);
			
		//	model.addAttribute("user", new User());
			
			// success message using flash attributes
	        redirectAttributes.addFlashAttribute(
	                "message",
	                new Message("Successfully Registered !!", "alert-success")
	        );
			
	     // Redirect to signup page to show the message
	        return "redirect:/signup";
			
		}catch(Exception e) {
			e.printStackTrace();
			//model.addAttribute("user",user);
			// Error message using flash attributes
			redirectAttributes.addFlashAttribute(
	                "message",
	                new Message("Something went wrong !! " + e.getMessage(), "alert-danger")
	        );
			redirectAttributes.addFlashAttribute("user", user);

			
			// Redirect to signup page to show the error
            return "redirect:/signup";
		}

		
	}
	@GetMapping("/signin")
	public String customlogin(Model model) {
		
		model.addAttribute("title","login page");
		
		return "login";
	}
	

}
