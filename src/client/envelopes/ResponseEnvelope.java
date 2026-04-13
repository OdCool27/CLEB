package envelopes;

import java.io.Serializable;
import java.util.UUID;

public class ResponseEnvelope<T> implements Serializable {
    private final UUID correlationId;
    private final String message;
    private final String status;
    private final T payload;

    public ResponseEnvelope(UUID correlationId, String message, String status, T payload) {
        this.correlationId = correlationId;
        this.message = message;
        this.status = status;
        this.payload = payload;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public T getPayload() {
        return payload;
    }
}

