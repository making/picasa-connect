package am.ik.picasa.connect;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@Table(indexes = { @Index(columnList = "USER_ID"),
		@Index(columnList = "GENRE_ID,USER_ID") })
public class Photo implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "PHOTO_ID")
	private Integer photoId;
	@Column(name = "URL", updatable = false, unique = true)
	private String url;
	@Column(name = "TITLE")
	@Size(min = 1, max = 100)
	@NotNull
	private String title;
	@Column(name = "COMMENT")
	@Size(min = 0, max = 255)
	@NotNull
	private String comment;
	@ManyToOne
	@JoinColumn(name = "GENRE_ID")
	@Valid
	@NotNull
	private Genre genre;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:ss")
	@Column(name = "PUBLISHED")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date published;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:ss")
	@Column(name = "UPDATED")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date updated;
	@Column(name = "USER_ID", updatable = false)
	@NotNull
	private String userId;
	@Version
	private Integer version;

	@Transient
	public String getLargeThumbnail() {
		return this.url == null ? "" : this.url.replace("/s1600", "");
	}

	@Transient
	public String getMediumThumbnail() {
		return this.url == null ? "" : this.url.replace("/s1600", "/s720");
	}

	@Transient
	public String getSmallThumbnail() {
		return this.url == null ? "" : this.url.replace("/s1600", "/s160");
	}
}
