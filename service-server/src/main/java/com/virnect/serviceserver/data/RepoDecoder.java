package com.virnect.serviceserver.data;


import com.virnect.serviceserver.api.ApiResponse;

public abstract class RepoDecoder<EntityType, ResponseType> {

    //private DataProcess<EntityType> entity;
    private EntityType entity = null;
    private DataProcess<ResponseType> response = null;
    //private DataProcess<ResponseType> apiResponse = null;

    public RepoDecoder(RepoDecoderType decoderType) {
        switch (decoderType) {
            case CREATE:
                loadFromDatabase();
                processData();
                /*if(entity != null) {
                    processData();
                } else {
                    response = new DataProcess<ResponseType>().error(9999, "fail process data");
                }*/
                break;

            case UPDATE:
                entity = loadFromDatabase();
                processData();
                //invokeDataProcess();
                break;
            case READ:
                entity = loadFromDatabase();
                processData();
                break;
            case DELETE:
                processData();
                break;
            default:
        }

    }

    private void processData() {
        ApiResponse<ResponseType> apiResponse = invokeDataProcess();
        if (apiResponse.getCode() != 200) {
            response = new DataProcess<ResponseType>().fail(apiResponse.getData(), apiResponse.getCode(), apiResponse.getMessage());
        } else {
            response = new DataProcess<ResponseType>().success(
                    apiResponse.getData(),
                    apiResponse.getCode());
        }
    }

    protected ResponseType processResponse(ApiResponse<ResponseType> response) {
        return response.getData();
    }

    protected int getResponseCode(ApiResponse<ResponseType> response) {
        return response.getCode();
    }


    abstract EntityType loadFromDatabase();

    abstract ApiResponse<ResponseType> invokeDataProcess();

    DataProcess<ResponseType> asResponseData() {
        return response;
    }

    ApiResponse<ResponseType> asApiResponse() {
        return new ApiResponse<>(response.data);
    }


}
