package com.excilys.exception;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHelper implements ApplicationContextAware {
    
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * Default constructor.
     */
    public ExceptionHelper() {
      
    }
    
    public ExceptionHelper(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * Recupere le message grâce au code présent dans les fichiers de properties.
     * @param code
     *            Code du message
     * @param params
     *            Parametre du message
     * @return Message recuperée
     */
    public static String getMessageByCode(final String code, Object... params) {
        return context.getBean(MessageSource.class).getMessage(code, params, LocaleContextHolder.getLocale());

    }

}
