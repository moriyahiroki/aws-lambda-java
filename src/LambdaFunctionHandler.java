
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Request, Response> {

    @Override
    public Response handleRequest(Request input, Context context) {
        context.getLogger().log("Input: " + input);
        String status;
        
        // TODO: implement your handler
        if(input.accountId.equals("012345678912")){
        	status = "OK";
        }else{
        	status = "NG";
        }
        
        return new Response(status);
    }

}
