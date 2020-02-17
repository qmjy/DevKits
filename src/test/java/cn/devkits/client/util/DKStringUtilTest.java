package cn.devkits.client.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class DKStringUtilTest {

    @Test
    public void testIpCheck() {
        assertTrue(DKStringUtil.isIP("192.168.1.1"));
        assertFalse(DKStringUtil.isIP("2.3.5"));
    }


    @Test
    public void testIsReachable() {}

    @Test
    public void testXmlFormat() {
        String str =
                "<RequestData><HeadData><UserCode>sh1_admin</UserCode><UserName>sh1_admin</UserName><UserCompanyCode>3107</UserCompanyCode><UserCompanyName>上海分公司一部</UserCompanyName><RequestType>03</RequestType></HeadData><BodyData><ReportId>113100000033</ReportId><Insurant>a5rfg87</Insurant><NumberPlate>沪E78612</NumberPlate><EngineModel></EngineModel><CarVin></CarVin><AccidentDate>2011-02-25 15:07:00</AccidentDate><ReportDate>2011-02-25 15:07:00</ReportDate><Province>310000</Province><City>310100</City><District></District><AccidentPlace>1</AccidentPlace><AccidentLongitude></AccidentLongitude><AccidentLatitude></AccidentLatitude><SurveyLongitude></SurveyLongitude><SurveyLatitude></SurveyLatitude><SceneReportFlag></SceneReportFlag><Reporter></Reporter><ReporterTel></ReporterTel><SurveyPlace></SurveyPlace><OperatorId>3525</OperatorId><OperatorName>sh_admin</OperatorName><ReportDealId>30000800</ReportDealId><ReportDealName>江苏分公司</ReportDealName><CompanyName></CompanyName><CustomerTypeCode></CustomerTypeCode><ForcePolicyId>a5rfg87a5rfg87a5rfg87</ForcePolicyId><BizPolicyId></BizPolicyId><Index>0</Index><FieldName>5</FieldName></BodyData></RequestData>";
        String xmlFormat = DKStringUtil.xmlFormat(str);
        assertEquals(42, xmlFormat.split("\\n").length);
    }

    @Test
    public void testIsPositiveInt() {
        assertTrue(DKStringUtil.isPositiveInt("999999999999999"));
        assertTrue(DKStringUtil.isPositiveInt("1"));
        assertFalse(DKStringUtil.isPositiveInt("0"));
        assertFalse(DKStringUtil.isPositiveInt("-1"));
        assertFalse(DKStringUtil.isPositiveInt("j"));
        assertFalse(DKStringUtil.isPositiveInt("("));
        assertFalse(DKStringUtil.isPositiveInt(null));
        assertFalse(DKStringUtil.isPositiveInt(""));
    }

}
