/**
 * 
 */
package com.higuerasprojects.spendlessoutback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;

import com.higuerasprojects.spendlessoutback.controller.MainController;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Ruhimo
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_12).select()
				.apis(RequestHandlerSelectors.basePackage(MainController.class.getPackage().getName()))
				.paths(PathSelectors.any()).build().apiInfo(getApiInfo());
	}

	@Bean
	public LinkDiscoverers discoverers() {
		List<LinkDiscoverer> plugins = new ArrayList<>();
		plugins.add(new CollectionJsonLinkDiscoverer());
		return new LinkDiscoverers(SimplePluginRegistry.create(plugins));

	}

	private ApiInfo getApiInfo() {
		return new ApiInfo("OCIO POR LO JUSTO - API", "The services to work with th concerts API are here.", "1.0", "",
				new Contact("RHigueras", "https://github.com/al315555", "email@domainemail.com"), "License", "https://github.com/al315555",
				Collections.emptyList());
	}
}
