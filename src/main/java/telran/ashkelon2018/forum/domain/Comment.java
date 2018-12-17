package telran.ashkelon2018.forum.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = {"user", "dateCreated"})
@ToString
public class Comment {
	String user;
    @Setter String text;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dateCreated;
    int likes;
    
	public Comment(String user, String text) {
		this.user = user;
		this.text = text;
		dateCreated = LocalDateTime.now();
	}
    
	public void addLike() {
		likes++;
	}
    
}
