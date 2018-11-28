package kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import param.Param;
import param.ParamEncoder;
import properties.SiteConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangjj17 on 2018/11/23.
 */
public class ParamProducer {
    //手动指定放在jar包外的log4j配置文件
    static {
        PropertyConfigurator.configure(System.getProperty("user.dir")+ File.separator+"etc"+File.separator+"log4j.properties");
    }
    private static Logger logger = Logger.getLogger(ParamProducer.class);
    private KafkaProducer<String, Param> producer;
    private String topic;
    private Boolean isAsync;

    /**
     * 构造函数，同步或异步方式发送消息
     * @param topic
     * @param isAsync
     */
    public ParamProducer(String topic, Boolean isAsync) {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SiteConfig.get("kafka.connect"));//kafka地址
        props.setProperty(ProducerConfig.ACKS_CONFIG, SiteConfig.get("producer.acks"));//是否等待kafka的响应；0为不等待，1等待本机，all等待kafka集群同步完成
        props.setProperty(ProducerConfig.RETRIES_CONFIG, SiteConfig.get("producer.retries"));//消息发送失败后重新发送次数
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());//键序列化
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ParamEncoder.class.getName());//值序列化
        producer = new KafkaProducer<String, Param>(props);
        this.topic = topic;
        this.isAsync = isAsync;
    }

    /**
     * 回调函数
     */
    class SendCallBack implements Callback {
        private final long startTime;
        private final String key;
        private final Param param;

        SendCallBack(long startTime, String key, Param param) {
            this.startTime = startTime;
            this.key = key;
            this.param = param;
        }

        @Override
        public void onCompletion(RecordMetadata metadata, Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (metadata != null) {
                logger.info("message("+key+":"+param.toString()+") sent to partition("+metadata.partition()+"), offset("+metadata.offset()+")"+elapsedTime+"ms");
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生产函数
     * @param key
     * @param param
     */
    public void producer(String key, Param param) {
        ProducerRecord<String, Param> record = new ProducerRecord<String, Param>(topic, key, param);
        long startTime = System.currentTimeMillis();
        if (isAsync) {//send asynchronously
            producer.send(record, new SendCallBack(startTime, key, param));
        } else {//send synchronously
            try {
                producer.send(record).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        logger.info("sent message:("+key+":"+param.toString()+")");
    }

    /**
     * kafka-console-producer.sh --broker-list `hostname`:6667 --topic data1
     * @param args
     */
    public static void main(String[] args) {
        TopicUtils tu = new TopicUtils();
        String topic = SiteConfig.get("kafka.topic");
        int partition = 2;
        int duplicate = 1;
        Properties props = new Properties();
        props.put("max.message.bytes", "655360");
        if (tu.queryTopic(topic) == null) {
            tu.createTopic(topic, partition, duplicate, props);
        }
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
        Boolean isAsync = Boolean.valueOf(SiteConfig.get("producer.send.isAsync"));
        ParamProducer pp = new ParamProducer(topic, isAsync);
        int n = 0;
        try {
            while (true) {
                pp.producer(key, param);
                if (++n == 100) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pp.producer.close();
        }
    }
}
