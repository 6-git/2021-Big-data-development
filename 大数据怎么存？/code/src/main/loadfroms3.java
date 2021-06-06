package main;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.AmazonS3;

public class loadfroms3 {
private AmazonS3 s3;
private String filePath;
private String bucketName;
	loadfroms3(AmazonS3 down_s3, String down_bucket, String key, String down_filePath) {
		
			this.s3 = down_s3;
			this.bucketName = down_bucket;
			this.filePath = down_filePath;
			
			
			final String keyName = key; 
			final String name = Paths.get(filePath).getFileName().toString();
			final File file = new File(filePath);
			File f = new File(filePath.replaceAll(name, ""));
			if (!f .exists() && !f .isDirectory()) {
				System.out.println("//不存在该目录，创建该目录。"); f .mkdir(); 
				} 
			else { System.out.println("//目录存在。"); }
			
	        System.out.format("Downloading %s from S3 bucket %s...\n", keyName, bucketName);
	        
	        S3ObjectInputStream s3is = null;
			FileOutputStream fos = null;
			try {
				S3Object o = s3.getObject(bucketName, keyName);
				s3is = o.getObjectContent();
				fos = new FileOutputStream(new File(filePath));
				byte[] read_buf = new byte[64 * 1024];
				int read_len = 0;
				while ((read_len = s3is.read(read_buf)) > 0) {
					fos.write(read_buf, 0, read_len);
				}
			} catch (AmazonServiceException e) {
				System.err.println(e.toString());
				System.exit(1);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			} finally {
				if (s3is != null) try { s3is.close(); } catch (IOException e) { }
				if (fos != null) try { fos.close(); } catch (IOException e) { }
			}
			
			System.out.println("Download Finish!");
		}
}
