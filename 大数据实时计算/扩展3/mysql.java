import com.bingocloud.ClientConfiguration;
import com.bingocloud.Protocol;
import com.bingocloud.auth.BasicAWSCredentials;
import com.bingocloud.services.s3.AmazonS3Client;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class mysql {
    private List table_list = new ArrayList();
    private List column_list = new ArrayList();
    private List data_list = new ArrayList();
    private ResultSet resultSet;
    private Statement statement;
    final String accessKey = "";
    final String secretKey = "";
    //s3地址
    final String endpoint = "";
    //上传到的桶
    final String bucket = "work3";
    final String key = "mysql.txt";
    //kafka参数
    final String topic = "lbsb";
    final String bootstrapServers = "";
    AmazonS3Client amazonS3;

    mysql() throws SQLException {
        String url = "";
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("user", "");
        properties.setProperty("password", "");
        Connection connection = DriverManager.getConnection(url, properties);
        statement = connection.createStatement();
    }

    public void get_data() throws SQLException, IOException {

        resultSet = statement.executeQuery("show tables");
        //每个database都要初始化table_list，否则会将其他database的也保存下来
        while (resultSet.next()) {
            String tableName = resultSet.getString(1);
            table_list.add(tableName);
        }
        Object[] table_array = table_list.toArray();

        for(int i=0;i<table_array.length;i++){
            System.out.println("table_name" + table_array[i].toString());
            //获取表列名
            column_list = new ArrayList();
            resultSet = statement.executeQuery("show columns from "+table_array[i]);
            while (resultSet.next()) {
                String columnName = resultSet.getString(1);
                column_list.add(columnName);
                System.out.println("column_name" + columnName);
            }

            //查询表数据
            resultSet = statement.executeQuery("select * from "+table_array[i]);
            Object[] column_array = column_list.toArray();
            while (resultSet.next()) {
                List<String> datas = new ArrayList<String>();
                String s = "{";
                for (int j = 1; j <= column_array.length; j++) {
                    datas.add(resultSet.getString(j));
                    s = s + "\"" + column_array[j - 1].toString() + "\"" + ":" + "\"" + resultSet.getString(j) + "\"";
                    if (j != column_array.length)
                        s += ",";
                }
                s += "}";

                System.out.println(s);
                //生产数据到kafaka，一边获取数据一边生产，每得到一行表中的数据就生产一行
                produceToKafka(s);
            }
        }
    }



    public void produceToKafka(String s3Content){
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props) ;
        ProducerRecord record = new ProducerRecord<String, String>(topic, null, s3Content);
        System.out.println("开始生产数据：" + s3Content);
        producer.send(record);
        producer.flush();
        producer.close();
    }

    public static void main(String args[]) throws SQLException, IOException {
        mysql m = new mysql();
        m.get_data();
        

    }
}