package com.payable

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.DeleteObjectRequest

import grails.config.Config
import grails.core.support.GrailsConfigurationAware

class AmazonService implements GrailsConfigurationAware {

  Config config

  String uploadFile(File file){
    AmazonS3 s3client = new AmazonS3Client(new BasicAWSCredentials(config.aws.accessKey, config.aws.secretKey))
    try {
      s3client.putObject(new PutObjectRequest(config.aws.bucket, file.name, file).withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (AmazonServiceException asEx){
      throw new RuntimeException(asEx.getMessage())
    } catch (AmazonClientException acEx){
      throw new RuntimeException(acEx.getMessage())
    }
    "${config.aws.bucket}.${config.aws.urlS3}/${file.name}"
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

  @Override
  void setConfiguration(Config config) {
    this.config = config
  }
}
