package dispatcher;

import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import handlers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class RequestDispatcher {
    private static final Logger logger = LogManager.getLogger(RequestDispatcher.class);
    private Map<String, RequestHandler<?>> handlers = new HashMap<>();

    public RequestDispatcher(Connection conn){
        handlers.put("LOGIN", new LoginHandler(conn));

        UserHandler userHandler = new UserHandler(conn);
        handlers.put("CREATE_USER", userHandler);
        handlers.put("UPDATE_USER", userHandler);
        handlers.put("GET_USER", userHandler);

        EquipmentHandler equipmentHandler = new EquipmentHandler(conn);
        handlers.put("ADD_EQUIPMENT", equipmentHandler);
        handlers.put("UPDATE_EQUIPMENT", equipmentHandler);
        handlers.put("DELETE_EQUIPMENT", equipmentHandler);
        handlers.put("GET_EQUIPMENT", equipmentHandler);
        handlers.put("GET_ALL_EQUIPMENT", equipmentHandler);
        handlers.put("MARK_MAINTENANCE", equipmentHandler);
        handlers.put("RESTORE_AVAILABILITY", equipmentHandler);
        handlers.put("GET_AVAILABLE_EQUIPMENT_AT_TIME", equipmentHandler);

        LabHandler labHandler = new LabHandler(conn);
        handlers.put("GET_ALL_LABS", labHandler);
        handlers.put("GET_LAB_BY_ID", labHandler);
        handlers.put("GET_SEATS_BY_LAB", labHandler);
        handlers.put("GET_AVAILABLE_SEATS_AT_TIME", labHandler);
        handlers.put("ADD_LAB", labHandler);
        handlers.put("UPDATE_LAB", labHandler);
        handlers.put("DELETE_LAB", labHandler);
        handlers.put("ADD_LAB_SEAT", labHandler);

        ReservationHandler reservationHandler = new ReservationHandler(conn);
        handlers.put("CREATE_EQUIPMENT_RESERVATION", reservationHandler);
        handlers.put("CREATE_LAB_SEAT_RESERVATION", reservationHandler);
        handlers.put("CANCEL_EQUIPMENT_RESERVATION", reservationHandler);
        handlers.put("CANCEL_LAB_SEAT_RESERVATION", reservationHandler);
        handlers.put("GET_EQUIPMENT_RESERVATIONS_BY_STUDENT", reservationHandler);
        handlers.put("GET_LAB_SEAT_RESERVATIONS_BY_STUDENT", reservationHandler);
        handlers.put("GET_ALL_EQUIPMENT_RESERVATIONS", reservationHandler);
        handlers.put("GET_ALL_LAB_SEAT_RESERVATIONS", reservationHandler);
        handlers.put("APPROVE_EQUIPMENT_RESERVATION", reservationHandler);
        handlers.put("APPROVE_LAB_SEAT_RESERVATION", reservationHandler);
        handlers.put("REJECT_EQUIPMENT_RESERVATION", reservationHandler);
        handlers.put("REJECT_LAB_SEAT_RESERVATION", reservationHandler);
    }

    public ResponseEnvelope<?> dispatch(RequestEnvelope requestEnvelope, Connection conn){
        logger.debug("Dispatching request: {} (ID: {})", requestEnvelope.getAction(), requestEnvelope.getCorrelationId());
        RequestHandler handler = handlers.get(requestEnvelope.getAction());

        if (handler == null){
            logger.warn("No handler found for action: {}", requestEnvelope.getAction());
            return new ResponseEnvelope(requestEnvelope.getCorrelationId(),
                    "Unknown Action: " + requestEnvelope.getAction(), "FAIL", requestEnvelope.getAction());
        }

        ResponseEnvelope<?> response = handler.handleRequest(requestEnvelope, conn);
        logger.debug("Request {} (ID: {}) handled with status: {}",
                requestEnvelope.getAction(), requestEnvelope.getCorrelationId(), response.getStatus());
        return response;
    }
}
