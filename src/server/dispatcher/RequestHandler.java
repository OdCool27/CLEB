package server.dispatcher;

import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;

import java.sql.Connection;

public interface RequestHandler<T> {
    ResponseEnvelope<?> handleRequest(RequestEnvelope<T> request, Connection conn);
}
