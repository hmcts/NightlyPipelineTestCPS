package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class D8DocumentCaseToCCDMapperTest {

    @Autowired
    private DivorceCaseToCCDMapper mapper;

    @Test
    public void shouldMapAllAndTransformAllFieldsForD8DocumentGenerated() throws URISyntaxException, IOException {

        CoreCaseData expectedCoreCaseData = (CoreCaseData) DivorceCaseToCCDMapperTestUtil
            .jsonToObject("fixtures/ccdmapping/d8document.json", D8DocumentCaseToCCDMapperTest.class,
                CoreCaseData.class);
        expectedCoreCaseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));

        DivorceSession divorceSession = (DivorceSession) DivorceCaseToCCDMapperTestUtil
            .jsonToObject("divorce-payload-json/d8-document.json", D8DocumentCaseToCCDMapperTest.class,
                DivorceSession.class);

        CoreCaseData actualCoreCaseData = mapper.divorceCaseDataToCourtCaseData(divorceSession);

        assertThat(actualCoreCaseData, samePropertyValuesAs(expectedCoreCaseData));
    }

}
