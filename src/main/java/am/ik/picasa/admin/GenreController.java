package am.ik.picasa.admin;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import am.ik.picasa.connect.Genre;
import am.ik.picasa.connect.GenreRepository;
import am.ik.picasa.google.PicasaUser;

@Controller
@RequestMapping("genres")
@Slf4j
public class GenreController {
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	PicasaUser picasaUser;

	@RequestMapping(method = RequestMethod.GET)
	String list(Model model, Principal principal) {
		model.addAttribute("genreForm", new GenreForm(genreRepository
				.findByUserIdOrderByGenreId(principal.getName()).stream().map(x -> {
					GenreForm.Item item = new GenreForm.Item();
					item.setGenre(x);
					return item;
				}).collect(Collectors.toList())));
		return "listGenres";
	}

	@RequestMapping(method = RequestMethod.POST, params = "update")
	@Transactional
	String update(@ModelAttribute("genreForm") GenreForm genreForm, Principal principal,
			RedirectAttributes attributes) {
		for (GenreForm.Item x : genreForm.getItems()) {
			// Authorize
			Genre g = genreRepository.findOne(x.getGenre().getGenreId());
			if (g == null || !Objects.equals(g.getUserId(), principal.getName())) {
				attributes.addFlashAttribute("error", "Illegal Operation!!.");
				log.warn("illegal operation detected: {}", picasaUser);
				return "redirect:/genres";
			}
			x.getGenre().setUserId(principal.getName());
		}

		List<Genre> toDelete = genreForm.getItems().stream().filter(x -> x.isRemove())
				.map(x -> x.getGenre()).collect(Collectors.toList());
		List<Genre> toUpdate = genreForm.getItems().stream().filter(x -> !x.isRemove())
				.map(x -> x.getGenre()).collect(Collectors.toList());
		try {
			genreRepository.deleteInBatch(toDelete);
		}
		catch (DataIntegrityViolationException e) {
			attributes.addFlashAttribute("error",
					"Genres used by photos are not allowed to remove! Please change genre(s) of photos in advance.");
		}
		genreRepository.save(toUpdate);
		attributes.addFlashAttribute("info", "Updated successfully!");
		return "redirect:/genres";
	}

	@RequestMapping(method = RequestMethod.POST, params = "create")
	String create(@RequestParam String genreName, Principal principal,
			RedirectAttributes attributes) {
		Genre genre = new Genre();
		genre.setGenreName(genreName);
		genre.setUserId(principal.getName());
		genreRepository.save(genre);
		attributes.addFlashAttribute("info", "Created successfully!");
		return "redirect:/genres";
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GenreForm implements Serializable {
		private List<Item> items;

		@Data
		public static class Item implements Serializable {
			private Genre genre;
			private boolean remove = false;
		}
	}
}
