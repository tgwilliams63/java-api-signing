import auth.AWS4SignerBase;
import auth.AWS4SignerForAuthorizationHeader;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import util.HttpUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class apiRequestv2 {
    public static void main(String[] args) {
        System.out.println("*******************************************************");
        System.out.println("*  Executing sample 'GetObjectUsingHostedAddressing'  *");
        System.out.println("*******************************************************");

        // the region-specific endpoint to the target object expressed in path style
        URL endpointUrl;
        String urlString = System.getenv("API_URL");
        if (urlString == null) {
            System.out.println("Please provide a url via the environment variable API_URL");
            System.exit(1);
        }
        try {
            endpointUrl = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }

        // for a simple GET, we have no body so supply the precomputed 'empty' hash
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-amz-content-sha256", AWS4SignerBase.EMPTY_BODY_SHA256);

        ProfileCredentialsProvider profileCredProvider = new ProfileCredentialsProvider("default");
        AWSCredentials profileCreds = profileCredProvider.getCredentials();

        AWS4SignerForAuthorizationHeader signer = new AWS4SignerForAuthorizationHeader(
                endpointUrl, "GET", "execute-api", "us-east-1");
        String authorization = signer.computeSignature(headers,
                null, // no query parameters
                AWS4SignerBase.EMPTY_BODY_SHA256,
                profileCreds.getAWSAccessKeyId(),
                profileCreds.getAWSSecretKey());

        // place the computed signature into a formatted 'Authorization' header
        // and call S3
        headers.put("Authorization", authorization);
        String response = HttpUtils.invokeHttpRequest(endpointUrl, "GET", headers, null);
        System.out.println("--------- Response content ---------");
        System.out.println(response);
        System.out.println("------------------------------------");
    }
}
