package com.pervasid.rfid.experiment;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.*;

public class sql_commands {
	public static void mysqldump(PervasidServerSettings settings,String file) {

		
        String executeCmd1 = "mysqldump -h " + settings.db_location+
        		" --user=" + settings.db_username +
        		" --password=" + settings.db_password +
        		" --databases "+settings.db_name;
        
        String executeCmd2 = "mysql -h " + settings.db_location+
        		" --user=" + settings.db_username +
        		" --password=" + settings.db_password +
        		" -B -D "+settings.db_name + " -e \"SELECT * FROM tag_reads_simple\";";
        
        String executeCmd3 = "mysql -h " + settings.db_location+
        		" --user=" + settings.db_username +
        		" --password=" + settings.db_password +
        		" -B -D "+settings.db_name + " -e \"SELECT * FROM reference_tags\";";
     //   System.out.printf(executeCmd2+"\n");
        Process runtimeProcess;
        FileOutputStream fos = null;
        DatabaseWorker dw = null;
        try {
			dw = new DatabaseWorker(settings);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		try {
			fos = new FileOutputStream(settings.data_location+file+".zip");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
        try {
        	ZipOutputStream zos =  new ZipOutputStream(new BufferedOutputStream(fos));
        	ZipEntry ze= new ZipEntry(file+".sql");
     		zos.putNextEntry(ze);
        	zos=dump_to_zip(executeCmd1, zos,file+".sql");
        	
        	zos.closeEntry();
        	ze= new ZipEntry(file+"_reference.csv");
     		zos.putNextEntry(ze);
 
        	 zos = dw.ReferenceTags2CSV(zos);
        	 zos.closeEntry();
        	 
        	 ze= new ZipEntry(file+"_tag_reads.csv");
      		zos.putNextEntry(ze);
  
         	 zos = dw.TagReadsSimple2CSV(zos);
         	 zos.closeEntry();
        	//dump_to_zip(executeCmd2, zos,file+"_tags_reads.csv");
        	// zos.flush();
        	//dump_to_zip(executeCmd3, zos,file+"_reference.csv");
            zos.flush();
            zos.close();   
 
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return;
 

    }
public static ZipOutputStream dump_to_zip(String executeCmd, ZipOutputStream zos,String zip_name) {

        Process runtimeProcess;
       
        try {
        	
 
            runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            
         // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(runtimeProcess.getErrorStream(), "ERR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(runtimeProcess.getInputStream(), "OUT",zos);
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???


            int processComplete = runtimeProcess.waitFor();
            System.out.println("Process exitValue: " + processComplete);
            outputGobbler.join();
            zos.flush(); 
 
            if (processComplete == 0) {
                
                return zos;
            } else {
                System.out.println("Could not create the backup");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return zos;
 

    }

}
