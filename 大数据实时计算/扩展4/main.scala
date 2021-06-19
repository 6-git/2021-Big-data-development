import java.io.FileWriter
import java.sql.{Connection, DriverManager}
import java.util.{Properties, Timer, TimerTask, UUID}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema

import scala.collection.immutable.ListMap

object main {
  //上传数据间隔 单位毫秒
  val period = 5000
  //输入的kafka主题名称
  val inputTopic = "flink_to_mysql_xjun"
  //kafka地址
  val bootstrapServers = ""
  val path = "flink2mysql.txt"
  var filewriter = new FileWriter(path,true)
  var change = false
  var timer: Timer = new Timer("sort")

  val url = ""
  val properties = new Properties
  properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver")
  properties.setProperty("user", "")
  properties.setProperty("password", "")
  val connection = DriverManager.getConnection(url, properties)
  val statement = connection.createStatement

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //连接mysql
    //用于创建表******
    //statement.execute("create table lbsb(sfzhm varchar(50), xm varchar(50), asjbh varchar(50),ajmc varchar(50), aj_jyqk varchar(50))")

    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[ObjectNode](inputTopic,
      new JSONKeyValueDeserializationSchema(true), kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)

    inputKafkaStream.map(x=>{
      println(x)
      val sfzhm = x.get("value").get("sfzhm").toString
      val xm = x.get("value").get("xm").toString
      val asjbh = x.get("value").get("asjbh").toString
      val ajmc = x.get("value").get("ajmc").toString
      val aj_jyqk = x.get("value").get("aj_jyqk").toString

      //statement.execute("show tables")
      statement.execute("INSERT INTO lbsb (sfzhm , xm , asjbh ,ajmc , aj_jyqk)"+
        "VALUES"+
        "( " + sfzhm + ","+ xm +"," + asjbh + "," + ajmc + "," + aj_jyqk+"); ")
      //if(!change)
      //  filewriter = new FileWriter(path,true)
      //filewriter.append(x+"\n")
      //filewriter.flush()
      //change = true
      //println(x)
      //time_task()
    })
    env.execute()
  }


  def time_task(): Unit = {
    timer.schedule(new TimerTask() {
      def run(): Unit = {
        this.synchronized{
          if(change) {
            filewriter.close()
            println("关闭文件")

            //文件关闭后，如果没有新记录，不会进行close
            change = false
          }
        }
      }
    }, 1000, period)

  }
}
