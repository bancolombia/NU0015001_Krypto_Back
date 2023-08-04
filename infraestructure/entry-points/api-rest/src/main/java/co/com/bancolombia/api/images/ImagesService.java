package co.com.bancolombia.api.images;

import java.io.FileNotFoundException;

import javax.validation.Valid;

import com.itextpdf.text.DocumentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "external/imagesgeneral")
@RequiredArgsConstructor
public class ImagesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesService.class);
    @PostMapping("/upimfile")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Images upload succesfully",),
            @ApiResponse(responseCode = "400", description = "Invalid request") })
    public ResponseEntity<ResponseData> uploadFile(@Valid @RequestBody RequestData<TrfsImages> requestData)
            throws DocumentException, TrfsException, FileNotFoundException {

        Metadata metadata = MetadataRequest.builder().messageId(requestData.getMetaData().getMessageId())
                .systemId(requestData.getMetaData().getSystemId()).build();

        LOGGER.info(String.format(Constants.METADATA_REQUEST,metadata.getMessageId(), metadata.getSystemId()));

        ResponseData responseData = new ResponseData();

        ResponseData responseDataUpload = new ResponseData();
        requestData.getData().setMessageId(requestData.getMetaData().getMessageId());
        ResponseData responseDataLiquida = saveImagesTrfs.uploadFile(requestData.getData());
        responseDataUpload.setData(responseDataLiquida.getData());
        responseData.setMetaData(new MetadataResponse(metadata.getMessageId(), metadata.getSystemId()));
        if (responseDataLiquida.getErrors() == null) {
            ResponseData responseDataImage = saveTrfs.matchTrfsImages(requestData.getData().getNumeroConsecutivo());

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
