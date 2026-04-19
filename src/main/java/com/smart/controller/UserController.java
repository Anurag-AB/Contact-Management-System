package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	private final ContactRepository contactRepository;

	@Autowired
	private UserRepository userRepository;

	UserController(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

	// method for adding data toresponse
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);

		model.addAttribute("user", user);

	}

	// dashboard home
	@RequestMapping("/index")
	public String Dashboard(Model model, Principal principal) {

		// String userName = principal.getName();
		// User user = userRepository.getUserByUserName(userName);

		// model.addAttribute("user",user);

		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "add contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Principal principal,
			RedirectAttributes redirectAttributes, Model model) {

		// ✅ VALIDATION CHECK
		if (result.hasErrors()) {
			model.addAttribute("contact", contact);
			return "normal/add_contact_form";
		}

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			if (file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);

			// ✅ SUCCESS
			redirectAttributes.addFlashAttribute("message", "Contact added successfully!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-success");

		} catch (Exception e) {
			e.printStackTrace();

			// ❌ ERROR
			redirectAttributes.addFlashAttribute("message", "Something went wrong!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
		}

		return "redirect:/user/add-contact";
	}

	// show contacts handler
	// per page=5[n]
	// current page=0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

		// get all contact by user
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	@GetMapping("/{cId}/contact")
	public String showcontactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		//
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}

	@GetMapping("delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal,
			RedirectAttributes redirectAttributes) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		//
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			contact.setUser(null);
			this.contactRepository.delete(contact);

			redirectAttributes.addFlashAttribute("message", "Contact deleted successfully!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		}

		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model model) {

		model.addAttribute("title", "update-contact");

		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, RedirectAttributes redirectAttributes) {

		try {

// Old contact
			Contact oldContact = this.contactRepository.findById(contact.getcId()).get();

// Image update
			if (!file.isEmpty()) {

// delete old image
				File deleteDir = new ClassPathResource("static/img").getFile();
				File oldFile = new File(deleteDir, oldContact.getImage());

				if (oldFile.exists()) {
					oldFile.delete();
				}

// save new image with UNIQUE NAME
				File saveDir = new ClassPathResource("static/img").getFile();

				String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

				Path path = Paths.get(saveDir.getAbsolutePath() + File.separator + fileName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(fileName);

			} else {
// keep old image
				contact.setImage(oldContact.getImage());
			}

// set user
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);

// save
			this.contactRepository.save(contact);

			redirectAttributes.addFlashAttribute("message", "Contact updated successfully!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-success");

		} catch (Exception e) {
			e.printStackTrace();

			redirectAttributes.addFlashAttribute("message", "Error updating contact!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
		}

		return "redirect:/user/" + contact.getcId() + "/contact";
	}
	
	//Your profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
	    model.addAttribute("title","Profile Page");
	    
	    return "normal/profile";
	}
	
	@GetMapping("/setting")
	public String yourSetting(Model model) {
	    model.addAttribute("title","Setting Page");
	    
	    return "normal/setting";
	}


}
