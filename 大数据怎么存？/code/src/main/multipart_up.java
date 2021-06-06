package main;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class multipart_up {
private AmazonS3 s3;
private String filePath;
private String bucketName;
private static long partSize = 5 << 20;

multipart_up(AmazonS3 up_s3, String up_bucket, String key, String up_filePath) {
	
	this.s3 = up_s3;
	this.bucketName = up_bucket;
	this.filePath = up_filePath;
	String keyName = key;
	
	// Create a list of UploadPartResponse objects. You get one of these
    // for each part upload.
	ArrayList<PartETag> partETags = new ArrayList<PartETag>();
	File file = new File(filePath);
	long contentLength = file.length();
	String uploadId = null;
	System.out.format("Upload %s to S3 bucket %s...\n", keyName, bucketName);
	try {
		// Step 1: Initialize.
		InitiateMultipartUploadRequest initRequest = 
				new InitiateMultipartUploadRequest(bucketName, keyName);
		uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
		System.out.format("Created upload ID was %s\n", uploadId);

		// Step 2: Upload parts.
		long filePosition = 0;
		for (int i = 1; filePosition < contentLength; i++) {
			// Last part can be less than 5 MB. Adjust part size.
			partSize = Math.min(partSize, contentLength - filePosition);

			// Create request to upload a part.
			UploadPartRequest uploadRequest = new UploadPartRequest()
					.withBucketName(bucketName)
					.withKey(keyName)
					.withUploadId(uploadId)
					.withPartNumber(i)
					.withFileOffset(filePosition)
					.withFile(file)
					.withPartSize(partSize);

			// Upload part and add response to our list.
			System.out.format("Uploading part %d\n", i);
			partETags.add(s3.uploadPart(uploadRequest).getPartETag());

			filePosition += partSize;
		}

		// Step 3: Complete.
		System.out.println("Completing upload");
		CompleteMultipartUploadRequest compRequest = 
				new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

		s3.completeMultipartUpload(compRequest);
	} catch (Exception e) {
		System.err.println(e.toString());
		if (uploadId != null && !uploadId.isEmpty()) {
			// Cancel when error occurred
			System.out.println("Aborting upload");
			s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
		}
		System.exit(1);
	}
	System.out.println("Done!");
	
	
}

}
