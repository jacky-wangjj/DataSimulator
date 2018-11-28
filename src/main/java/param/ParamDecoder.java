package param;

import kafka.serializer.Decoder;

/**
 * Created by wangjj17 on 2018/11/28.
 */
public class ParamDecoder implements Decoder<Param> {

    @Override
    public Param fromBytes(byte[] bytes) {
        return (Param) BeanUtils.toObject(bytes);
    }
}
