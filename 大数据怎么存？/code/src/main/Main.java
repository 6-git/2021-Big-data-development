package main;

import java.io.*; 
import java.util.*;
import java.sql.Array;
import   java.sql.Timestamp;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.codec.digest.DigestUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;


public class Main {
	private final static String bucketName = "work1";		//ָ����Ͱ����
	private final static String accessKey = "A770D324DC705AED7E34";
	private final static String secretKey = "WzdENDYwM0ZCMjM5NEM0QzdDMTM4RTQ2REZFOEYz";
	private final static String serviceEndpoint = "http://scut.depts.bingosoft.net:29997";
	private final static String signingRegion = "";
	//����ָ���ֿ鴫����ļ���С������partSizeʱ���зֿ�
	private static long partSize = 5 << 20;				
	private final static int bias = 30;
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		final ClientConfiguration ccfg = new ClientConfiguration().withUseExpectContinue(false);

		final EndpointConfiguration endpoint = new EndpointConfiguration(serviceEndpoint, signingRegion);
		
		//�ֱ�洢S3�ļ���keyname, ��С������޸����ڣ�md5��ֵ
		List<String> s3_file = new ArrayList<String>();
	    List<Long> s3_size = new ArrayList<Long>();
	    List<Long> s3_date = new ArrayList<Long>();
	    List<String> s3_md5 = new ArrayList<String>();
	    
	    String filepath = "D:\\eclipse2020-workplace\\homework1\\local_file";
		int len = filepath.length();
		File file = new File(filepath);			//File���Ϳ������ļ�Ҳ�������ļ���
		File[] fileList = file.listFiles();		//����Ŀ¼�µ������ļ�������һ��File���͵�������
		
		//�ֱ�洢�����ļ���·�����ļ�������С������޸����ڣ�����õ���md5ֵ
		List<String> wjpath = new ArrayList<String>();//�½�һ���ļ����ϴ洢�ļ�·��
		List<String> local_file = new ArrayList<String>();
		List<Long> local_size = new ArrayList<Long>();
		List<Long> local_date = new ArrayList<Long>();
		List<String> local_md5 = new ArrayList<String>();
		
		
	
		//operation����ͬ���ļ�ʱ�Ĳ�������
		int opera = -1, upload = 0, download=1;
		int s3_num =0;		//s3�ļ��ĸ���
		int folder_num = 0;		//s3�ļ��еĸ�����s3���޲�νṹ�����ļ�����Ϊ�����ļ���ǰ׺����
		int local_num =0;		//�����ļ�����
		List<Integer> local_tagl = new ArrayList<Integer>();		//��Ǳ����ļ��Ƿ��Ѿ��ж�Ϊ��Ҫ�ϴ�0������1
		List<Integer> s3_tagl = new ArrayList<Integer>();			//�������ظ��ǵ����ļ���-1���������κβ���
		
