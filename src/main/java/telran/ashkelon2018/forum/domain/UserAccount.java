package telran.ashkelon2018.forum.domain;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "login")
@Document(collection = "forum_user")
public class UserAccount {
	@Id
	@NotNull 
	String login;
	@NotNull 
	String password;
	@NotNull 
	String firstName;
	@NotNull 
	String lastName;
	@Singular 			// in builder one by one
	Set<String> roles;
	LocalDateTime expdate;
	
	public void addRole(String role) {
		roles.add(role);
	}
	
	public void removeRole(String role) {
		roles.remove(role);
	}
}
