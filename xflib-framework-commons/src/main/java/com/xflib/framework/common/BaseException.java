/**Copyright: Copyright (c) 2019, koradji*/
package com.xflib.framework.common;

import org.slf4j.event.Level;

/**
 * 通用错误类
 *
 * History:<br> 
 *    . v1.0.0.20190415, Koradji, Create<br>
 */
public class BaseException extends RuntimeException{
    static final long serialVersionUID = -3387516993124229941L;

    private String [] messageParam;                     // 消息参数，不做任何处理直接把参数返回    
    private Class<?> source=NoDefine.class;        // Exception来源
    private Level level=Level.ERROR;                     // Exception Level, 借助org.slf4j.event.Level作为ErrorMessage二次加工的路由关键字

    // 这一组方法用于抛出已经处理过的错误(错误是可控的)，仅用于统一传递错误信息，因此提供了改变level的参数(默认为Level.INFO)
    public BaseException(Class<?> source, String message) {
        super(message);
        this.source=source;
        this.level=Level.INFO;
    }
    public BaseException(Class<?> source, Level level, String message) {
        this(source, message);
        this.level=level;
    }

    public BaseException(Class<?> source, String message, String ...args) {
        this(source, message);
        this.messageParam = args;
    }
    public BaseException(Class<?> source, Level level, String message, String ...args) {
        this(source, level, message);
        this.messageParam = args;
    }

    // 这一组方法用于抛出已经拦截但未处理过的错误(错误未得到控制)，因此提供了自定义消息体和出错原因，Level默认值为Level.ERROR
    public BaseException(Class<?> source, String message, Throwable cause) {
        super(message, cause);
        this.source=source;
	}

    public BaseException(Class<?> source, String message, Throwable cause, String ...args) {
        this(source,message, cause);
        messageParam = args;
    }

	public String[] getMessageParam() {
		return messageParam;
	}

	public void setMessageParam(String[] messageParam) {
		this.messageParam = messageParam;
	}

	public Class<?> getSource() {
		return source;
	}

	public void setSource(Class<?> source) {
		this.source = source;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
	/**
	 * 默认的Exception来源
	 * @author koradji
	 *
	 */
	private class NoDefine {}

}
