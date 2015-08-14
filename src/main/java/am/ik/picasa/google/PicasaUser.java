package am.ik.picasa.google;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
@ToString
@Getter
public class PicasaUser {
	private String userId;
	private String email;
	private String displayName;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void init() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		OAuth2Authentication auth = OAuth2Authentication.class.cast(authentication);
		Map<String, Object> details = (Map<String, Object>) auth.getUserAuthentication()
				.getDetails();
		this.userId = auth.getName();
		this.email = (String) ((Map<String, Object>) ((List) details.get("emails"))
				.get(0)).get("value");
		this.displayName = (String) details.get("displayName");
		log.info("load {}", this);
	}
}
