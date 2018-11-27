package kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import param.Param;
import properties.SiteConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by wangjj17 on 2018/11/23.
 */
public class Consumer {
    //手动指定放在jar包外的log4j配置文件
    static {
        PropertyConfigurator.configure(System.getProperty("user.dir")+ File.separator+"etc"+File.separator+"log4j.properties");
    }
    private static Logger logger = Logger.getLogger(Consumer.class);
    private KafkaConsumer consumer;
    private String topic;
    private String kafkaConnect;
    private String GROUPID;
    private String keyDeserializer;
    private String valueDeserializer;

    public Consumer(String topic) {
        kafkaConnect = SiteConfig.get("kafka.connect");
        GROUPID = SiteConfig.get("consumer.groupid");
        keyDeserializer = SiteConfig.get("consumer.key.deserializer");
        valueDeserializer = SiteConfig.get("consumer.value.deserializer");
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaConnect);
        props.put("group.id", GROUPID);
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);
        this.consumer = new KafkaConsumer(props);
        this.topic = topic;
        consumer.subscribe(Arrays.asList(topic));//订阅topic
    }

    public void consumer() {
        while (true) {
            ConsumerRecords msgList = consumer.poll(1000);
            if (null != msgList && msgList.count() > 0) {
                for (Object record : msgList) {
                    String data = (String) record;
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        Param param = (Param) ois.readObject();
                        logger.info(param.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String topic = "data";
        Consumer consumer = new Consumer(topic);
        consumer.consumer();
    }
}
