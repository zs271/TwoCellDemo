package com.pervasid.rfid.experiment;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecUtils {
	public static void execShellCmd(String cmd) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[] { "/bin/bash", "-c", cmd });
            int exitValue = process.waitFor();
            System.out.println("exit value: " + exitValue);
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println("exec response: " + line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
	
	

}
