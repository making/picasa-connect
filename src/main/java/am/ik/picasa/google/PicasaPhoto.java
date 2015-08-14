package am.ik.picasa.google;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.rometools.rome.feed.atom.Entry;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PicasaPhoto extends PicasaEntryBase {
	private String src;
	private String largeThumbnail;
	private String mediumThumbnail;
	private String smallThumbnail;
	private Date shootingDate;

	public PicasaPhoto(Entry entry) {
		super(entry);
		if (entry.getContents() != null && !entry.getContents().isEmpty()) {
			setSrc(entry.getContents().get(0).getSrc());
			setLargeThumbnail(getSrc().replace("/s1600", ""));
			setMediumThumbnail(getSrc().replace("/s1600", "/s720"));
			setSmallThumbnail(getSrc().replace("/s1600", "/s200"));
		}
		entry.getForeignMarkup().stream()
				.filter(x -> "gphoto:timestamp".equals(x.getQualifiedName())).findAny()
				.ifPresent(x -> setShootingDate(new Date(Long.parseLong(x.getText()))));
	}
}
