package telran.ashkelon2018.forum.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.ashkelon2018.forum.domain.Post;

public interface ForumRepository extends MongoRepository<Post, String> {
	
	Iterable<Post> findPostsByAuthor(String author);
	
	Iterable<Post> findByTagsIn(List<String> tagsTags);	
	
	Iterable<Post> findPostsByDateCreatedBetween(LocalDate from, LocalDate to);

}
