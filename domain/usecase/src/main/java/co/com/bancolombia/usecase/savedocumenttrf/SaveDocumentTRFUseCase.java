package co.com.bancolombia.usecase.savedocumenttrf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;

import co.com.bancolombia.usecase.util.GeneratePDF;

@RequiredArgsConstructor
public class SaveDocumentTRFUseCase {

    private final GeneratePDF generatePDF;

    private static final Logger LOGGER = LoggerFactory.getLogger(SaveDocumentTRFUseCase.class);
    public SaveDocumentTRFComplete transmitirDocumento(String numeroConsecutivo) throws Exception {

        List<Trfs> trfs = Optional.ofNullable(trfsRepository.findByNumberConsecutive(numeroConsecutivo))
                .orElseThrow(() -> new NotFoundException(ConstantsErrors.ERROR_CONSECUTIVE_NOT_EXIST));

        List<String> imagesBase64 = imagesDataRepository.getListImagesByConsecutive(
                "ACTIVO/" + trfs.get(0).getTipoProducto() + "_" + trfs.get(0).getNumeroProducto() +
                        "/TRFCompleta",numeroConsecutivo);

        Document trfCompletePDF = generatePDF.convertImagesPdf(imagesBase64);

        byte[] bytesImageTrf = org.apache.commons.codec.binary.Base64.encodeBase64((trfCompletePDF.toString()).getBytes());

        String productName = productRepository.findProductByCodigoActive(trfs.get(0).getTipoProducto()).getNombreProducto();

        String typeDocumentacronym = typeDocumentRepository.
                findByDocumentCode(trfs.get(0).getTipoIdentificacion()).getSiglas();

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = LocalDateTime.now().format(formatter);
        var tdcDocumentTrf = TDCDocumentTRF.builder().fechaExpedicionDocumento(formatDateTime)
                .numeroConsecutivo(numeroConsecutivo).tipoProducto(trfs.get(0).getTipoProducto())
                .nombreProducto(productName).tipoIdentificacionTitular(typeDocumentacronym)
                .numeroIdentificacionTitular(trfs.get(0).getNumIdentificacionTitular())
                .archivoDocumentoTRF(bytesImageTrf).nombreDocumentoTRF(trfs.get(0).getNumeroConsecutivo() + ".pdf")
                .build();

        return saveDocumentTRFRepository.saveDocument(tdcDocumentTrf);
    }

}
