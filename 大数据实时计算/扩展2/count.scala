
import java.util.{Properties, UUID}
import java.util.{Timer, TimerTask}

import scala.collection.immutable.ListMap
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema

import scala.collection.mutable.Map

object count {

  val inputTopic = "mn_buy_ticket_1_xjun"

  val bootstrapServers = ""
  var maps:Map[String, Int] = Map[String, Int]()
  val period = 20000
  var timer: Timer = new Timer("sort")
  var change = false

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
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

    inputKafkaStream.map(x => {
      val k:String = x.get("value").get("destination").toString
      if(maps.contains(k)){
        var num:Int = maps.apply(k)+1
        maps.put(k,num)
      }
      else{
        maps.put(k, 1)
      }
      //println(x)
      //使用value值从大到小进行排序
      change = true
      time_task()
    })
    env.execute()
  }

  //计时任务，每20秒，如果期间无新数据流入则统计排序一次
  def time_task(): Unit = {
    timer.schedule(new TimerTask() {
      def run(): Unit = {
        this.synchronized{
          if(change) {
            val l = ListMap(maps.toSeq.sortWith(_._2 > _._2): _*)
            change = false
            println(l.take(5))
          }
        }
      }
    }, 1000, period)

  }


}
