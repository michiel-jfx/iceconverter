package nl.dotjava.javafx.holidayconverter;

import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyRate;
import nl.dotjava.javafx.support.ConvertSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class HoliCurControllerTest {

    private HoliCurController controller;

    @Mock
    private ConvertSupport mockConvertSupport;

    private static final BigDecimal ICELAND_FROM = new BigDecimal("0.0068");
    private static final BigDecimal USD_FROM = new BigDecimal("0.875");
    private static final BigDecimal USD_TO = new BigDecimal("1.142857143");

    @BeforeEach
    void setUp() throws Exception {
        openMocks(this);
        // setup controller and inject the mocked ConvertSupport
        controller = new HoliCurController() {
            @Override
            public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
                // Skip fxml initialization
            }
        };

        // use reflection to set the mocked ConvertSupport
        java.lang.reflect.Field convertSupportField = HoliCurController.class.getDeclaredField("convertSupport");
        convertSupportField.setAccessible(true);
        convertSupportField.set(controller, mockConvertSupport);
        setupTestCurrencies();
    }

    private void setupTestCurrencies() {
        CurrencyRate eurCurrencyRate = new CurrencyRate(Currency.EUR);
        eurCurrencyRate.setValueFrom(BigDecimal.ONE);
        CurrencyRate iskCurrencyRate = new CurrencyRate(Currency.ISK);
        iskCurrencyRate.setValueFrom(ICELAND_FROM);
        CurrencyRate usdCurrencyRate = new CurrencyRate(Currency.USD);
        usdCurrencyRate.setValueFrom(USD_FROM);
        // add list with ISK, EUR and USD currency rates to the controller
        controller.setCurrencyMap(Arrays.asList(iskCurrencyRate, usdCurrencyRate, eurCurrencyRate));
    }

    @Test
    void testDefaultCurrencyConversion() {
        controller.setCurrencyToUse("ISK");
        ArgumentCaptor<CurrencyRate> captor = ArgumentCaptor.forClass(CurrencyRate.class);
        verify(mockConvertSupport).setCurrency(captor.capture());
        CurrencyRate capturedRate = captor.getValue();

        // verify capture setup
        assertThat(capturedRate.getName()).isEqualTo("ISK");
        assertThat(capturedRate.getTargetSymbol()).isEqualTo("€ ");

        // expected result should be ISK.valueFrom
        BigDecimal expectedValue = ICELAND_FROM.multiply(BigDecimal.ONE, new MathContext(10, RoundingMode.HALF_UP));
        assertThat(capturedRate.getValueFrom()).isEqualByComparingTo(expectedValue);
    }

    @Test
    void testConvertCurrencyFromOneToAnotherDifferentCurrency() {
        controller.setCurrencyToUse("ISK", "USD");
        ArgumentCaptor<CurrencyRate> captor = ArgumentCaptor.forClass(CurrencyRate.class);
        verify(mockConvertSupport).setCurrency(captor.capture());
        CurrencyRate capturedRate = captor.getValue();

        // verify capture setup
        assertThat(capturedRate.getName()).isEqualTo("ISK");
        assertThat(capturedRate.getTargetSymbol()).isEqualTo("$ ");

        // when converting two different currency rates, the first is converted to euro then converted from euro to the
        // second, which means the first currencyRate valueFrom is multiplied by the second currencyRate valueTo, this
        // means one CurrencyRate to do both steps in one, then the result is: ICELAND_FROM rate * USD_TO rate
        // using the CurrencyRates in this way, you can convert any currency to any currency :-)
        BigDecimal expectedValue = ICELAND_FROM.multiply(USD_TO, new MathContext(10, RoundingMode.HALF_UP));
        BigDecimal difference = capturedRate.getValueFrom().subtract(expectedValue).abs();
        assertThat(difference).isLessThanOrEqualTo(new BigDecimal("0.000000001"));
    }
}
