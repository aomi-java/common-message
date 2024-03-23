package tech.aomi.common.message.gson;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import tech.aomi.common.message.MessageEncodeDecodeService;

import java.nio.charset.Charset;

/**
 * @author Sean createAt 2021/7/12
 */
@RequiredArgsConstructor
public class GsonMessageEncodeDecodeService implements MessageEncodeDecodeService {

    private final Charset charset;

    @Override
    public <T> byte[] message2Byte(T payload) {
        Gson gson = new Gson();
        return gson.toJson(payload).getBytes(charset);
    }

    @Override
    public <T> T byte2Message(byte[] payload, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(new String(payload, charset), clazz);
    }
}
