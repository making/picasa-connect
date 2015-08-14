package am.ik.picasa.admin;

import java.io.Serializable;
import java.security.Principal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import am.ik.picasa.connect.Genre;
import am.ik.picasa.connect.GenreRepository;
import am.ik.picasa.connect.Photo;
import am.ik.picasa.connect.PhotoRepository;
import am.ik.picasa.google.PicasaAlbum;
import am.ik.picasa.google.PicasaPhoto;
import am.ik.picasa.google.PicasaService;

@Controller
@SessionAttributes({ "photoMap", "chosen" })
public class AlbumController {
	@Autowired
	PicasaService picasaService;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	PhotoRepository photoRepository;

	@ModelAttribute("photoMap")
	Map<String, PicasaPhoto> setupPhotoMap() {
		return new HashMap<>();
	}

	@ModelAttribute("chosen")
	ChosenPhoto setupChosen() {
		return new ChosenPhoto();
	}

	List<Genre> genres(Principal principal) {
		if (genreRepository.countByUserId(principal.getName()) == 0) {
			genreRepository.save(
					Arrays.asList(new Genre(null, "Landscapes", principal.getName()),
							new Genre(null, "People", principal.getName()),
							new Genre(null, "Animals", principal.getName()),
							new Genre(null, "Objects", principal.getName())));
		}
		return genreRepository.findByUserIdOrderByGenreId(principal.getName());
	}

	@RequestMapping(value = "/albums")
	String albums(Model model) {
		List<PicasaAlbum> albums = picasaService.findAlbums();
		model.addAttribute("albums", albums);
		return "albums";
	}

	@RequestMapping(value = "/albums/{albumId}")
	String album(@PathVariable String albumId, Model model,
			@ModelAttribute("photoMap") Map<String, PicasaPhoto> photoMap) {
		List<PicasaPhoto> photos = picasaService.findPhotosByAlbumId(albumId);
		photoMap.putAll(photos.stream()
				.collect(Collectors.toMap(PicasaPhoto::getId, Function.identity())));
		model.addAttribute("photos", photos);
		return "album";
	}

	@ResponseBody
	@RequestMapping(value = "/photos", method = RequestMethod.POST)
	String choosePhoto(@RequestParam String photoId,
			@ModelAttribute("photoMap") Map<String, PicasaPhoto> photoMap,
			@ModelAttribute("chosen") ChosenPhoto chosen) {
		PicasaPhoto photo = photoMap.get(photoId);
		chosen.getItems()
				.add(ChosenPhoto.Item.builder().picasaPhoto(photo).title(photo.getTitle())
						.published(photo.getPublished()).updated(photo.getUpdated())
						.build());
		return "OK";
	}

	@RequestMapping(value = "register", params = "form")
	String registerForm(Model model, Principal principal) {
		model.addAttribute("genres", genres(principal));
		return "registerForm";
	}

	@RequestMapping(value = "register", params = "save")
	String saveForm(@ModelAttribute("chosen") ChosenPhoto chosen) {
		Set<ChosenPhoto.Item> items = chosen.getItems().stream()
				.filter(x -> !x.isRemove()).collect(Collectors.toSet());
		chosen.setItems(items);
		if (chosen.isUseShootingDate()) {
			chosen.getItems().forEach(x -> {
				x.setPublished(x.getPicasaPhoto().getShootingDate());
				x.setUpdated(x.getPicasaPhoto().getShootingDate());
			});
		}
		return "redirect:/register?form";
	}

	@RequestMapping(value = "register", params = "register")
	String register(@ModelAttribute("chosen") ChosenPhoto chosen, Principal principal,
			SessionStatus sessionStatus, RedirectAttributes attributes) {
		if (chosen.isUseShootingDate()) {
			chosen.getItems().forEach(x -> {
				x.setPublished(x.getPicasaPhoto().getShootingDate());
				x.setUpdated(x.getPicasaPhoto().getShootingDate());
			});
		}
		List<Photo> photos = chosen.getItems().stream().filter(x -> !x.isRemove())
				.map(x -> {
					Photo photo = new Photo();
					photo.setUrl(x.getPicasaPhoto().getSrc());
					photo.setTitle(x.getTitle());
					photo.setComment(x.getComment());
					photo.setGenre(new Genre(x.getGenreId(), null, null));
					photo.setPublished(x.getPublished());
					photo.setUpdated(x.getUpdated());
					photo.setUserId(principal.getName());
					return photo;
				}).collect(Collectors.toList());
		photoRepository.save(photos);
		sessionStatus.setComplete();
		attributes.addFlashAttribute("info", "Registered successfully!");
		return "redirect:/registeredPhotos";
	}

	@ExceptionHandler(HttpClientErrorException.class)
	ModelAndView handleClientError() {
		return new ModelAndView("albums").addObject("warning", "No photo in Picasa?");
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChosenPhoto implements Serializable {
		private Set<Item> items = new LinkedHashSet<>();
		private boolean useShootingDate = false;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@EqualsAndHashCode(of = "picasaPhoto")
		public static class Item {
			private PicasaPhoto picasaPhoto;
			private String title;
			private String comment;
			private Integer genreId;
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:ss")
			private Date published;
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:ss")
			private Date updated;
			private boolean remove = false;
		}
	}
}
