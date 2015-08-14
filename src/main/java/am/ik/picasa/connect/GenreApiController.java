package am.ik.picasa.connect;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("api/users/{userId}/genres")
public class GenreApiController {
	@Autowired
	GenreRepository genreRepository;

	@RequestMapping(method = RequestMethod.GET)
	GenresResource listGenres(@PathVariable String userId,
			@Value("#{request.requestURL}") StringBuffer requestUrl) {
		List<Genre> genres = genreRepository.findByUserIdOrderByGenreId(userId);
		return new GenresResource(genres,
				genres.stream()
						.map(x -> new Link(x.getGenreName(),
								requestUrl.toString() + "/" + x.getGenreId() + "/photos"))
				.collect(Collectors.toList()));
	}

	@Data
	public static class GenresResource implements Serializable {
		private final List<Genre> content;
		@JsonProperty("_links")
		private final List<Link> links;
	}

	@Data
	public static class Link implements Serializable {
		private final String rel;
		private final String href;
	}
}
