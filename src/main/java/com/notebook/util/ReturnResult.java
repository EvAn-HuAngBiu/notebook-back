package com.notebook.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * General return value class for Restful api
 *
 * @author evan
 * @date 2020/10/9
 * @version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public final class ReturnResult {

    /**
     * Return code, include status code and status message
     * See also {@link ReturnCode}
     *
     * The Jackson serialization format for this field is specified by the configuration class
     * {@link com.notebook.config.CustomJacksonSerializer}
     *
     * @since 1.0.0
     */
    private ReturnCode code = ReturnCode.FAILED;

    /**
     * Return data map, which needs {@code String} as key and any type {@code Object} as value
     * Annotation {@link JsonInclude.Include#NON_NULL} means
     * this field will not be serialized if no data contained in this map.
     *
     * Default initial capacity for {@code HashMap} is 16 and load factor is 0.75
     *
     * @since 1.0.0
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> data = new HashMap<>(16);

    /**
     * Mark whether operation done successfully or not
     * Default option is {@code false}
     *
     * @since 1.0.0
     */
    private Boolean success = false;

    /**
     * Generate a new class instance
     * It is not to implement a singleton pattern, but to ensure consistency in the use of
     * class methods. Meanwhile, the class constructors is also available by using
     * {@link NoArgsConstructor} and {@link AllArgsConstructor} which are given by Lombok.
     *
     * @return A new class instance
     * @since 1.0.0
     */
    public static ReturnResult newInstance() {
        return new ReturnResult();
    }

    /**
     * Set response return code and message, it will not set response status, use
     * {@link HttpServletResponse#setStatus(int)} or
     * {@link org.springframework.web.bind.annotation.ResponseStatus} manually
     *
     * After version 1.0.1, this method will automatically set {@link ReturnResult#success} flag.
     * Only if {@code ReturnCode} equals {@link ReturnCode#SUCCESS} then the {@link ReturnResult#success}
     * will be set {@code true}, otherwise {@code false}
     *
     * @param code Set return code
     * @return Current instance ref
     * @since 1.0.0
     */
    public ReturnResult setCode(ReturnCode code) {
        this.code = code;
        if (this.code == ReturnCode.SUCCESS) {
            return isSuccess(true);
        }
        return this;
    }

    /**
     * Set response operation status, false by default
     *
     * @param success Operation status
     * @return Current instance ref
     * @since 1.0.0
     */
    public ReturnResult isSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    /**
     * Replace the existing data with given data
     * Use {@link ReturnResult#mergeData(Map)} to add data without replace
     *
     * @param data Response data map
     * @return Current instance ref
     * @since 1.0.0
     */
    public ReturnResult setData(Map<String, Object> data) {
        this.data.clear();
        return mergeData(data);
    }

    /**
     * Add all data to the existing data, it will not replace or delete data in result map
     * Use {@link ReturnResult#setData(Map)} to replace data
     *
     * @param data Response data map to add
     * @return Current instance ref
     * @since 1.0.0
     */
    public ReturnResult mergeData(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    /**
     * Add key-value pair to the data map
     *
     * @param key Data key to add to the result
     * @param value Data value to add to the result
     * @return Current instance ref
     * @since 1.0.0
     */
    public ReturnResult putData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * Automatic set response status for current request
     *
     * @param response The response to be set
     * @return Current instance ref
     *
     * @since 1.0.1
     */
    public ReturnResult withResponseStatus(HttpServletResponse response) {
        response.setStatus(this.code.responseCode);
        return this;
    }
}
