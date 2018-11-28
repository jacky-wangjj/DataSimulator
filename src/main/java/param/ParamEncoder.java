package param;

import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by wangjj17 on 2018/11/28.
 */
public class ParamEncoder implements Serializer<Param> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, Param param) {
        return BeanUtils.toBytes(param);
    }

    @Override
    public void close() {

    }
}
