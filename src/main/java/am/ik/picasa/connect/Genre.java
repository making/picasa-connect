package am.ik.picasa.connect;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = { @Index(columnList = "USER_ID"),
		@Index(columnList = "GENRE_NAME,USER_ID", unique = true) })
public class Genre implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "GENRE_ID")
	private Integer genreId;
	@Column(name = "GENRE_NAME")
	@Size(min = 1, max = 255)
	@NotNull
	private String genreName;
	@Column(name = "USER_ID", updatable = false)
	@NotNull
	private String userId;
}
