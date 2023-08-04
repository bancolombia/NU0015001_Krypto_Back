package co.com.bancolombia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.bancolombia.domain.imagesdata.gateways.ImagesDataRepository;
import co.com.bancolombia.domain.administrator.gateways.TypeDocumentRepository;
import co.com.bancolombia.domain.products.gateways.ProductRepository;
import co.com.bancolombia.domain.savedocumenttrf.gateway.SaveDocumentTRFRepository;
import co.com.bancolombia.domain.trfs.gateways.TrfsRepository;
import co.com.bancolombia.usecase.savedocumenttrf.SaveDocumentTRFUseCase;
import co.com.bancolombia.usecase.util.GeneratePDF;

@Configuration
public class UseCaseConfig {

    @Bean
    public SaveDocumentTRFUseCase createPersistenceDocumentTRFUseCase (SaveDocumentTRFRepository saveDocumentTRFRepository,
                                                                       TrfsRepository trfsRepository, ImagesDataRepository imagesDataRespository, ProductRepository productRepository,
                                                                       TypeDocumentRepository typeDocumentRepository, GeneratePDF generatePDF) {
        return new SaveDocumentTRFUseCase(saveDocumentTRFRepository, trfsRepository, imagesDataRespository, productRepository,
                typeDocumentRepository, generatePDF);
    }
    @Bean
    public GeneratePDF createGeneratePDF() {
        return new GeneratePDF();
    }

}
