package com.xflib.demo.command.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xflib.demo.command.pojo.Project;
import com.xflib.demo.command.service.ProjectService;
import com.xflib.framework.common.BaseException;
import com.xflib.framework.common.Constants;
import com.xflib.framework.common.ReturnMessageInfo;

@RestController
@RequestMapping(path = "/project")
public class ProjectController {

	private static Logger log = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	ProjectService service;

	@PutMapping("/")
	public ReturnMessageInfo insert(List<Project> projects) {
		ReturnMessageInfo result = new ReturnMessageInfo();

		try {
			result.setData(service.insert(projects));
			result.setErrorCode(Constants.ERROE_CODE_SUCCESS);
		} catch (BaseException e) {
			result.setErrorCode(e.getMessage());
			log.error(e.getMessage(), e.getCause());
		} catch (Exception e) {
			log.error(Constants.ERROE_CODE_DEFAULT, e);
		}

		return result;
	}

	@PostMapping("/")
	public ReturnMessageInfo update(List<Project> projects) {
		ReturnMessageInfo result = new ReturnMessageInfo();

		try {
			result.setData(service.update(projects));
			result.setErrorCode(Constants.ERROE_CODE_SUCCESS);
		} catch (BaseException e) {
			result.setErrorCode(e.getMessage());
			log.error(e.getMessage(), e.getCause());
		} catch (Exception e) {
			log.error(Constants.ERROE_CODE_DEFAULT, e);
		}

		return result;
	}

	@DeleteMapping("/")
	public ReturnMessageInfo delete(List<Project> projects) {
		ReturnMessageInfo result = new ReturnMessageInfo();

		try {
			result.setData(service.update(projects));
			result.setErrorCode(Constants.ERROE_CODE_SUCCESS);
		} catch (BaseException e) {
			result.setErrorCode(e.getMessage());
			log.error(e.getMessage(), e.getCause());
		} catch (Exception e) {
			log.error(Constants.ERROE_CODE_DEFAULT, e);
		}

		return result;
	}

//	public Object exexute(Map<?, ?> args) {
//		Object exps = super.execute(args);
//		ExpressionParser parser = new SpelExpressionParser();
//		EvaluationContext context = new StandardEvaluationContext();
//		if (exps instanceof Map<?,?>) {
//			int i=0;
//			for(Map.Entry<?, ?> exp :((Map<?,?>) exps).entrySet() ){
//				context.setVariable((String) exp.getKey(), exp.getValue());
//			}
//		} else {
//			throw new Exception();
//		}
//		Expression expression = parser.parseExpression(this.getExpression(), new TemplateParserContext());
//		return expression.getValue(context, String.class);
//	}
//
//	private String getExpression() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	public static void main(String[] args) {
//		String greetingExp = "Hello, #{#user}";
////		String greetingExp = "Hello, #user";
//		ExpressionParser parser = new SpelExpressionParser();
//		EvaluationContext context = new StandardEvaluationContext();
//		context.setVariable("user", "Gangyou");
//		context.setVariable("Gangyou", "xxx");
//
//		Expression expression = parser.parseExpression(greetingExp, new TemplateParserContext());
//		System.out.println(expression.getValue(context, String.class));
//		
//		List<String> list=new ArrayList<>();
//		list.parallelStream().forEach((s)->{
//			System.out.println(s);
//		});
//	}
}
