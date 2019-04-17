package com.xflib.demo.configuration.feign.service;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class XfLibImportSelector  implements ImportSelector {
	
	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		return new String[]{com.xflib.demo.feign.service.service.impl.XflibServiceImpl.class.getName()};
	}
	
}