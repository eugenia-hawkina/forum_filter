package telran.ashkelon2018.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;
import telran.ashkelon2018.forum.exeptions.ForbiddenException;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired
	ForumRepository repository;

	@Autowired
	AccountConfiguration configuration;

	@Autowired
	UserAccountRepository userRepository;

	@Override
	public Post addNewPost(NewPostDto newPost) {
		// FIXME
		Post post = new Post(newPost.getTitle(), 
				newPost.getContent(), newPost.getAuthor(), newPost.getTags());
		return repository.save(post);
	}

	@Override
	public Post getPost(String id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Post removePost(String id, String token) {
		AccountUserCredentials credentials = configuration.decodeToken(token);
		Post post = repository.findById(id).orElse(null);
		if (post == null) {
			throw new RuntimeException("What are you trying to remove?");
		}
		boolean cond1 = post.getAuthor().equals(credentials.getLogin());
		UserAccount user = userRepository.findById(credentials.getLogin()).orElse(null);
		if (user == null) {
			throw new RuntimeException("Wrong user");
		}
		boolean cond2 = user.getRoles().stream()
				.anyMatch(s -> "admin".equals(s) || "moderator".equals(s));
		if (!(cond1 || cond2)) {
			throw new RuntimeException("Go to hell");
		}
		repository.deleteById(id);
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdate, String token) {
		AccountUserCredentials credentials = configuration.decodeToken(token);
		Post post = repository.findById(postUpdate.getId()).orElse(null);
		if (post == null) {
			throw new RuntimeException("No such post");
		}
		if (post.getAuthor().equals(credentials.getLogin())) {
			post.setContent(postUpdate.getContent());
			repository.save(post);
			return post;
		} else {
			throw new ForbiddenException("Can't update post");
		}

	}

	@Override
	public boolean addLike(String id) {
		Post post = repository.findById(id).orElse(null);
		if (post == null) {
			return false;
		}
		post.addLike();
		repository.save(post);
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment) {
		Post post = repository.findById(id).orElse(null);
		if (post != null) {
			Comment comment = new Comment(newComment.getUser(), newComment.getText());
			post.addComment(comment);
			repository.save(post);
		}
		return post;
	}

	@Override
	public Iterable<Post> findPostsByTags(List<String> tags) {
		return repository.findByTagsIn(tags);
	}

	@Override
	public Iterable<Post> findPostsByAuthor(String author) {
		return repository.findPostsByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostsByDates(DatePeriodDto datesDto) {
		return repository.findPostsByDateCreatedBetween(datesDto.getDateFrom(), 
				datesDto.getDateTo());

	}

}
