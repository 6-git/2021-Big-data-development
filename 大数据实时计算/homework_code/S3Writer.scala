import java.io.{File, FileWriter}
import java.util.{Timer, TimerTask}

import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.services.s3.AmazonS3Client
import com.bingocloud.{ClientConfiguration, Protocol}
import org.apache.flink.configuration.Configuration

import scala.collection.mutable.ListBuffer

class S3Writer(accessKey: String, secretKey: String, endpoint: String, bucket: String, keyPrefix: String, period: Int, file_new: ListBuffer[String], timer: Timer) {

  var file: File = _
  var amazonS3: AmazonS3Client = _

  def upload: Unit = {
    this.synchronized {
      var i = 0
      for (i<- 0 to file_new.distinct.length-1) {
        val targetKey = keyPrefix + file_new.distinct.apply(i)
        //println(i,file_new.distinct.apply(i),file_new.distinct.length)
        val path = "data/" + file_new.distinct.apply(i)
        file = new File(path)
        //println(bucket, targetKey, file)
        amazonS3.putObject(bucket, targetKey, file)
        println("开始上传文件：%s 至 %s 桶的 %s 目录下".format(file.getAbsoluteFile, bucket, targetKey))
      }
      file_new.clear()
    }
  }

  def time_task(): Unit = {

    timer.schedule(new TimerTask() {
      def run(): Unit = {
        upload
      }
    }, 1000, period)
    val credentials = new BasicAWSCredentials(accessKey, secretKey)
    val clientConfig = new ClientConfiguration()
    clientConfig.setProtocol(Protocol.HTTP)
    amazonS3 = new AmazonS3Client(credentials, clientConfig)
    amazonS3.setEndpoint(endpoint)

  }

}
