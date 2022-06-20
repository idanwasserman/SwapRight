package iob.logic.services;

import java.util.List;

import iob.UsersAPI.UserBoundary;
import iob.data.entities.UserRoleEntity;

public interface EnhancedUsersService extends UsersService {
	
	public List<UserBoundary> getAllUsers(
			String adminDomain, String adminEmail,
            int size, int page,
            boolean sortAscending, String[] sortBy);

	/**
	 * method Check if user exists in the database. If not -> User NotFoundException thrown.
	 * If user exists -> check that user's role is one of the valid roles, if not -> throw AccessDeniedException
	 * @param userDomain User's domain
	 * @param userEmail User's email
	 * @param validRoles Roles that are allowed to perform method that called this function
	 * @param msg Message to print if throwing AccessDeniedException
	 * @return User's role as entity
	 */
	public UserRoleEntity checkUser(
			String userDomain, String userEmail, 
			UserRoleEntity[] validRoles, String msg);
}
