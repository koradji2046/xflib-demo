package com.xflib.demo.configuration.springboot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import( value={XfLibImportSelector.class })
public class XflibImportSelectorConfig {
	
}
