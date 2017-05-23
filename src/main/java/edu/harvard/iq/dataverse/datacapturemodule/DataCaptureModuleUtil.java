package edu.harvard.iq.dataverse.datacapturemodule;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class DataCaptureModuleUtil {

    private static final Logger logger = Logger.getLogger(DataCaptureModuleUtil.class.getCanonicalName());

    static JsonObject generateJsonForUploadRequest(AuthenticatedUser user, Dataset dataset) {
        JsonObjectBuilder jab = Json.createObjectBuilder();
//        // The general rule should be to always pass the user id and dataset id to the DCM.
        jab.add("userId", user.getId());
        // FIXME: It would make more sense for the key to be "datasetId" since we're sending the primary key.
        jab.add("datasetIdentifier", dataset.getId());
        return jab.build();
    }

    public static ScriptRequestResponse getScriptFromRequest(HttpResponse<JsonNode> uploadRequest) {
        int status = uploadRequest.getStatus();
        JsonNode body = uploadRequest.getBody();
        logger.info("Got " + status + " with body: " + body);
        if (status == 404) {
            return new ScriptRequestResponse(status);
        }
        int httpStatusCode = uploadRequest.getStatus();
        String script = body.getObject().getString("script");
        long datasetId = body.getObject().getLong("datasetIdentifier");
        long userId = body.getObject().getLong("userId");
        ScriptRequestResponse scriptRequestResponse = new ScriptRequestResponse(httpStatusCode, datasetId, userId, script);
        return scriptRequestResponse;
    }

    static UploadRequestResponse makeUploadRequest(HttpResponse<String> uploadRequest) {
        int status = uploadRequest.getStatus();
        String body = uploadRequest.getBody();
        logger.info("Got " + status + " with body: " + body);
        return new UploadRequestResponse(uploadRequest.getStatus(), body);
    }

}