package envelopes;

import java.io.Serializable;
import java.util.UUID;

public class RequestEnvelope<T> implements Serializable {
    private final UUID correlationId;
    private final String action;
    private final T payload;

    public RequestEnvelope(UUID correlationId, String action, T payload) {
        this.correlationId = correlationId;
        this.action = action;
        this.payload = payload;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public String getAction() {
        return action;
    }

    public T getPayload() {
        return payload;
    }


}
