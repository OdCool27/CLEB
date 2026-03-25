package server.dispatcher;

import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.handlers.user.LoginHandler;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class RequestDispatcher {

    private Map<String, RequestHandler<?>> handlers = new HashMap<>();

    public RequestDispatcher(){
        handlers.put("LOGIN", new LoginHandler());
    }

    public ResponseEnvelope<?> dispatch(RequestEnvelope requestEnvelope, Connection dbConn){

        RequestHandler<?> handler = handlers.get(requestEnvelope.getAction());

        if (handler == null){
            return new ResponseEnvelope(requestEnvelope.getCorrelationId(),
                    "Unknown Action", "FAIL", requestEnvelope.getAction());
        }

        return handler.handleRequest(requestEnvelope, dbConn);
    }
}
