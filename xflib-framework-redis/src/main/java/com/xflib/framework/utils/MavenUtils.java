/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * 找出maven下载出错的文件 Created by xiejx618 on 14-4-1.
 */
public class MavenUtils {
	private static MessageDigest messageDigest = null;
	static {
		try {
			messageDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };
	private static final int BUFFER_SIZE = 4096;

	/**
	 * 以16进制字符串形式返回数据的sha1
	 * 
	 * @param data
	 *            data
	 * @return str
	 */
	public static String digestData(byte[] data) {
		messageDigest.update(data);
		data = messageDigest.digest();
		int len = data.length;
		StringBuilder buf = new StringBuilder(len * 2);
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(data[j] >> 4) & 0x0f]).append(HEX_DIGITS[data[j] & 0x0f]);
		}
		return buf.toString();
	}

	/**
	 * 根据文件与编码以String形式返回文件的数据
	 * 
	 * @param file
	 *            文件名
	 * @param charset
	 *            字符集
	 * @return str
	 */
	public static String getFileString(File file, String charset) {
		InputStreamReader reader = null;
		StringBuilder out = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file), charset);
			out = new StringBuilder();
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = reader.read(buffer)) != -1) {
				out.append(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return out.toString();
	}

	/**
	 * 根据文件以byte[]形式返回文件的数据
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getFileData(File file) {
		FileInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new ByteArrayOutputStream(BUFFER_SIZE);
			@SuppressWarnings("unused")
			int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return out.toByteArray();
	}

	/**
	 * 根据目录dir,递归所有的文件按handle方法处理
	 * 
	 * @param dir
	 */
	public static final void showAllFiles(File dir) {
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory()) {
				showAllFiles(fs[i]);
			}
			handle(fs[i]);
		}
	}

	/**
	 * 凡是以.sha1的文件结尾的文件,先将文件名去掉.sha1找到文件sourcefile,获取sourcefile文件的真实的sha1和从这个.sha1文件获取sha1,
	 * 进行比照,如果不匹配,输出实际的sha1和期望的sha1
	 * 
	 * @param file
	 */
	public static final void handle(File file) {
		String filename = file.getName();
		if (filename.endsWith(".sha1")) {
			String sourcename = filename.substring(0, filename.lastIndexOf('.'));
			File sourcefile = new File(file.getParent(), sourcename);
			byte[] sourcedata = getFileData(sourcefile);
			String sha1Real = digestData(sourcedata);
			String content = getFileString(file, "UTF-8");
			String sha1Check = content.split(" ")[0].trim();
			if (!sha1Real.equalsIgnoreCase(sha1Check)) {
				System.out.println("rd /q /s " + sourcefile.getParent());
				// System.out.println(sourcefile.getAbsolutePath());
				// System.out.println("===验证SHA1:"+sha1Check);
				// System.out.println("===实际SHA1:"+sha1Real);
			}
		}
	}

	private static List<String> vars = new ArrayList<>(Arrays.asList("com.qzdatasoft.cloud.version",
			"com.qzdatasoft.supports.version", "com.qzdatasoft.framework.version", "com.qzdatasoft.platform.version",
			"com.qzdatasoft.platform.job.version", "com.qzdatasoft.platform.log.version",
			"com.qzdatasoft.platform.jczy.version", "com.qzdatasoft.platform.report.version",
			"com.qzdatasoft.platform.workflow.version", "com.qzdatasoft.platform.data-center.version",
			"com.qzdatasoft.platform.message-center.version", "com.qzdatasoft.platform.resource-service.version"));
	private static List<String> files = new ArrayList<>(Arrays.asList(
			"/qzdatasoft-cloud/qzdatasoft-cloud-build/pom.xml", "/qzdatasoft-cloud-dependencies/pom.xml",
			"/qzdatasoft-framework/parent/pom.xml", "/qzdatasoft-framework-dependencies/pom.xml",
			"/qzdatasoft-platform/qzdatasoft-platform-build/pom.xml",
			"/qzdatasoft-platform/qzdatasoft-platform-workflow/qzdatasoft-platform-workflow-build/pom.xml",
			"/qzdatasoft-platform-dependencies/pom.xml",
			"/qzdatasoft-platform-dependencies/qzdatasoft-platform-dependencies-internal/pom.xml",
			"/qzdatasoft-starter/qzdatasoft-starter-framework-parent/pom.xml",
			"/qzdatasoft-starter/qzdatasoft-starter-platform-parent/pom.xml",
			"/qzdatasoft-supports/qzdatasoft-supports-build/pom.xml", "/qzdatasoft-supports-dependencies/pom.xml"));

	public static String ReadVersion(String filePath) throws IOException {
		String result = "";//G:\SVN\code-dev\旧版本号.txt
		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			String tempString = "";
			reader = new BufferedReader(new FileReader(file));
			if ((tempString = reader.readLine()) != null)
				result = tempString;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return result;
	}

	public static void upgradeVersion(String workDir) {

		String oldVersion = "0.0.1-SNAPSHOT", newVersion = "0.0.1";
		try {
			newVersion = ReadVersion(workDir + "/新版本号.txt");
			oldVersion = ReadVersion(workDir + "/旧版本号.txt");
			for (String filePath : files) {
				filePath = String.format("%s/work%s", workDir, filePath);
				File file = new File(filePath);
				File out = new File(filePath + ".tmp");
				BufferedReader reader = null;
				BufferedWriter writer = null;

				try {
//					System.out.println(filePath);
					reader = new BufferedReader(new FileReader(file));
					writer = new BufferedWriter(new FileWriter(out, true));
					String tempString = null;
					int line = 1;
					while ((tempString = reader.readLine()) != null) {
						// 显示行号
						line++;
						// 分析替换变量
						for (String s : vars) {
							String regex = String.format("<%s>%s</%s>", s, oldVersion, s);
							String replacement = String.format("<%s>%s</%s>", s, newVersion, s);
							if (tempString.contains(s))
								tempString = tempString.replaceAll(regex, replacement);
						}
//						System.out.println("line " + line + ": " + tempString);
						writer.append(tempString);
						writer.newLine();
					}
					writer.flush();
					writer.close();
					reader.close();
					FileUtils.copyFile(out, file, true);
					org.apache.tomcat.util.http.fileupload.FileUtils.forceDelete(out);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e1) {
						}
					}
					if (writer != null) {
						try {
							reader.close();
						} catch (IOException e1) {
						}
					}
				}
			}
		} catch (Exception e) {
//			System.out.println("异常啦！");
		}
	}

	public static void main(String[] args) {  
//		String workdir=System.getProperty("user.dir"); 
//		System.out.println("workdir: "+workdir);
//    	upgradeVersion(workdir);
        showAllFiles(new File("C:/Users/Administrator/.m2"));  
      System.out.println("the end");  
    }
}