package com.virnect.serviceserver.data;


import com.virnect.data.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class RepoDecoder<EntityType, ResponseType> {

    //private DataProcess<EntityType> entity;
    private EntityType entity = null;
    private DataProcess<ResponseType> response = null;
    //private DataProcess<ResponseType> apiResponse = null;

    public RepoDecoder(RepoDecoderType decoderType) {
        switch (decoderType) {
            case CREATE:
                //loadFromDatabase();
                processData();
                /*if(entity != null) {
                    processData();
                } else {
                    response = new DataProcess<ResponseType>().error(9999, "fail process data");
                }*/
                break;

            case UPDATE:
                //entity = loadFromDatabase();
                processData();
                //invokeDataProcess();
                break;
            case READ:
                //entity = loadFromDatabase();
                processData();
                break;
            case DELETE:
                processData();
                break;
            case FETCH:
                processData();
                break;
            default:
        }

    }

    private void processData() {
        //ApiResponse<ResponseType> apiResponse = invokeDataProcess();
        DataProcess<ResponseType> dataProcess = invokeDataProcess();
        response = new DataProcess<>(dataProcess.getData(), dataProcess.getCode(), dataProcess.getMessage());
        //response = new DataProcess<>(apiResponse.getData(), apiResponse.getCode(), apiResponse.getMessage());
        /*if (apiResponse.getCode() != 200) {
            response = new DataProcess<ResponseType>().fail(apiResponse.getData(), apiResponse.getCode(), apiResponse.getMessage());
        } else {
            response = new DataProcess<ResponseType>().success(
                    apiResponse.getData(),
                    apiResponse.getCode());
        }*/
    }

    protected ResponseType processResponse(ApiResponse<ResponseType> response) {
        return response.getData();
    }

    protected int getResponseCode(ApiResponse<ResponseType> response) {
        return response.getCode();
    }

    //abstract void invokeValidation();

    //abstract EntityType fetchFromRepository();

    abstract EntityType loadFromDatabase();

    //abstract ApiResponse<ResponseType> invokeDataProcess();
    abstract DataProcess<ResponseType> invokeDataProcess();

    DataProcess<ResponseType> asResponseData() {
        return response;
    }

    ApiResponse<ResponseType> asApiResponse() {
        return new ApiResponse<>(response.data, response.code, response.message);
    }

    ResponseEntity<byte[]> asResponseByteArray(byte[] data, HttpHeaders httpHeaders, HttpStatus status) {
        return new ResponseEntity<>(data, httpHeaders, status);
    }


}
