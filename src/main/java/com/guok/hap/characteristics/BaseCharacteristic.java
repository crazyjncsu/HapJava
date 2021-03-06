package com.guok.hap.characteristics;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.guok.hap.HomekitCharacteristicChangeCallback;
import com.guok.hap.impl.HomekitUtils;
import com.guok.hap.impl.responses.HapStatusCodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * all characteristics require iOS 10 or 10.3 have not supported.
 * Base class for implementing {@link Characteristic}.
 *
 * @author Andy Lintner
 */
public abstract class BaseCharacteristic<T> implements Characteristic {

    protected final Logger logger = LoggerFactory.getLogger(BaseCharacteristic.class);

    private final String type;
    private final CharacteristicValueFormats format;
    private final boolean isWritable;
    private final boolean isReadable;
    private final boolean isEventable;
    private final String description;

    protected T minValue;
    protected T maxValue;
    protected T minStep;
    protected CharacteristicUnits unit;

    protected T value;
    protected CharacteristicCallBack<T> mCallBack;
    protected HomekitCharacteristicChangeCallback subcribeCallback;

    /**
     * Default constructor
     *
     * @param type        a string containing a type that indicates the type of characteristic.
     *                    Apple defines a set of these, however implementors can create their own as
     *                    well.
     * @param format      a string indicating the value type, which must be a recognized type by the
     *                    consuming device.
     * @param isWritable  indicates whether the value can be changed.
     * @param isReadable  indicates whether the value can be retrieved.
     * @param description a description of the characteristic to be passed to the consuming device.
     */
    public BaseCharacteristic(String type, CharacteristicValueFormats format, boolean isWritable, boolean isReadable, String description) {
        if (type == null || format == null || description == null) {
            throw new NullPointerException();
        }
        this.type = HomekitUtils.getTypeFromUUID(type);
        this.format = format;
        this.isWritable = isWritable;
        this.isReadable = isReadable;
        this.isEventable = this instanceof EventableCharacteristic;
        this.description = description;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public final ListenableFuture<JsonObject> toJson(int iid) {
        return Futures.transform(makeBuilder(iid), new Function<JsonObjectBuilder, JsonObject>() {
            @Override
            public JsonObject apply(JsonObjectBuilder jsonObjectBuilder) {
                return jsonObjectBuilder.build();
            }
        });
    }

    /**
     * Creates the JSON serialized form of the accessory for use over the Homekit Accessory
     * Protocol.
     *
     * @param instanceId the static id of the accessory.
     * @return a future that will complete with the JSON builder for the object.
     */
    protected ListenableFuture<JsonObjectBuilder> makeBuilder(final int instanceId) {

        return Futures.transform(getValue(), new Function<T, JsonObjectBuilder>() {
            @Override
            public JsonObjectBuilder apply(T value) {
                try {
                    JsonArrayBuilder perms = Json.createArrayBuilder();
                    if (isWritable) {
                        perms.add("pw");
                    }
                    if (isReadable) {
                        perms.add("pr");
                    }
                    if (isEventable) {
                        perms.add("ev");
                    }
                    JsonObjectBuilder builder = Json.createObjectBuilder()
                            .add("iid", instanceId)
                            .add("type", type)
                            .add("perms", perms.build())
                            .add("format", format.toString())
                            .add("events", false)
                            .add("bonjour", false)
                            .add("description", description);
                    setJsonValue(builder, value);
                    return builder;
                } catch (Exception e) {
                    logger.error("Could not retrieve value " + this.getClass().getName(), e);
                    return null;
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int setValue(JsonValue jsonValue) {

        try {
            return this.setValue(convert(jsonValue));
        } catch (Exception e) {
            logger.error("Error while setting JSON value", e);
            return -1;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int supplyValue(JsonObjectBuilder builder) {
        try {
            return setJsonValue(builder, getValueImmediately().get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error retrieving value", e);
            return setJsonValue(builder, getDefault());
        }
    }

    /**
     * Converts from the JSON value to a Java object of the type T
     *
     * @param jsonValue the JSON value to convert from.
     * @return the converted Java object.
     */
    protected abstract T convert(JsonValue jsonValue);

    /**
     * Update the characteristic value using a new value supplied by the connected client.
     *
     * @param value the new value to set.
     * @return 0 when set successfully. {@link HapStatusCodes} when set failure.
     * @throws Exception if the value cannot be set.
     */
    protected int setValue(T value) throws Exception {
        this.value = value;

        if (this.subcribeCallback != null)
            this.subcribeCallback.changed();

        if (this.mCallBack != null)
            return this.mCallBack.setValueCallback(value, this.subcribeCallback != null);
        return HapStatusCodes.SUCCESS;
    }

    /**
     * Retrieves the current value of the characteristic.
     *
     * @return a future that will complete with the current value.
     */
    public ListenableFuture<T> getValue() {
        if (this.mCallBack != null) {
            ListenableFuture<T> valueCallback = this.mCallBack.getValueCallback(this, this.subcribeCallback != null, new CharacteristicCallBack.FetchCallBack<T>() {
                @Override
                public void fetchValue(T val) {
                    value = val;
                    if (subcribeCallback != null)
                        subcribeCallback.changed();//iOS could receive new value via this method
                }
            });
            return valueCallback != null ? valueCallback : getValueImmediately();
        }
        return getValueImmediately();
    }

    /**
     * Supplies a default value for the characteristic to send to connected clients when the real
     * value. cannot be retrieved.
     *
     * @return a sensible default value.
     */
    protected abstract T getDefault();

    /**
     * Writes the value key to the serialized characteristic
     *
     * @param builder The JSON builder to add the value to
     * @param value   The value to add
     */
    protected int setJsonValue(JsonObjectBuilder builder, T value) {
        //I don't like this - there should really be a way to construct a disconnected JSONValue...
        if (value instanceof Boolean) {
            builder.add("value", (Boolean) value);
        } else if (value instanceof Double) {
            builder.add("value", (Double) value);
        } else if (value instanceof Integer) {
            builder.add("value", (Integer) value);
        } else if (value instanceof Long) {
            builder.add("value", (Long) value);
        } else if (value instanceof BigInteger) {
            builder.add("value", (BigInteger) value);
        } else if (value instanceof BigDecimal) {
            builder.add("value", (BigDecimal) value);
        } else if (value == null) {
            // Do not add null value, HomeKit cannot handle that
        } else {
            builder.add("value", value.toString());
        }
        return HapStatusCodes.SUCCESS;
    }

    @Override
    public String getType() {
        return type;
    }

    private static final class FutureException extends Exception {

    }

    public void setCallBack(CharacteristicCallBack<T> callBack) {
        this.mCallBack = callBack;
    }


    public ListenableFuture<T> getValueImmediately() {
        return Futures.immediateFuture(value);
    }

    public HomekitCharacteristicChangeCallback getSubcribeCallback() {
        return subcribeCallback;
    }

    protected void setSubcribeCallback(HomekitCharacteristicChangeCallback subcribeCallback) {
        this.subcribeCallback = subcribeCallback;
    }
}
