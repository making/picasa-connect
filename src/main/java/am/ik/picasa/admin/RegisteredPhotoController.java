package am.ik.picasa.admin;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import am.ik.picasa.connect.GenreRepository;
import am.ik.picasa.connect.Photo;
import am.ik.picasa.connect.PhotoRepository;
import am.ik.picasa.google.PicasaUser;

@Controller
@RequestMapping("registeredPhotos")
@Slf4j
public class RegisteredPhotoController {
	@Autowired
	PhotoRepository photoRepository;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	PicasaUser picasaUser;

	@RequestMapping(method = RequestMethod.GET)
	String list(@PageableDefault(size = 100_000) Pageable pageable, Model model,
			Principal principal) {
		Page<Photo> photos = photoRepository
				.findByUserIdOrderByUpdatedDesc(principal.getName(), pageable);
		model.addAttribute("photosForm",
				new PhotosForm(photos.getContent().stream().map(x -> {
					PhotosForm.Item item = new PhotosForm.Item();
					item.setPhoto(x);
					return item;
				}).collect(Collectors.toList())));
		model.addAttribute("genres",
				genreRepository.findByUserIdOrderByGenreId(principal.getName()));
		return "listRegisteredPhotos";
	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	String update(@ModelAttribute("photosForm") PhotosForm photosForm,
			Principal principal, RedirectAttributes attributes) {
		List<Photo> toDelete = new ArrayList<>();
		List<Photo> toUpdate = new ArrayList<>();
		for (PhotosForm.Item item : photosForm.getItems()) {
			// Authorize
			Photo photo = photoRepository.findOne(item.getPhoto().getPhotoId());
			if (photo == null
					|| !Objects.equals(photo.getUserId(), principal.getName())) {
				attributes.addFlashAttribute("error", "Illegal Operation!!.");
				log.warn("illegal operation detected: {}", picasaUser);
				return "redirect:/registeredPhotos";
			}
			item.getPhoto().setUserId(principal.getName());
			BeanUtils.copyProperties(item.getPhoto(), photo);
			if (item.isRemove()) {
				toDelete.add(photo);
			}
			else {
				toUpdate.add(photo);
			}
		}
		photoRepository.delete(toDelete);
		photoRepository.save(toUpdate);
		attributes.addFlashAttribute("info", "Updated successfully!");
		return "redirect:/registeredPhotos";
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PhotosForm implements Serializable {
		private List<Item> items;

		@Data
		public static class Item implements Serializable {
			private Photo photo;
			private boolean remove = false;
		}
	}
}
