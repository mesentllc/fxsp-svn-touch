package com.fedex.smartpost.utilities.svntouch;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.wc.ISVNPropertyHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SvnTouch {
    private File[] files;

    private File[] extractNamesFromText(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<File> fileList = new ArrayList<File>();

        while (br.ready()) {
            String filepath = br.readLine().trim();
            if (filepath.length() > 1) {
                fileList.add(new File(filepath));
            }
        }
        return (File[])fileList.toArray();
    }
    
    private boolean validateCommandLine(String[] params) {
		if ((params == null) || (params.length != 2)) {
            return false;
        }
        try {
            if ("-f".equals(params[0])) {
                files = extractNamesFromText(params[1]);
                return true;
            }
            if ("-d".equals(params[0])) {
                File fileHandle = new File(params[1]);
                files = fileHandle.listFiles();
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }
    
    private void mainProcess(String[] params) throws Exception {
		if (validateCommandLine(params)) {
			SVNWCClient wcClient = SVNClientManager.newInstance().getWCClient();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
			
			for (File file : files) {
				if (!file.getName().startsWith(".")) {
					try {
						wcClient.doSetProperty(file, "touched", 
									   		   SVNPropertyValue.create(sdf.format(Calendar.getInstance().getTime())), 
									   		   true, SVNDepth.INFINITY, ISVNPropertyHandler.NULL, null);
					} 
					catch (SVNException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Completed.");
		}
		else {
			System.err.println("\tUsage:  svnTouch [-d | -f] <base path>");
            System.err.println("\tEx: svnTouch -d c:\\projects\\fxsp-svn-touch");
			System.err.println("\tWill \"touch\" all files under the c:\\projects\\fxsp-svn-touch sub-directory.");
			System.err.println("\tEx: svnTouch -f c:\\filelist.txt");
            System.err.println("\tWill \"touch\" the files found in the c:\\filelist.txt.");
		}
    }
    
	public static void main(String[] params) {
        SvnTouch svnTouch = new SvnTouch();
        try {
            svnTouch.mainProcess(params);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
	}
}
