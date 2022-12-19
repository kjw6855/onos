package org.onosproject.intenderagent.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.codec.impl.CodecManager;
import org.onosproject.net.intent.Constraint;

import java.util.HashMap;
import java.util.Map;

public class IntenderCodec implements CodecContext {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CodecManager manager = new CodecManager();
    private final Map<Class<?>, Object> services = new HashMap<>();

    public IntenderCodec() {
        manager.activate();
        manager.unregisterCodec(Constraint.class);
        manager.registerCodec(Constraint.class, new MyConstraintCodec());
    }

    @Override
    public ObjectMapper mapper() {
        return mapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> JsonCodec<T> codec(Class<T> entityClass) {
        return manager.getCodec(entityClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getService(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }

    public <T> void registerService(Class<T> serviceClass, T impl) {
        services.put(serviceClass, impl);
    }
}
