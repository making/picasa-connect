package am.ik.picasa.connect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users/{userId}")
public class PhotoApiController {
	@Autowired
	PhotoRepository photoRepository;

	@RequestMapping(value = "photos", method = RequestMethod.GET)
	Page<Photo> listPhotos(@PathVariable String userId,
			@PageableDefault Pageable pageable) {
		return photoRepository.findByUserIdOrderByUpdatedDesc(userId, pageable);
	}

	@RequestMapping(value = "genres/{genreId}/photos", method = RequestMethod.GET)
	Page<Photo> listPhotosByGenreId(@PathVariable String userId,
			@PathVariable Integer genreId, @PageableDefault Pageable pageable) {
		return photoRepository.findByUserIdAndGenre_genreIdOrderByUpdatedDesc(userId,
				genreId, pageable);
	}
}
