package kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import param.Param;
import properties.SiteConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangjj17 on 2018/11/23.
 */
public class Producer {
    private static Logger logger = Logger.getLogger(Producer.class);
    private KafkaProducer producer;
    private String topic;
    private String kafkaConnect;
    private String acks;
    private String keySerializer;
    private String valueSerializer;

    public Producer(String topic) {
        kafkaConnect = SiteConfig.get("kafka.connect");
        acks = SiteConfig.get("producer.acks");
        keySerializer = SiteConfig.get("producer.key.serializer");
        valueSerializer = SiteConfig.get("producer.value.serializer");
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaConnect);
        props.put("acks", acks);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);
        producer = new KafkaProducer(props);
        this.topic = topic;
    }

    public void producer(String key, String data) {
        ProducerRecord record = new ProducerRecord(topic, key, data);
        producer.send(record);
    }

    public static void main(String[] args) {
        String topic = "data";
        String key = "param1";
        Param param = new Param();
        param.setTimestamp(Long.valueOf("1542966198938"));
        param.setId(1);
        param.setName("param1");
        Map values = new HashMap<>();
        values.put("par1", 12.12);
        values.put("par2", 2.1234);
        values.put("par3", 3);
        param.setValues(values);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = baos.toString();
        Producer producer = new Producer(topic);
        while (true) {
            producer.producer(key, data);
            logger.info(key+":"+data);
        }
    }
}
