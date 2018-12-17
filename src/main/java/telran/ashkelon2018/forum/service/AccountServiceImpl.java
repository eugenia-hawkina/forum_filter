package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exeptions.ForbiddenException;
import telran.ashkelon2018.forum.exeptions.UserConflictException;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public UserProfileDto addUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.decodeToken(token);
		if (userRepository.existsById(credentials.getLogin())) {
			throw new UserConflictException();
		}
		String hashPassword = BCrypt.hashpw(credentials.getPassword(), 
				BCrypt.gensalt());
		// first arg = what we crypt, second = how (key)
		UserAccount userAccount = UserAccount.builder()
				.login(credentials.getLogin())
				.password(hashPassword)
				.firstName(userRegDto.getFirstName())
				.lastName(userRegDto.getLastName()).role("user")
				.expdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()))
				.build();
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}

	private UserProfileDto convertToUserProfileDto(UserAccount userAccount) {
		return UserProfileDto.builder()
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.login(userAccount.getLogin())
				.roles(userAccount.getRoles())
				.build();
	}

	@Override
	public UserProfileDto editUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.decodeToken(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		// user can't be null, bec it's for registered users only
		if (userRegDto.getFirstName() != null) {
			userAccount.setFirstName(userRegDto.getFirstName());
		}
		if (userRegDto.getLastName() != null) {
			userAccount.setLastName(userRegDto.getLastName());
		}
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}

	
	@Override
	public UserProfileDto removeUser(String login, String token) {
		// only admin or user himself can remove the user
		AccountUserCredentials credentials = accountConfiguration.decodeToken(token);
		UserAccount user = userRepository.findById(credentials.getLogin()).get();
		Set<String> roles = user.getRoles();
		boolean hasRight = roles.stream().anyMatch(s -> "admin".equals(s) || "moderator".equals(s));
		hasRight = hasRight || credentials.getLogin().equals(login);
		if (!hasRight) {
			throw new ForbiddenException("Can't remove user");
		}
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount != null) {
			userRepository.delete(userAccount);
		}
		return convertToUserProfileDto(userAccount);
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount != null) {
			// in roleFilter we check user who adds != null
			// here - to whom add
			userAccount.addRole(role);
			userRepository.save(userAccount);
		} else {
			return null;
		}
		return userAccount.getRoles();
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount != null) {
			userAccount.removeRole(role);
			userRepository.save(userAccount);
		} else {
			return null;
		}
		return userAccount.getRoles();
	}

	@Override
	public void changePassword(String password, String token) {
		// FIXME
		AccountUserCredentials credentials = accountConfiguration.decodeToken(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		if (credentials.getPassword().equals(userAccount.getPassword())
				&& credentials.getLogin().equals(userAccount.getLogin())) {
			String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			userAccount.setPassword(hashPassword);
			userAccount.setExpdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()));
			userRepository.save(userAccount);
		}
	}

	@Override
	public UserProfileDto loginUser(String token) {
		// FIXME
		AccountUserCredentials credentials = accountConfiguration.decodeToken(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).orElse(null);
		return convertToUserProfileDto(userAccount);
	}

}
