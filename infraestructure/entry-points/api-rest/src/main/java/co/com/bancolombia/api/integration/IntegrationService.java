package co.com.bancolombia.api.integration;

import java.io.FileNotFoundException;
import java.security.InvalidKeyException;

import javax.validation.Valid;

import com.itextpdf.text.DocumentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "external/externalconsumer", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityRequirement(name = "external")

public class IntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationService.class);

    private ResponseData responseData = new ResponseData();

    @PostMapping("/upimfile")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Images upload succesfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request") })
    public ResponseEntity<ResponseData> uploadFile(@Valid @RequestBody RequestData<TrfsImages> requestData)
            throws DocumentException, TrfsException, FileNotFoundException {

        Metadata metadata = MetadataRequest.builder().messageId(requestData.getMetaData().getMessageId())
                .systemId(requestData.getMetaData().getSystemId()).build();

        LOGGER.info(String.format(Constants.METADATA_REQUEST, metadata.getMessageId(), metadata.getSystemId()));

        ResponseData responseDataUpload = new ResponseData();
        requestData.getData().setMessageId(requestData.getMetaData().getMessageId());
        ResponseData responseDataLiquid = saveImagesTrfs.uploadFile(requestData.getData());
        responseDataUpload.setData(responseDataLiquid.getData());
        responseData.setMetaData(new MetadataResponse(metadata.getMessageId(), metadata.getSystemId()));
        if (responseDataLiquid.getErrors() == null) {
            ResponseData responseDataImage = saveTrfs.matchTrfsImages(requestData.getData().getNumberSecuence());
            if (responseDataImage.getErrors() == null) {
                responseDataUpload.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(responseDataUpload, HttpStatus.OK);
            }
        }

        responseDataUpload.setStatusCode(HttpStatus.BAD_REQUEST.value());
        responseDataUpload.setErrors(responseDataLiquida.getErrors());
        return new ResponseEntity<>(responseDataUpload, HttpStatus.BAD_REQUEST);
    }
}
