package am.ik.picasa.google;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.rometools.rome.feed.atom.Entry;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PicasaAlbum extends PicasaEntryBase {
	private String albumId;

	public PicasaAlbum(Entry entry) {
		super(entry);
		setAlbumId(entry.getId().substring(entry.getId().lastIndexOf('/') + 1));
	}
}
