package com.xflib.demo.configuration.feign.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import( value={XfLibImportSelector.class })
public class XflibImportSelectorConfig {
	
}
