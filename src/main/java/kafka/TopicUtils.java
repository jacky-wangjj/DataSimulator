package kafka;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import properties.SiteConfig;

/**
 * Created by wangjj17 on 2018/11/23.
 */
public class TopicUtils {
    private String zkConnect;

    public TopicUtils() {
        zkConnect = SiteConfig.get("zookeeper.connect");
    }

    /**
     * 创建topic
     * @param topic
     * @param partition
     * @param duplicate
     */
    public void createTopic(String topic, int partition, int duplicate, Properties props) {
        ZkUtils zkUtils = ZkUtils.apply(zkConnect, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        AdminUtils.createTopic(zkUtils, topic, partition, duplicate, props, RackAwareMode.Enforced$.MODULE$);
        zkUtils.close();
    }

    /**
     * 删除指定topic
     * @param topic
     */
    public void deleteTopic(String topic) {
        ZkUtils zkUtils = ZkUtils.apply(zkConnect, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        AdminUtils.deleteTopic(zkUtils, topic);
        zkUtils.close();
    }

    /**
     * 查询topic
     * @param topic
     * @return
     */
    public Properties queryTopic(String topic) {
        ZkUtils zkUtils = ZkUtils.apply(zkConnect, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        Properties props = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topic);
        zkUtils.close();
        return props;
    }

    /**
     * 修改topic的属性
     * @param topic
     * @param addProps
     * @param delProps
     */
    public Properties modifyTopic(String topic, Properties addProps, Properties delProps) {
        ZkUtils zkUtils = ZkUtils.apply(zkConnect, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        Properties props = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topic);
        Iterator addIt = addProps.entrySet().iterator();
        while (addIt.hasNext()) {
            Map.Entry entry = (Map.Entry) addIt.next();
            props.put(entry.getKey(), entry.getValue());//添加属性
        }
        Iterator delIt = delProps.entrySet().iterator();
        while (delIt.hasNext()) {
            Map.Entry entry = (Map.Entry) delIt.next();
            props.remove(entry.getKey());//删除属性
        }
        AdminUtils.changeTopicConfig(zkUtils, topic, props);//修改topic的属性
        zkUtils.close();
        return props;
    }

    public static void main(String[] args) {
        TopicUtils tu = new TopicUtils();
        String topic = "data";
        int partition = 2;
        int duplicate = 1;
        Properties props = new Properties();
        props.put("max.message.bytes", 655360);
        tu.createTopic(topic, partition, duplicate, props);
        Properties queryProps = tu.queryTopic(topic);
        Iterator it = queryProps.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        Properties addProps = new Properties();
        addProps.put("min.cleanable.dirty.ratio", "0.3");
        Properties delProps = new Properties();
        delProps.put("max.message.bytes", null);
        Properties newProps = tu.modifyTopic(topic, addProps, delProps);
        Iterator it1 = newProps.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry entry = (Map.Entry) it1.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        tu.deleteTopic(topic);
    }
}
