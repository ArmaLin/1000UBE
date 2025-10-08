package com.dyaco.spirit_commercial.support;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/***
 * 執行命令列工具類
 *
 */
public class CommandUtil {

	public static final String TAG = "$$$CommandUtil$$$$";
	public static final String COMMAND_SH = "sh";
	public static final String COMMAND_LINE_END = "\n";
	public static final String COMMAND_EXIT = "exit\n";
	private static final boolean ISDEBUG = true;


	public static String execute(String commands) {
		StringBuilder results = new StringBuilder();
		int status = -1;

		Process process = null;
		BufferedReader successReader = null;
		BufferedReader errorReader = null;
		StringBuilder errorMsg = null;

		DataOutputStream dos = null;
		try {

			process = Runtime.getRuntime().exec(COMMAND_SH);
			dos = new DataOutputStream(process.getOutputStream());

				dos.write(commands.getBytes());
				dos.writeBytes(COMMAND_LINE_END);
				dos.flush();

			dos.writeBytes(COMMAND_EXIT);
			dos.flush();

			status = process.waitFor();

			errorMsg = new StringBuilder();
			successReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			errorReader = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			String lineStr;
			while ((lineStr = successReader.readLine()) != null) {
				results.append(lineStr);
			}
			while ((lineStr = errorReader.readLine()) != null) {
				errorMsg.append(lineStr);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dos != null) {
					dos.close();
				}
				if (successReader != null) {
					successReader.close();
				}
				if (errorReader != null) {
					errorReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (process != null) {
				process.destroy();
			}
		}
		debug(String.format(Locale.CHINA,
				"execute command end,errorMsg:%s,and status %d: ", errorMsg,
				status));
		return results.toString();
	}

	/**
	 * 執行單條命令
	 */
//	public static List<String> execute(String command) {
//		return execute(new String[] { command });
//	}
//
//	/**
//	 * 可執行多行命令（bat）
//	 */
//	public static List<String> execute(String[] commands) {
//		List<String> results = new ArrayList<>();
//		int status = -1;
//		if (commands == null || commands.length == 0) {
//			return null;
//		}
//		debug("execute command start : " + Arrays.toString(commands));
//		Process process = null;
//		BufferedReader successReader = null;
//		BufferedReader errorReader = null;
//		StringBuilder errorMsg = null;
//
//		DataOutputStream dos = null;
//		try {
//
//			process = Runtime.getRuntime().exec(COMMAND_SH);
//			dos = new DataOutputStream(process.getOutputStream());
//			for (String command : commands) {
//				if (command == null) {
//					continue;
//				}
//				dos.write(command.getBytes());
//				dos.writeBytes(COMMAND_LINE_END);
//				dos.flush();
//			}
//			dos.writeBytes(COMMAND_EXIT);
//			dos.flush();
//
//			status = process.waitFor();
//
//			errorMsg = new StringBuilder();
//			successReader = new BufferedReader(new InputStreamReader(
//					process.getInputStream()));
//			errorReader = new BufferedReader(new InputStreamReader(
//					process.getErrorStream()));
//			String lineStr;
//			while ((lineStr = successReader.readLine()) != null) {
//				results.add(lineStr);
//				debug(" command line item : " + lineStr);
//			}
//			while ((lineStr = errorReader.readLine()) != null) {
//				errorMsg.append(lineStr);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (dos != null) {
//					dos.close();
//				}
//				if (successReader != null) {
//					successReader.close();
//				}
//				if (errorReader != null) {
//					errorReader.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			if (process != null) {
//				process.destroy();
//			}
//		}
//		debug(String.format(Locale.CHINA,
//				"execute command end,errorMsg:%s,and status %d: ", errorMsg,
//				status));
//		return results;
//	}

	/**
	 * DEBUG LOG
	 */
	private static void debug(String message) {
		if (ISDEBUG) {
			Log.d(TAG, message);
		}
	}


	//這段程式碼會檢查裝置上是否安裝了 su，如果安裝了，則傳回 true。
	public static boolean isRootAvailable() {
		boolean isRooted = false;
		try {
			Process process = Runtime.getRuntime().exec("which su");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = reader.readLine();
			isRooted = (output != null && !output.isEmpty());
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isRooted;
	}


	public static String executeAsRoot(String command) {
		StringBuilder output = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec("su"); // 取得 Root 權限
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();

			process.waitFor();

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}

			os.close();
			reader.close();
		} catch (Exception e) {
			output.append("Error: ").append(e.getMessage());
		}
		return output.toString();
	}

}