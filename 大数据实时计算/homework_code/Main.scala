import java.util.{Calendar, Date, Properties, Timer, UUID}
import java.io.{File, FileWriter, Serializable}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema

import scala.collection.mutable.ListBuffer

object Main {
  val accessKey = ""
  val secretKey = ""
  //s3地址
  val endpoint = ""
  //上传到的桶
  val bucket = "work3"
  val key = "data_s.txt"
  //上传文件的路径前缀
  val keyPrefix = "save/"
  //上传数据间隔 单位毫秒
  val period = 10000
  //输入的kafka主题名称
  val inputTopic = "mn_buy_ticket_2_xjun"
  //kafka地址
  val bootstrapServers = ""
  var timer: Timer = new Timer("S3Writer")
  var fileWriter: FileWriter = _
  var file_new = new ListBuffer[String]

  def main(args: Array[String]): Unit = {

    //从s3接入数据，使用kafka生产者,
    val s3 = new s3_to_kafka(accessKey, secretKey, endpoint, bucket, key, inputTopic, bootstrapServers)
    s3.perform()


    // 1.获取flink流计算的运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    //指定Kafka的Broker地址
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    //指定组ID
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    //如果没有记录偏移量，第一次从最开始消费
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    // 2.从kafka读取数据
    val kafkaConsumer = new FlinkKafkaConsumer010[ObjectNode](inputTopic, new JSONKeyValueDeserializationSchema(true), kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    //添加数据来源
    val inputKafkaStream = env.addSource(kafkaConsumer)
    //结果落地，存入S3
    println("存入S3")
    inputKafkaStream.map(data => {
      var file_name: String = data.get("value").get("buy_time").toString().substring(1, 8) + ".txt"
      //if(file_name.charAt(6)=='-'){
      //  file_name = file_name.substring(0,5)+ "0" + file_name.substring(5,6) + ".txt"
      //}
      val file_path: String = "data/" + file_name
      file_new.append(file_name)
      fileWriter = new FileWriter(file_path, true)
      fileWriter.append(data.toString + "\n")
      fileWriter.close()
      val time_count = new S3Writer(accessKey, secretKey, endpoint, bucket, keyPrefix, period, file_new, timer)
      time_count.time_task()
      println(data.toString)
    })
    //4.启动流计算
    env.execute()
  }
}
