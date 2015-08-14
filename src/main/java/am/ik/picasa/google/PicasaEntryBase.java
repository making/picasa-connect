package am.ik.picasa.google;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import com.rometools.rome.feed.atom.Entry;

@Data
public class PicasaEntryBase implements Serializable {
	private String id;
	private String title;
	private Date published;
	private Date updated;

	public PicasaEntryBase(Entry entry) {
		setId(entry.getId());
		setTitle(entry.getTitle());
		setPublished(entry.getPublished());
		setUpdated(entry.getUpdated());
	}
}
