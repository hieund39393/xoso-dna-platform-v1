package com.xoso.infrastructure.security.support;

import org.apache.commons.lang3.StringUtils;
import com.xoso.infrastructure.security.FintechDomain;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AuthorityParser implements ApplicationContextAware {

	private ApplicationContext context;

	private final String SEPARATOR = ":";
	private final String URL_SEPARATOR = ".";

	public String parse(String url, String method, FintechDomain domain) {
		String eUrl = evaludateUrl(url);
		return StringUtils.joinWith(SEPARATOR, eUrl, method.toUpperCase(), domain.name().toLowerCase());
	}

	private String evaludateUrl(String url) {
		Assert.notNull(url, "url must be not null");
		if (StringUtils.isNotBlank(url)) {
			return url.replaceAll("/", URL_SEPARATOR).substring(1, url.length()).toLowerCase();
		} else {
			throw new IllegalArgumentException("url must be not blank");
		}
	}

	public FintechDomain getDomain() {
		String id = context.getId();
		if (id.contains(":")) {
			String[] domains = org.springframework.util.StringUtils.split(id, ":");
			return FintechDomain.valueOf(domains[0]);
		}
		return FintechDomain.valueOf(id);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
