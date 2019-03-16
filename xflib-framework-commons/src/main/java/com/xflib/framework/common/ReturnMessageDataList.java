/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.common;

/**
 * Search结果集存入ReturnMessageInfo的类<br>
 *
 * History:<br> 
 *    . 1.0.0.20161011, com.qzdatasoft.koradji, Create<br>
 *
 */
@SuppressWarnings("serial")
public class ReturnMessageDataList implements java.io.Serializable {

	private int rowCount=0;
	private Object items=null;

	public ReturnMessageDataList(){
	}
	
	public ReturnMessageDataList(int rowcount, Object items){
		this.rowCount=rowcount;
		this.items=items;
	}
	
	public int getRowCount() {
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	public Object getItems() {
		return items;
	}
	public void setData(Object items) {
		this.items = items;
	}

}
