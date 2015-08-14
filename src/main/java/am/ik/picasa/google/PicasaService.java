package am.ik.picasa.google;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

import com.rometools.rome.feed.atom.Feed;

@Component
public class PicasaService {
	@Autowired
	OAuth2RestTemplate restTemplate;

	public List<PicasaAlbum> findAlbums() {
		Feed f = restTemplate
				.getForEntity("https://picasaweb.google.com/data/feed/api/user/default",
						Feed.class)
				.getBody();
		return f.getEntries().stream().map(PicasaAlbum::new).collect(Collectors.toList());
	}

	public List<PicasaPhoto> findPhotosByAlbumId(String albumId) {
		Feed f = restTemplate.getForEntity(
				"https://picasaweb.google.com/data/feed/api/user/default/albumid/"
						+ albumId + "?imgmax=1600",
				Feed.class).getBody();
		return f.getEntries().stream().map(PicasaPhoto::new).collect(Collectors.toList());
	}
}
