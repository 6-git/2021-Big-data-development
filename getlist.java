package main;

import java.io.File;
import java.util.List;

public class getlist {
getlist(String dir,List<String> wjpath)
{
	getfile(dir,wjpath);
}
public void getfile(String dir,List<String> wjpath)
{
	
	File file = new File(dir);	
	File[] fileList = file.listFiles();
	for (int i=0;i<fileList.length;i++)
	{
		  if(fileList[i].isDirectory())
		  {
			  getfile(fileList[i].toString(), wjpath);
		  }
		  else
			  wjpath.add(fileList[i].toString());
		
	}
}
}
