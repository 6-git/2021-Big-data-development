package main;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.nio.file.Paths;

public class uploadtos3 {
private AmazonS3 s3;
private String filePath;
private String bucketName;


	uploadtos3(AmazonS3 up_s3, String up_bucket, String key, String up_filePath) {
		this.s3 = up_s3;
		this.bucketName = up_bucket;
		this.filePath = up_filePath;
		
		System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
		final String keyName = key; //Paths.get(filePath).getFileName().toString();
		final File file = new File(filePath);

		for (int i = 0; i < 2; i++) {
			try {
				s3.putObject(bucketName, keyName, file);
				break;
			} catch (AmazonServiceException e) {
				if (e.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
					s3.createBucket(bucketName);
					continue;
				}

				System.err.println(e.toString());
				System.exit(1);
			} catch (AmazonClientException e) {
				try {
					// detect bucket whether exists
					s3.getBucketAcl(bucketName);
				} catch (AmazonServiceException ase) {
					if (ase.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
						s3.createBucket(bucketName);
						continue;
					}
				} catch (Exception ignore) {
				}

				System.err.println(e.toString());
				System.exit(1);
			}
		}

		System.out.println("Upload Finish !");
	}
}
