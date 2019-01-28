package kafka;

import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.parser.JSONParser;
import kafka.consumer.Consumer;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import kafka.consumer.ConsumerConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import param.Param;
import param.ParamDecoder;
import properties.SiteConfig;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangjj17 on 2018/11/28.
 */
public class ParamConsumer {
    //手动指定放在jar包外的log4j配置文件
    static {
        PropertyConfigurator.configure(System.getProperty("user.dir")+ File.separator+"etc"+File.separator+"log4j.properties");
    }
    private static Logger logger = Logger.getLogger(kafka.ParamConsumer.class);
    private ConsumerConnector connector;
    private String topic;

    /**
     * 构造函数
     * @param topic
     */
    public ParamConsumer(String topic) {
        Properties props = new Properties();
        props.setProperty("zookeeper.connect", SiteConfig.get("zookeeper.connect"));
        props.setProperty("metadata.broker.list", SiteConfig.get("kafka.connect"));
        props.setProperty("group.id", SiteConfig.get("consumer.groupid"));
        this.topic = topic;
        connector = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
    }

    /**
     * 消费函数
     */
/*
    public void consumer() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        ParamDecoder valueDecoder = new ParamDecoder();
        Map<String, List<KafkaStream<String, Param>>> consumerMap = connector.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        KafkaStream<String, Param> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<String, Param> it = stream.iterator();
        while (it.hasNext()) {
            Param param = it.next().message();
            logger.info(param.toString());
        }
    }
*/

    public void consumer() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
        Map<String, List<KafkaStream<String, String>>> consumerMap = connector.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        while (it.hasNext()) {
            String paramJson = it.next().message();
            Param param = JSON.parseObject(paramJson, Param.class);
            logger.info(param.toString());
        }
    }

    /**
     * kafka-console-consumer.sh --bootstrap-server `hostname`:6667 --topic data1 --new-consumer --from-beginning
     * @param args
     */
    public static void main(String[] args) {
        String topic = SiteConfig.get("kafka.topic");
        ParamConsumer pc = new ParamConsumer(topic);
        pc.consumer();
    }
}