		// Find out the files of S3
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withClientConfiguration(ccfg)
				.withEndpointConfiguration(endpoint).withPathStyleAccessEnabled(true).build();
		try {

			System.out.println("Listing objects of S3:");

			// maxKeys is set to 100 to demonstrate the use of
			// ListObjectsV2Result.getNextContinuationToken()
			ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(100);
			ListObjectsV2Result result;
        
            result = s3.listObjectsV2(req);


            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                System.out.printf("\nfileName: %s \n", objectSummary.getKey());
                s3_file.add(objectSummary.getKey());
                String key_last = objectSummary.getKey().toString();
                if(key_last.substring(key_last.length()-1).equals("/"))
                	folder_num++;
			    System.out.printf("size: %d \n", objectSummary.getSize());
			    s3_size.add(objectSummary.getSize());
			    
			    // Use md5 of the metadata
			    GetObjectMetadataRequest mreq = new GetObjectMetadataRequest(bucketName, objectSummary.getKey());
			    ObjectMetadata retrieved_metadata = s3.getObjectMetadata(mreq);	 
			    System.out.println("Time: " + retrieved_metadata.getLastModified());
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			    Date date =retrieved_metadata.getLastModified();
			    String strDate= sdf.format(date );
			    long s3_time = Long.parseLong(strDate);
			    System.out.printf("newdate: %d \n", s3_time);
			    s3_date.add(s3_time);
			    System.out.println("md5: " + retrieved_metadata.getContentMD5());
			    s3_md5.add(retrieved_metadata.getContentMD5());
			    
            }
            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            //String token = result.getNextContinuationToken();
            //System.out.println("Next Continuation Token: " + token);
            //req.setContinuationToken(token);
       
		} catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } 
		
		
		//Find out the files of the local folder
		
		System.out.println("\nListing files of local folder:");
		
		//��ȡ�����ļ��б�
		getfile(filepath, wjpath);
		/*for(int i=0;i<wjpath.toArray().length;i++) {
			System.out.println(wjpath.toArray().length);
			System.out.println(wjpath.toArray()[i].toString());
			}*/
		
		for (int i = 0; i < wjpath.toArray().length; i++) {
			  String dir = wjpath.toArray()[i].toString();
			  File f = new File(dir);
			  if (f.isFile()) {//�ж��Ƿ�Ϊ�ļ�
				    String filename = dir.replace('\\', '/').substring(len+1);
				    //String keyName = Paths.get(dir).getFileName().toString();
			        System.out.printf("\nfileName: %s\n",filename);
			        local_file.add(filename);
				    long   size =   f.length(); //   size (bytes)
				    System.out.printf("size: %d \n", size);
				    local_size.add(size);
				    long   modify_time = f.lastModified(); //  lastModified time
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				    Date date = new Date(modify_time);
				    String strDate= sdf.format(date);
				    long local_time = Long.parseLong(strDate);
				    local_date.add(local_time);
				    System.out.printf("Time: %s \n", new Timestamp(modify_time).toString()); 
				    System.out.println("newdate: "+ local_time);
				    
				    //Use Md5 to verify if the file was modified	    
				    FileInputStream fis = new FileInputStream(f);
				    byte[] content_bytes = IOUtils.toByteArray(fis);
				    String md5 = DigestUtils.md5Hex(content_bytes);
				    local_md5.add(md5);
				    fis.close();
				    //�ϴ��ļ�ʱ����Ԫ�����е�md5�������´ν���md5��֤
				    System.out.printf("md5: %s\n", md5);
				    ObjectMetadata metadata = new ObjectMetadata();
				    metadata.setContentMD5(md5);
				    
			   }
			   /*else {
				    String filename = wjpath.toArray()[1].toString().substring(len+1);
			        System.out.printf("\nfileName: %s\n",filename);
			        local_file.add(filename);
			        long   size =   fileList[i].length(); //   size (bytes)
				    System.out.printf("size: %d \n", size);
				    local_size.add(size);
				    long   modify_time = fileList[i].lastModified(); //  lastModified time
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				    Date date = new Date(modify_time);
				    String strDate= sdf.format(date);
				    long local_time = Long.parseLong(strDate);
				    local_date.add(local_time);
				    System.out.printf("Time: %s \n", new Timestamp(modify_time).toString()); 
				    System.out.println("newdate: "+ local_time);
				    local_md5.add("null");
				    System.out.printf("md5: null\n");
			   }*/
			}
		
		
		//Compare the files of the local folder and S3:
		//����List��Object�����ת������������Ĳ���
		s3_num = s3_file.size();
		local_num = wjpath.toArray().length;
		Object[] path = wjpath.toArray();
		Object[] s3_file_a = s3_file.toArray();
		Object[] s3_size_a = s3_size.toArray();
		Object[] s3_date_a = s3_date.toArray();
		Object[] s3_md5_a = s3_md5.toArray();
		Object[] local_file_a = local_file.toArray();
		Object[] local_size_a = local_size.toArray();
		Object[] local_date_a = local_date.toArray();
		Object[] local_md5_a = local_md5.toArray();
		
		for (int i=0 ; i<s3_num ; i++) {
			s3_tagl.add(0);
		}
		for(int j=0 ; j<local_num; j++) {
			local_tagl.add(0);
		}
		Object[] s3_tag = s3_tagl.toArray();
		Object[] local_tag = local_tagl.toArray();
	
		
		for (int i=0 ; i<s3_num ; i++) {
			for(int j=0 ; j<local_num ; j++) {
				//�������ʹ���ַ���ƥ���㷨��ǰ��ʱ�����ڶ���ѭ���������ӿ�����ٶ�
				/*if(Integer.parseInt(local_tag[j].toString())!=0) {
					System.out.println("local_tagj]"+local_tag[j] );
					continue;
				}*/
					
				//�ҵ���ͬ�ļ�ʱ
				
				//���MD5У���붼��������������޸�
				if(s3_file_a[i].equals(local_file_a[j])) {
					if(s3_md5_a[i]!=null&&s3_md5_a[i].equals(local_md5_a[j])) {
						opera=-1;
						s3_tag[i] = opera;
						local_tag[j] = opera;
						//System.out.printf("no%s: %d-1 * %d-1\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						break;
					}		
					//md5�����ڵ����ȣ���Ҫ�ϴ����������ļ�
					else if(s3_md5_a[i]!=null&&!s3_md5_a[i].equals(local_md5_a[j])) {
						if(Long.parseLong(s3_date_a[0].toString())<Long.parseLong(local_date_a[0].toString())) {
							opera = upload; 	//upload=0:�������ļ��ϴ�
							local_tag[j] = upload;	//upload=0�������ļ��ϴ�
							s3_tag[i] = download;	//download=1�ӱ����ļ�����
							//System.out.printf("upload%s: %d1 * %d0\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						}
						else {
							opera = download;		//1:��s3�ļ����ز���Ϊ���ļ�
							local_tag[j] = download;	//download=1��s3����
							s3_tag[i] = upload;		//upload=0 s3��Ͱ���ļ��ϴ�������
							//System.out.printf("download%s: %d0 * %d1\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						}
						break;
					}
					
					//s3�е��ļ�������md5У����ʱ��ʹ���ļ�����޸����ںʹ�С�����ж�
					long time_bias = Long.parseLong(s3_date_a[i].toString())-Long.parseLong(local_date_a[j].toString());	
					if(!s3_size_a[i].equals(local_size_a[j])) {
						if(time_bias>0) {
							opera = download;		//�ļ���С������S3���޸����ڸ��£�1:��s3�ļ����ز���Ϊ���ļ�
							local_tag[j] = download;
							s3_tag[i] = upload;
							//System.out.printf("download%s: %d0 * %d1\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						}
						else {
							opera = upload; 	//0:�������ļ��ϴ�
							local_tag[j] = upload;
							s3_tag[i] = download;
							//System.out.printf("upload%s: %d1 * %d0\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						}
						break;
					}
					
					//�����ļ�����޸�ʱ�����ϴ��ļ���ʱ��ƫ����30��֮�ڲ����ļ���С���ʱ
					if(time_bias>0 && time_bias<=bias) {
						opera=-1;
						s3_tag[i] = opera;
						local_tag[j] = opera;
						//System.out.printf("no%s: %d-1 * %d-1\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						break;
					}
					else if(time_bias>bias) {
						opera = download;		//�ļ���С�����S3���޸����ڸ��£�1:��s3�ļ����ز���Ϊ���ļ�
						local_tag[j] = download;
						s3_tag[i] = upload;
						//System.out.printf("download%s: %d0 * %d1\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						break;
					}
					else {
						opera = upload;	 	//0:�������ļ��ϴ�
						local_tag[j] = upload;
						s3_tag[i] = download;
						//System.out.printf("upload%s: %d1 * %d0\n",s3_file_a[i], s3_tag[i],local_tag[j]);
						break;
					}
					
				}
			}
		}
		

		System.out.println("Begin to load....");
		//����operation������Ӧ���ϴ�������
		//�ϴ�
		for(int j=0 ; j<local_num ; j++) {
			if(Integer.parseInt(local_tag[j].toString())==0) {
				File f = new File(path[j].toString());
				if(f.length()<partSize) {
					new uploadtos3(s3,bucketName,local_file_a[j].toString(),path[j].toString());
				}
				else {
					new multipart_up(s3,bucketName,local_file_a[j].toString(),path[j].toString());
				}
				
			}
		}
		//����
		for (int i=0 ; i<s3_num  ; i++) {
			if(Integer.parseInt(s3_tag[i].toString())==0) {
				if(!s3_file_a[i].toString().substring(s3_file_a[i].toString().length()-1).equals("/")) {
					String p = filepath +"\\"+ s3_file_a[i].toString();
					if(Long.parseLong(s3_size_a[i].toString())<partSize) {
						new loadfroms3(s3,bucketName,s3_file_a[i].toString(),p);
					}
					else {
						new multipart_down(s3,bucketName,s3_file_a[i].toString(),p);
					}
				}
					
			}
		}
		System.out.println("Begin to watch  !");
		Path dir = Paths.get("D:\\eclipse2020-workplace\\homework1\\local_file\\");
		watchFile w = new watchFile(dir, s3, bucketName);
		//w.processEvents();
		
        System.out.println("\nEnd!");
        
	}
	
	public static void getfile(String dir,List<String> wjpath)
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
