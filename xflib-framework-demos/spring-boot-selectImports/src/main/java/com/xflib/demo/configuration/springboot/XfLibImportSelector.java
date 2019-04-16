package com.xflib.demo.configuration.springboot;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class XfLibImportSelector  implements ImportSelector {
	
	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		return new String[]{com.xflib.demo.springboot.service.impl.XflibServiceImpl.class.getName()};
	}
	
}