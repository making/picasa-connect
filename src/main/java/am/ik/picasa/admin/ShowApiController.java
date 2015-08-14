package am.ik.picasa.admin;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShowApiController {

	@RequestMapping("showApi")
	String showApi(Principal principal,
			@Value("#{request.requestURL}") StringBuffer requestUrl, Model model) {
		model.addAttribute("baseUri", requestUrl.append("api/users/")
				.append(principal.getName()).toString().replace("showApi", ""));
		return "showApi";
	}
}
