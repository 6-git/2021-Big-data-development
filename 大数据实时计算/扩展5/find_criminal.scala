import java.util
import java.util.{Map, Properties, Scanner, UUID, Vector}

import com.bingocloud.util.json.JSONObject
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.client.JobExecutionException
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParseException
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema

object find_criminal {
  val inputTopics: util.ArrayList[String] = new util.ArrayList[String]() {
    {
      add("mn_buy_ticket_1") //车票购买记录主题
      add("mn_hotel_stay_1") //酒店入住信息主题
      add("mn_monitoring_1") //监控系统数据主题
    }
  }
  val bootstrapServers = ""
  //犯人名字集合
  var name_set: Map[String, util.ArrayList[String]] = new util.HashMap[String, util.ArrayList[String]]()

  //使用runnable接口实现线程
  class Thread_input extends Runnable{
    override def run(): Unit = {
      val input = new Scanner(System.in)
      while(true){
        val name = input.next()
        var record_list = name_set.get(name)
        if(record_list==null){
          println("系统中找不到“ + name + ”的任何记录")
        }
        else{
          var i = 0
          println()
          for(i <- 0 to record_list.toArray.length-1)
            println(record_list.get(i).toString)
        }
      }
    }
  }

  //设置实时监听输入的线程
  var thread_input = new Thread_input()
  var tt = new Thread(thread_input)

  def main(args: Array[String]): Unit = {
    //启动线程
    tt.start()

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    //val kafkaConsumer = new FlinkKafkaConsumer010[ObjectNode](inputTopics,
    //  new JSONKeyValueDeserializationSchema(true), kafkaProperties)
    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopics, new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)
    /*inputKafkaStream.map(x => {
      if(x != null){
        try {

          println(x)
          var name: String = x.get("value").get("username").toString()
          println(name)
          if (name != null) {
            //ArrayList初始为空，需要新建立键值对联系
            if (name_set.get(name) == null) {
              name_set.put(name, new util.ArrayList[String])
            }
            //添加记录进入ArrayList
            name_set.get(name).add(x.toString)
            println(x + " " + name_set.get(name).size())
          }
        } catch {
          case exception: Exception => {
            println(exception)
          }
          case exception: JsonParseException => {
                        println(exception)
                      }
          case exception: JobExecutionException => {
            println(exception)
          }
        }
      }
    })*/

    inputKafkaStream.map(x => {
      if (x != null) {
        try {
          //避免文件流中出现非ObjectNode格式数据而报错出现异常，先进行格式的转换
          var to_json = new JSONObject(x)
          var name = to_json.get("username").toString()
          if (name != null) {
            //ArrayList初始为空，需要新建立键值对联系
            if (name_set.get(name) == null) {
              name_set.put(name, new util.ArrayList[String])
            }
            //添加记录进入ArrayList
            name_set.get(name).add(x)
            println(x + " " + "一共有" + name_set.get(name).size() + "记录")
          }
        } catch {
          case exception: Exception => {
            println(exception)
          }
          case exception: JsonParseException => {
            println(exception)
          }
          case exception: JobExecutionException => {
            println(exception)
          }

        }
      }
    })


    env.execute()
  }
}
