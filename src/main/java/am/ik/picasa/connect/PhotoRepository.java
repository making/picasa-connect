package am.ik.picasa.connect;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
	Page<Photo> findByUserIdOrderByUpdatedDesc(String userId, Pageable pageable);

	Page<Photo> findByUserIdAndGenre_genreIdOrderByUpdatedDesc(String userId,
			Integer genreId, Pageable pageable);
}
