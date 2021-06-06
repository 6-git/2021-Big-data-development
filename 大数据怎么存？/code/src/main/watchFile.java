package main;

import java.io.*;
import java.util.*;

import com.amazonaws.services.s3.AmazonS3;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;

public class watchFile {
public static Path old_dir;
public static Path targetPath;
public static String new_path;
private static AmazonS3 s3;
private static String bucketName;
private static long partSize = 5 << 20;
private static int len=0;
watchFile(Path root,AmazonS3 the_s3, String the_bucket) throws IOException {
    this.s3 = the_s3;
    this.bucketName = the_bucket;
    this.targetPath = root;
    this.old_dir = root;
    this.len = old_dir.toString().length()+1;
	try(WatchService watchService = targetPath.getFileSystem().newWatchService()) {
            // �� targetPath �µ������ļ���(���)��ע�ᵽ watchService
            Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
            });
 
            WatchKey watchKey = null;
            while (true) {
                try {
                    watchKey = watchService.take();
 
                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    for (WatchEvent<?> event : watchEvents) {
                        @SuppressWarnings("unchecked")
						WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;
                        WatchEvent.Kind<Path> kind = watchEvent.kind();
                        Path watchable = ((Path) watchKey.watchable()).resolve(watchEvent.context());
                        // �ڼ������ļ��д�����ʱ��Ҫ����� path ע�ᵽ watchService ��
                        if(Files.isDirectory(watchable)){
                            if(kind == StandardWatchEventKinds.ENTRY_CREATE) {        
                            	System.out.println("�½��ļ��� = " + watchable);
                            	String s= "�½��ļ���";
                            	
                            	if(watchable.toString().length()>=(len+s.length())){
                            			if(watchable.toString().substring(len,len+s.length()).equalsIgnoreCase("�½��ļ���")) 
                            				System.out.println("������ "+watchable.toString().substring(len,len+s.length() ));
                            			else{
                                    		System.out.println("�����У�");
                                    		watchable.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                                                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                                    	}
                            	}
                            	else{
                            		System.out.println("�����У�");
                            		watchable.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                                            StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                            	}
                                
                            }
                        } else {
                        	new_path = watchable.toString();
                            File f = new File(new_path);
                        	String filename = new_path.replace('\\', '/').substring(len);
                            if(kind == StandardWatchEventKinds.ENTRY_CREATE) {

                            		if(f.length()<partSize) {
                						new uploadtos3(s3,bucketName,filename,new_path);
                					}
                					else {
                						new multipart_up(s3,bucketName,filename,new_path);
                					}
                            	
                                System.out.println("�½��ļ� = " + watchable);
 
                            } else if(StandardWatchEventKinds.ENTRY_MODIFY == kind){
                            	
                            		if(f.length()<partSize) {
                						new uploadtos3(s3,bucketName,filename,new_path);
                					}
                					else {
                						new multipart_up(s3,bucketName,filename,new_path);
                					}
                            		System.out.println("�޸��ļ� = " + watchable);
                            		
                            	} else if(StandardWatchEventKinds.ENTRY_DELETE == kind){
                            		new deletes3(s3,bucketName,filename);  
                            		
                            		System.out.println("ɾ���ļ� = " + watchable);
                            		
                               }
                            }
                        
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(watchKey != null){
                        watchKey.reset();
                    }
                }
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}