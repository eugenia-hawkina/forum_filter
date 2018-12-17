package telran.ashkelon2018.forum.service.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;

@Service
@Order(3)
public class RoleFilter implements Filter {

	@Autowired
	UserAccountRepository repository;

	@Autowired
	AccountConfiguration configuration;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req; // casting
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String token = request.getHeader("Authorization");
		String method = request.getMethod();

		// boolean cond1 = path.endsWith("admin") &&
		// ("DELETE".equals(method) || "PUT".equals(method));
		// if (! (cond1)) {S
		// response.sendError(403, "No rights");
		// return;
		// }

		if (path.startsWith("/account/role/")) {
			AccountUserCredentials userCredentials = configuration.decodeToken(token);
			UserAccount userAccount = repository.findById(userCredentials.getLogin()).orElse(null);
			if (!userAccount.getRoles().contains("admin")) {
				response.sendError(403, "No rights");
				return;
			}
		}
		chain.doFilter(request, response);
	}
}