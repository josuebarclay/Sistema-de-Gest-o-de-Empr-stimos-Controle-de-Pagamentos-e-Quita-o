package com.gerenciamento.grc.configuracao;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addFormatterForFieldType(LocalDate.class, new LocalDateFormatter());
//    }
//}
//
//class LocalDateFormatter implements org.springframework.format.Formatter<LocalDate> {
//
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//    @Override
//    public LocalDate parse(String text, java.util.Locale locale) {
//        return LocalDate.parse(text, formatter);
//    }
//
//    @Override
//    public String print(LocalDate object, java.util.Locale locale) {
//        return object.format(formatter);
//    }
}
