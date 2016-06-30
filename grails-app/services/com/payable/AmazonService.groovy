package com.payable

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest

class AmazonService {
  def grailsApplication

  String url = ""
  String accessKey = grailsApplication.aws.accessKey
  String secretKey = grailsApplication.aws.secretKey
  String bucket = grailsApplication.aws.bucketName
  String urlS3 = grailsApplication.aws.domain

  String uploadFile(File file){
    AmazonS3 s3client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
    try {
      s3client.putObject(new PutObjectRequest(bucket, file.name, file).withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (AmazonServiceException asEx){
      throw new RuntimeException(asEx.getMessage())
    } catch (AmazonClientException acEx){
      throw new RuntimeException(acEx.getMessage())
    }
    url = "${bucket}.${urlS3}/${file.name}"
  }

  void deleteFile(String object){
    AmazonS3 s3client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
    try {
      s3Client.deleteObject(new DeleteObjectRequest(bucket, object));
    } catch (AmazonServiceException asEx) {
      throw new RuntimeException(asEx.getMessage())
    } catch (AmazonClientException acEx) {
      throw new RuntimeException(acEx.getMessage())
    }
  }
}
