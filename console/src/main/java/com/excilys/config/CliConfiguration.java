package com.excilys.config;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@Configuration
@ComponentScan(basePackages = { "com.excilys.ui" })
public class CliConfiguration extends ServiceConfig{

    /**
     * Creer un localeResolver a default en anglais.
     * @return SessionLocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.FRENCH);
        return cookieLocaleResolver;
    }
}
