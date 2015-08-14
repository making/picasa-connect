package am.ik.picasa.connect;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
	List<Genre> findByUserIdOrderByGenreId(String userId);

	long countByUserId(String userId);
}
