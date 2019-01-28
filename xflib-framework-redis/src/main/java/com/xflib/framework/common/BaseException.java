/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.common;

/**
 * 通用错误类<br>
 * 与returnMessageInfo.setErrorCode(String)配合使用
 * 本类的message是国际化错误代码，是必须传入的初始化参数，原来的Exception对象可以通过
 * BaseException(String errorCode, Throwable cause)当做cause传入
 * <br>
 * History:<br> 
 *    . v1.0.0.20170104, com.qzdatasoft.Koradji, Create<br>
 *    . 20170531, wangshi, 修改带参数的异常处理方式，【messageParam/消息参数】，不做任何处理直接把参数返回，参数的处理在ReturnMessageInfo.setErrorCode(BaseException e)里完成    <br>
 */
public class BaseException extends RuntimeException{
    static final long serialVersionUID = -3387516993124229941L;

    private String [] messageParam; // 消息参数，不做任何处理直接把参数返回    

	/**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * 对应Control里的异常配套处理方法：r.setErrorCode(e)/ReturnMessageInfo.setErrorCode(BaseException e)
     * @param message	消息内容
     * @param args,异常消息参数
     */
    public BaseException(String message, String ...args) {
        super(message);
        messageParam = args;
    }
    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
	}

    public BaseException(String message, Throwable cause, String ...args) {
        super(message, cause);
        messageParam = args;
    }

//    /**
//     * Constructs a new exception with the specified cause and a detail
//     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
//     * typically contains the class and detail message of <tt>cause</tt>).
//     * This constructor is useful for exceptions that are little more than
//     * wrappers for other throwables (for example, {@link
//     * java.security.PrivilegedActionException}).
//     *
//     * @param  cause the cause (which is saved for later retrieval by the
//     *         {@link #getCause()} method).  (A <tt>null</tt> value is
//     *         permitted, and indicates that the cause is nonexistent or
//     *         unknown.)
//     * @since  1.4
//     */
//    public BaseException(Throwable cause) {
//        super(cause);
//    }

    /**
     * Constructs a new exception with the specified detail message,
     * cause, suppression enabled or disabled, and writable stack
     * trace enabled or disabled.
     *
     * @param  message the detail message.
     * @param cause the cause.  (A {@code null} value is permitted,
     * and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled
     *                          or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     * @since 1.7
     */
    protected BaseException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
	}

	
    

	public String[] getMessageParam() {
		return messageParam;
	}

	public void setMessageParam(String[] messageParam) {
		this.messageParam = messageParam;
	}

}
