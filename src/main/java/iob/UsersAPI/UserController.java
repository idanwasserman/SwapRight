package iob.UsersAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iob.logic.services.UsersService;

@RestController
public class UserController {
	
	private UsersService users;
	
	@Autowired
	public UserController(UsersService users) {
		this.users = users;
	}
	
	@RequestMapping(
			path = "/iob/users",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser (@RequestBody NewUserBoundary newUser) {
		return users.createUser(new UserBoundary(newUser));
	}
	
	@RequestMapping(
			path = "/iob/users/login/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary loginUser(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail) {
		return users.login(userDomain, userEmail);
	}

	@RequestMapping(
			path="/iob/users/{userDomain}/{userEmail}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUserDetails (
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary user) {
		users.updateUser(userDomain, userEmail, user);
	}

}
