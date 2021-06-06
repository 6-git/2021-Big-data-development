package main;

import com.amazonaws.services.s3.AmazonS3;

public class deletes3 {

	deletes3(AmazonS3 d_s3, String d_bucket, String key){
		System.out.format("Delete %s of S3 bucket %s...\n", key, d_bucket);
		d_s3.deleteObject(d_bucket,key);
	}
}
