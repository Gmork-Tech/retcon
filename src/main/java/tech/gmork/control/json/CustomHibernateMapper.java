package tech.gmork.control.json;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;

//@JsonFormat
//@PersistenceUnitExtension
@RegisterForReflection
public class CustomHibernateMapper implements FormatMapper, StatementInspector {
    private final FormatMapper delegate = new JacksonJsonFormatMapper(CustomJacksonMapper.getInstance());

    public CustomHibernateMapper() {}

    @Override
    public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {
        return delegate.fromString(charSequence, javaType, wrapperOptions);
    }

    @Override
    public <T> String toString(T t, JavaType<T> javaType, WrapperOptions wrapperOptions) {
        return delegate.toString(t, javaType, wrapperOptions);
    }

    @Override
    public String inspect(String s) {
        return s;
    }
}