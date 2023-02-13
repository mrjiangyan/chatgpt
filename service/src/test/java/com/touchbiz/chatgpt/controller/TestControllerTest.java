package com.touchbiz.chatgpt.controller;

import com.github.plexpt.chatgpt.Chatbot;
import gg.acai.chatgpt.ChatGPT;
import gg.acai.chatgpt.Conversation;
import gg.acai.chatgpt.exception.ParsedExceptionEntry;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
public class TestControllerTest  {

    private String token = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..RQqKFPN5BexcgNp9.c4wnxEysNRxO585L9XznRkJoWtO6StwzVES2bT0WszRiL_v9nZ3AueJQlgolNiPcYOpat-Wus3CL0XbpxvYZ736mgKP88VPOr3HjbQ-lIlrwttR_fhuNYobxaUaz4zbtwupDV21T3qVLeZozcoIouy5cCOneOxj98ktkamUoPigWLij11I-goWJDv6YEzBDk4LuS_P_iK2Dtr7A7-zYUjw4h382pYK3VQu36H0upNBbRO1rd86lB3MRhDf7pohmBUE8P2Z7syLcDIwrOtYVnnov0drA9DnQW9ncVLJgbYuUb-96oQwxwIRE2gqLbQ3pwmssOi--EOCSKpE7sUkHlxUCUKLGDcHsK-rChS1AjmIvhot_qn9cAPsBSp_ZnXKjDey1MXVsEijOSt6Q08D_xUxbqVFcna0kcrHQgrv1tpnVZGQ4z70wkNRDiuMnahlcNeKjvpfLSipm0TMOqtW8IfzTdiLrqmmVrOT4tlpcTvBgXFd2GpjFl3-QkwA4Pm4vgrwWOPTiM4BFlszUhqaOExjUbWPNbHuThHO_FNnMQZPgVL__QjXgV7EOXMzwzBADcaKf5YdmG7QZ1SOhKGJTGoo9CWF0Cpb0w_n2h7db9sxpu4BHN5YsTL2DiCikuu4osTgEh-TMveiXCJDiF_0C3iA1XQW9Sr6s7W4ILW1H8hCdCdt5D8RQHO3X7qWz43eVKuIGHxG7SwYWoxosHbRFMB8_uD_pNejm_UAYUN4FCXoJgRm8DX4JmM6bl1lcuqWuhaxi5dXkCwLtkBMSz5-ct-zd9_xyBMnk4pzZqsp4AdHSGDi70GQeoq-TSVDAmXZS1NbXdcy2a-Ts607an9wu_G93wF0hYB0p095LDY_e0PSuCXF1_7rVv0baTVGXeTI5NmeC-1iRkz9W2bYHO7DOZg8uEde_c2iD8XZrrXrOisQJAvuBJJpXW4AYqj7TDrVdOHiYw1_B-YXbl6MjC9jHCV3fn1FlQTrfWQqS5JzLkBu0yH6D4tJ8hoacRq11w5Le35_TGIfJ0rJSCZsz7eI_J-FuKLiUqZUN4I6v3BK4_mXdHChRwRatZmbaZl6v812Cr292vMMczMqnK2KP6iT3HS68Cz_sHHJTjzwh0tT-cVvQW2VqfpuGSVKF5cQEJPLSIdZhVZ2a2ERWmNcgYhf-_wGKfYnk3RI71oJ1L38n2TT42lP0f6yaDSdDPiFMdB-WpRtX6jpLwf2aIanm3itJwRTGGBP26CyKTSMxqEo5gOeQJTm8RdVaL3gObbYyK6ruhnSNElhofb8l81_pPXB766Ma1iyrXZ88NVgqeEDCeLzGdLg_2o7a7IPz9yIJVmf2GNp5gPMWAIcjhYsNdA80aoeCQGcMAj9BimhZ02LzwSV2smbmfMATxZc2zDBGi17TcacydcAtmUF37GPa--shaM--bDYfzHwKvzw0FX3EQbOU5yN41Zi-c8tB87G6gGRxNRV5Vlgz8iDLeL9n-Mj_KJxG5m955qSTCMqKlVWXLapclFEdU5beWtsNfgmeX_n4H3AZHtbAVV4GbKnXuxJIZcZgqiXduZxKFnPERp-2t4f77G9qALH1f82z--hDHqYnlcV01olh2zMHt4tAd6cowCMwUK02lUUbcSsWfZ7tWjN1McHtOKEp8I_z5WCNlvoUpF4VmtmfmDkG6sTm7-yQ7x6KKe3vQJOWLocccN1Z2Cd4CO5MHF8MB-18bIt_KJwEWahUv4UrSJa3u93ovRcH1ahUKDLa9VcdsxeVaDYsz4lj2AMmpGfSvfd32nJdrUaJXP1brwRYRa-l6FTFZzr81bERkEGupcEaSQPKxKOpWf6oVPfM-H-l77_LcaTzO45MQOG7UeAj3pZEYWKqt-KoRJTiJ_0Au-Y1KtaGPIFdc9ZYIpEkBKuWEtjUqFkl5kyGoAdqDcjrrUiP5_uZshxf3v3frgWfmzqsDdMvn1xP_AL-k5XKGx1Xf9_kvooqWAMYQwg24HOwbV-jAflCHAsgZJ5oOnob-qCLhCaDHaSx8v9i3Nk0vG8Dn6-gEJZq2RMa-na5_jKvC03Q5jqW4G5XkQU4vrnPolH8QefJ7z0wJ34IAMIJCo9stf7yRqWxxapqX8YhRMRt8mqCE6MHHt58DW5-XPB5t9_sLdmdDTS19VEOEPeHJZmD-yuNCwxJs50jZ5kasEV0UcqHfEQwcyn3FmZuQmhh8Do6ETyi8yZwZd9tY4zzqXQlfByzle6PDMIXD0tddmW2PQyiwjOleZ1JnPu39wAYkAqLLRyx5CPXciTwsUJ91XyGZfy9PANcMPnq-0nWd0JF_E6rdl7MumYqtkL1hmt9_wdKJpYf0YaEiG3TSgPW-xUfbUh5xbAouspil8CUNzLHFp3Bv3jhPBiZfp4T176pF0VclqPXRaR1ZmEsj2Xf79XQXfkq3QFAvE6a2_hJHuDatyx5Vwe3in57Lgfkxlmwut0Wl7gvHN19w9QdRERNAzxSDhxPlcXO4s4VPaX-VkY70ci4O2AXEP4n0FoIP3uadWGyp_GyPFaD4ssL5fd38BXSPjb67tgnH1gvX606JGiYary-_6wZqr91SW-we2gwbUnKMJEwMkYBqvezOcNw8blp_3G16sSxu22Xneh9cAZVWp-jReEUoQq0hp98ChhLDRvkJvGY-Mt45AD3-VzWJdsL9.2LD8FRj5TlQqTiSKNk3rZg";

    private String cfClearance = "SaQtJke1.JIx_OHYh8FaL9LOg9QXwtdAfPQBWVpJAkg-1676195220-0-1-326c0e09.52ded7e8.656d51e9-160";

    private String userAgent =  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";
    @Test
    public void test1() {
//        accept: text/event-stream
//        accept-encoding: gzip, deflate, br
//        accept-language: zh-CN,zh;q=0.9
//        authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1UaEVOVUpHTkVNMVFURTRNMEZCTWpkQ05UZzVNRFUxUlRVd1FVSkRNRU13UmtGRVFrRXpSZyJ9.eyJodHRwczovL2FwaS5vcGVuYWkuY29tL3Byb2ZpbGUiOnsiZW1haWwiOiJtcmppYW5neWFuQGhvdG1haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImdlb2lwX2NvdW50cnkiOiJTRyJ9LCJodHRwczovL2FwaS5vcGVuYWkuY29tL2F1dGgiOnsidXNlcl9pZCI6InVzZXItOFB2NGczekJvdUNnN2dGN0lMdXdaR0VqIn0sImlzcyI6Imh0dHBzOi8vYXV0aDAub3BlbmFpLmNvbS8iLCJzdWIiOiJhdXRoMHw2M2U0ODdjNTgwMGY2ZTcwZmI0ODJhOTgiLCJhdWQiOlsiaHR0cHM6Ly9hcGkub3BlbmFpLmNvbS92MSIsImh0dHBzOi8vb3BlbmFpLm9wZW5haS5hdXRoMGFwcC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNjc2MTY3NDYzLCJleHAiOjE2NzczNzcwNjMsImF6cCI6IlRkSkljYmUxNldvVEh0Tjk1bnl5d2g1RTR5T282SXRHIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCBtb2RlbC5yZWFkIG1vZGVsLnJlcXVlc3Qgb3JnYW5pemF0aW9uLnJlYWQgb2ZmbGluZV9hY2Nlc3MifQ.mpSWiXYnCntZ-MlEV2FeCJnnTNCdV1Na0OyniGp71tEYhUvBq4fpFm4Imnq64XAhotoE6EYn_tET9-cFPlNW5DD_34Vi6a1BBia8vfDq24OjCTZrPYkVspoUQnYf8pH42Tbz20JtEFbFtWMKCcbHBhCx3EfNFuPCuYA7u4g24SNJQCBJFsQ_8OC202TCDa2hWReFELKeFnIwdx-N432i_E62LeM6WK7xBaMteWHFAfz-sJdmSB5SHeQ00_qMm9PRIG3L0Pg0HolU0Sa9f0so-tidKM2ZQlN4zU_s-EvYykuk8mMPP3jGKFJq-ZYMfMfN4NYjCSmxD4t5lpEghLDHMQ
//        content-length: 235
//        content-type: application/json
//        cookie: _gid=GA1.2.1767379607.1676085462; _ga_34B604LFFQ=GS1.1.1676166701.2.1.1676166995.60.0.0; _ga=GA1.2.1943452994.1676085462; __Host-next-auth.csrf-token=76954d2ab1d23c7755025996cf51de1db678028e8d28c334c71c2146575a6647%7Caed593489a664a92ae5166ea363088df1783e9c5f70e33a7afcf759d36946845; _cfuvid=svb06mr3zBcoVavwtlniZt6VwGIcO6StDvnm6._q.30-1676167424785-0-604800000; __Secure-next-auth.callback-url=https%3A%2F%2Fchat.openai.com%2F; __cf_bm=snOxaXMh7KvgvzSDFookR7e._QkzwIWMcmGmMLxFoWc-1676168131-0-AdhxiucQ2+EcdKOqkmYZd/08l68tg51ZXCEYmv7ekh7DlxoOKXiJLsWpU/OWQYPogBYaDGLnXIZeuXvWy3RhyctJepBaz3yQvtBvuScjX2rIxVcYmLslI4CxCwPsGg7n/x+hOqellI4im0m1mIBtZfQTXEXv17ht2ed5uAgUaruQgfu9+R2Mcl6YCpoKEUQObw==; cf_clearance=yjUN0ukZodRET2sWC3yFDslXOyDq2HiN0eCk2ntSQDo-1676168133-0-1-326c0e09.25275f75.656d51e9-160; __Secure-next-auth.session-token=eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..8Zdesv9t7TrrAH5Q.paWlfQiHRH0T6k-RSJEEKyxeZKWpDya3djY9g1r8C5V7G6hNwe2LeR-Va1q0R445byOPgsIT1QM-DSnQZVzxAnylCxqdtDPcf_89vmM9FUBs4YCrOoAOP4xEu6NZF3mP4LZeRGKSVKIZYCVI4iv8HdLK_mycShB5KA0f7F-9Gnj45SDI2UgCigbegpE-iWn5oLjrBiWXqBWMxhogt-04He2stcvgS429LolBI17Tezsyz5d2aGb-o2VRIJOH4BHbT-gDRjWB64B1aHrZ1PZ6NqAUoMqd8OdYZMRvEkzPUdNYIssobODK59dvvrulUmBDgSvSetpdsOKgtAfIc5eE5lmP73SmvH5KmOwBUNfm91l7OaeZ-Htm1R51s3ZCraCSPMIcrnlV3hYIhvx4rGiXPPOF8kOpcjk-N5N-7l502kNlpXA167qDyTy7v-tmwUzjQvx9-Z5-LKljN_8ScBg1UuynfhhOdBRcW0ovC-nbTobgL4ZTVR2IQIHVw3DrbdFh2Ufaykn5t4wyhJb_smaqtI7mN71872yVWrNJ29PoKnf_J3cKEIEqR1MsoGbZF6_NCrSqCCGU3Ta7lcIxfMmf9dBub_VwJLbDqc-xplEAdM_v6EQvg8hx2Nq7oYV9u_ozZeZg5rUKIlUEvnZT3UjMbxlNNgO22AhoqjcaqyqAPvuA2b_MQOX1tMPPYrWAAda_WLrzojL-zNWpUIm38f99SZQObRGybQX8OhgYApzFjHFi9T1_zaBhMP_aK1-aM8iFxqfTGkxRoyjbqNxyIDMok0MycvG05Bu2UYoMhRjORKnB_AG0Me7rgYzkcB4y5al-lwi4nw30_GXl1D8ixVJDRJq7juVj_Ylkkf8ZRy3AuF79qYnX9GyTuZza6xHOV1Nde4J2kILek3YhdPDfTvG_OYvZMsGyYWf_Ukt_52oKn2SQmqHeGqfjbb25ru2SHwEh9ZywJAsHEdTMaMbQWVwB1Men8Ipjpw4x_r7Ga1PQB2-Jc4nv1nvMow6iPjZ6sZgiZG1dEa89G1caLjfSY_suXgo6FfsQK7_4L_iia-_cDcWJsdpqmO9hNRCeNaFy0NMdVWLGFSGFQNNOAcisUjgELV7LW57XtqwVaPfmLj_4cUWTvpmOP1PDlp3r95rCcoKlCQ1qPbehBs4dCe5JWgiRIF-GPGarYudIPwLQAzot-EXXSLo9xeCd1hDDMoVEJV-3uCPEwYLtmV6XvVzYnjgRsCaazfGDNLPpUT4_dMcf660gIoJBQQoo1NmuWdWCKAAl2TzjeyJeCMNTinZzCcVh2HLOffapJkTMDzyFE2sMnKHnZTNorwCmAtxBNTGbMTkW-eTvsCRpAyfFomNuon9dMTAIHIJjRpiO9trsT1osnkDCdAH6Aj_xv52fgSQAqjv5j1JJ8RUx8v4w7OJayAizk6VO55wUv9BsC6S60yDMpZIMEaHUzWFFZOD-_hXq9_PPftUMeMjZlS34DgkkByS3m9TPML6kki2d0kbiAonMNx7p8tXvyGwmHkoTsGpRt62DQ1g-Figs_pcAYHqh3KV0jN8ZJnwdFNnFL-BN_-JLEaPtZCi78vXCOWDzjXndY7FhUQqWcKnQYDUQLroqsr998t0xkDXMpXVTFLCvFK-0bzHf16VybXlGkEAnQ3rAyaHX22KSruqm2QSwjzUHkOdSvBYEGSBCAf2djUnQ3aBIatn6QUmF6wseVBevO9-GHtr_gedWfjQ1qtiIhdq0Uz5eEJhLKjY_NCKM230EtLcvIjqXQ6X1s8ztw1VW9AlW_YhNmyxnoltSLBXgXHwogZBaX4KxOw6r_JTKFweKUd_NkqAxkybPH7c_bQAwHPGGE5YwkOWhtwrBf10lWG7jupyYkEaQjinUntGPVO_10nOy8ZsEtLM-JZ3tKkI8PjvhDwwP-Q7MZ5wKZeELIZ5KR32M1RGtKbNemRtUe0dOqrJ24XHNSaVrqmpXuUklbJTf8qSTHrYTJdoqIx9gO0qiHoRlREzgF7l-3jals_ebCShizc_F2sZRXMFsDggBzSdDKO0RET8bV_nZAsg3X2qydKSX3EFjYcZWvmm3Lms63RKXtNgVwPnLDzNa4gwVkdSIpSoUdSFLjNRJggutAv4WeLvPlzIdUM1MJyTKySA6xiJMUzfUziX_LlyV1O3j2sXoefA_fdm51LnQ2N268QaLExD4mVxxuebC1gpaRQH5D6eena_oRVnAYb4KE2cuppvduA9cEsWzpbps794dJkuaCP7tpBcEbR2lOQundcrRdP2CGPnp1v3DTdUBFUuWEwiIM4mu1SPdxB1jZz5TWo3J0YakRO0lYKQ_HOQuEDnJ9Opg8XGcpLTpUimf1ju3-CMMn-s6ajEirUSdcT4fcgeDOwS07b_PoohHria-JKM8iWwVx6QZnWULV3aPW8VPM-bhtbC-BDkFUSBiQa3L_06KC5FHCcdiF6ULXbiJevgQpIJzL2iP3-nYwR7dNlQRjQBi7YW8rbs0A8tp1rZOjtKuMH7LJN4oFSQHJ52CYvOtvHHQm4w3lwHhv5RGtwnG5UZzjvczfZ0tJkuBIDCpmqKwigSfiu8x7AIdSIBL_wDmya2WDEpBdMzMV2HDaO7k2GVHyivK4OgUj4qk4Mm67UnW5YqRG-1et9XFFay8MAV13KbHSwFufCAOYMdA.52AzLD9LIbxgM5YsuqSO_A
//        origin: https://chat.openai.com
//        referer: https://chat.openai.com/chat
//        sec-ch-ua: "Not_A Brand";v="99", "Google Chrome";v="109", "Chromium";v="109"
//        sec-ch-ua-mobile: ?0
//        sec-ch-ua-platform: "macOS"
//        sec-fetch-dest: empty
//        sec-fetch-mode: cors
//        sec-fetch-site: same-origin
//        user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36
//        x-openai-assistant-app-id


        Chatbot chatbot = new Chatbot(token,
                cfClearance,  userAgent);
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("accept","text/event-stream");
        headers.put("accept-encoding","gzip, deflate, br");
        headers.put("accept-language","zh-CN,zh;q=0.9");
        headers.put("authorization","Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1UaEVOVUpHTkVNMVFURTRNMEZCTWpkQ05UZzVNRFUxUlRVd1FVSkRNRU13UmtGRVFrRXpSZyJ9.eyJodHRwczovL2FwaS5vcGVuYWkuY29tL3Byb2ZpbGUiOnsiZW1haWwiOiJtcmppYW5neWFuQGhvdG1haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImdlb2lwX2NvdW50cnkiOiJTRyJ9LCJodHRwczovL2FwaS5vcGVuYWkuY29tL2F1dGgiOnsidXNlcl9pZCI6InVzZXItOFB2NGczekJvdUNnN2dGN0lMdXdaR0VqIn0sImlzcyI6Imh0dHBzOi8vYXV0aDAub3BlbmFpLmNvbS8iLCJzdWIiOiJhdXRoMHw2M2U0ODdjNTgwMGY2ZTcwZmI0ODJhOTgiLCJhdWQiOlsiaHR0cHM6Ly9hcGkub3BlbmFpLmNvbS92MSIsImh0dHBzOi8vb3BlbmFpLm9wZW5haS5hdXRoMGFwcC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNjc2MTY3NDYzLCJleHAiOjE2NzczNzcwNjMsImF6cCI6IlRkSkljYmUxNldvVEh0Tjk1bnl5d2g1RTR5T282SXRHIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCBtb2RlbC5yZWFkIG1vZGVsLnJlcXVlc3Qgb3JnYW5pemF0aW9uLnJlYWQgb2ZmbGluZV9hY2Nlc3MifQ.mpSWiXYnCntZ-MlEV2FeCJnnTNCdV1Na0OyniGp71tEYhUvBq4fpFm4Imnq64XAhotoE6EYn_tET9-cFPlNW5DD_34Vi6a1BBia8vfDq24OjCTZrPYkVspoUQnYf8pH42Tbz20JtEFbFtWMKCcbHBhCx3EfNFuPCuYA7u4g24SNJQCBJFsQ_8OC202TCDa2hWReFELKeFnIwdx-N432i_E62LeM6WK7xBaMteWHFAfz-sJdmSB5SHeQ00_qMm9PRIG3L0Pg0HolU0Sa9f0so-tidKM2ZQlN4zU_s-EvYykuk8mMPP3jGKFJq-ZYMfMfN4NYjCSmxD4t5lpEghLDHMQ");
        headers.put("content-type","application/json");
        headers.put("cookie","_gid=GA1.2.1767379607.1676085462; _ga_34B604LFFQ=GS1.1.1676166701.2.1.1676166995.60.0.0; _ga=GA1.2.1943452994.1676085462; __Host-next-auth.csrf-token=76954d2ab1d23c7755025996cf51de1db678028e8d28c334c71c2146575a6647%7Caed593489a664a92ae5166ea363088df1783e9c5f70e33a7afcf759d36946845; _cfuvid=svb06mr3zBcoVavwtlniZt6VwGIcO6StDvnm6._q.30-1676167424785-0-604800000; __Secure-next-auth.callback-url=https%3A%2F%2Fchat.openai.com%2F; __cf_bm=snOxaXMh7KvgvzSDFookR7e._QkzwIWMcmGmMLxFoWc-1676168131-0-AdhxiucQ2+EcdKOqkmYZd/08l68tg51ZXCEYmv7ekh7DlxoOKXiJLsWpU/OWQYPogBYaDGLnXIZeuXvWy3RhyctJepBaz3yQvtBvuScjX2rIxVcYmLslI4CxCwPsGg7n/x+hOqellI4im0m1mIBtZfQTXEXv17ht2ed5uAgUaruQgfu9+R2Mcl6YCpoKEUQObw==; cf_clearance=yjUN0ukZodRET2sWC3yFDslXOyDq2HiN0eCk2ntSQDo-1676168133-0-1-326c0e09.25275f75.656d51e9-160; __Secure-next-auth.session-token=eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..8Zdesv9t7TrrAH5Q.paWlfQiHRH0T6k-RSJEEKyxeZKWpDya3djY9g1r8C5V7G6hNwe2LeR-Va1q0R445byOPgsIT1QM-DSnQZVzxAnylCxqdtDPcf_89vmM9FUBs4YCrOoAOP4xEu6NZF3mP4LZeRGKSVKIZYCVI4iv8HdLK_mycShB5KA0f7F-9Gnj45SDI2UgCigbegpE-iWn5oLjrBiWXqBWMxhogt-04He2stcvgS429LolBI17Tezsyz5d2aGb-o2VRIJOH4BHbT-gDRjWB64B1aHrZ1PZ6NqAUoMqd8OdYZMRvEkzPUdNYIssobODK59dvvrulUmBDgSvSetpdsOKgtAfIc5eE5lmP73SmvH5KmOwBUNfm91l7OaeZ-Htm1R51s3ZCraCSPMIcrnlV3hYIhvx4rGiXPPOF8kOpcjk-N5N-7l502kNlpXA167qDyTy7v-tmwUzjQvx9-Z5-LKljN_8ScBg1UuynfhhOdBRcW0ovC-nbTobgL4ZTVR2IQIHVw3DrbdFh2Ufaykn5t4wyhJb_smaqtI7mN71872yVWrNJ29PoKnf_J3cKEIEqR1MsoGbZF6_NCrSqCCGU3Ta7lcIxfMmf9dBub_VwJLbDqc-xplEAdM_v6EQvg8hx2Nq7oYV9u_ozZeZg5rUKIlUEvnZT3UjMbxlNNgO22AhoqjcaqyqAPvuA2b_MQOX1tMPPYrWAAda_WLrzojL-zNWpUIm38f99SZQObRGybQX8OhgYApzFjHFi9T1_zaBhMP_aK1-aM8iFxqfTGkxRoyjbqNxyIDMok0MycvG05Bu2UYoMhRjORKnB_AG0Me7rgYzkcB4y5al-lwi4nw30_GXl1D8ixVJDRJq7juVj_Ylkkf8ZRy3AuF79qYnX9GyTuZza6xHOV1Nde4J2kILek3YhdPDfTvG_OYvZMsGyYWf_Ukt_52oKn2SQmqHeGqfjbb25ru2SHwEh9ZywJAsHEdTMaMbQWVwB1Men8Ipjpw4x_r7Ga1PQB2-Jc4nv1nvMow6iPjZ6sZgiZG1dEa89G1caLjfSY_suXgo6FfsQK7_4L_iia-_cDcWJsdpqmO9hNRCeNaFy0NMdVWLGFSGFQNNOAcisUjgELV7LW57XtqwVaPfmLj_4cUWTvpmOP1PDlp3r95rCcoKlCQ1qPbehBs4dCe5JWgiRIF-GPGarYudIPwLQAzot-EXXSLo9xeCd1hDDMoVEJV-3uCPEwYLtmV6XvVzYnjgRsCaazfGDNLPpUT4_dMcf660gIoJBQQoo1NmuWdWCKAAl2TzjeyJeCMNTinZzCcVh2HLOffapJkTMDzyFE2sMnKHnZTNorwCmAtxBNTGbMTkW-eTvsCRpAyfFomNuon9dMTAIHIJjRpiO9trsT1osnkDCdAH6Aj_xv52fgSQAqjv5j1JJ8RUx8v4w7OJayAizk6VO55wUv9BsC6S60yDMpZIMEaHUzWFFZOD-_hXq9_PPftUMeMjZlS34DgkkByS3m9TPML6kki2d0kbiAonMNx7p8tXvyGwmHkoTsGpRt62DQ1g-Figs_pcAYHqh3KV0jN8ZJnwdFNnFL-BN_-JLEaPtZCi78vXCOWDzjXndY7FhUQqWcKnQYDUQLroqsr998t0xkDXMpXVTFLCvFK-0bzHf16VybXlGkEAnQ3rAyaHX22KSruqm2QSwjzUHkOdSvBYEGSBCAf2djUnQ3aBIatn6QUmF6wseVBevO9-GHtr_gedWfjQ1qtiIhdq0Uz5eEJhLKjY_NCKM230EtLcvIjqXQ6X1s8ztw1VW9AlW_YhNmyxnoltSLBXgXHwogZBaX4KxOw6r_JTKFweKUd_NkqAxkybPH7c_bQAwHPGGE5YwkOWhtwrBf10lWG7jupyYkEaQjinUntGPVO_10nOy8ZsEtLM-JZ3tKkI8PjvhDwwP-Q7MZ5wKZeELIZ5KR32M1RGtKbNemRtUe0dOqrJ24XHNSaVrqmpXuUklbJTf8qSTHrYTJdoqIx9gO0qiHoRlREzgF7l-3jals_ebCShizc_F2sZRXMFsDggBzSdDKO0RET8bV_nZAsg3X2qydKSX3EFjYcZWvmm3Lms63RKXtNgVwPnLDzNa4gwVkdSIpSoUdSFLjNRJggutAv4WeLvPlzIdUM1MJyTKySA6xiJMUzfUziX_LlyV1O3j2sXoefA_fdm51LnQ2N268QaLExD4mVxxuebC1gpaRQH5D6eena_oRVnAYb4KE2cuppvduA9cEsWzpbps794dJkuaCP7tpBcEbR2lOQundcrRdP2CGPnp1v3DTdUBFUuWEwiIM4mu1SPdxB1jZz5TWo3J0YakRO0lYKQ_HOQuEDnJ9Opg8XGcpLTpUimf1ju3-CMMn-s6ajEirUSdcT4fcgeDOwS07b_PoohHria-JKM8iWwVx6QZnWULV3aPW8VPM-bhtbC-BDkFUSBiQa3L_06KC5FHCcdiF6ULXbiJevgQpIJzL2iP3-nYwR7dNlQRjQBi7YW8rbs0A8tp1rZOjtKuMH7LJN4oFSQHJ52CYvOtvHHQm4w3lwHhv5RGtwnG5UZzjvczfZ0tJkuBIDCpmqKwigSfiu8x7AIdSIBL_wDmya2WDEpBdMzMV2HDaO7k2GVHyivK4OgUj4qk4Mm67UnW5YqRG-1et9XFFay8MAV13KbHSwFufCAOYMdA.52AzLD9LIbxgM5YsuqSO_A");
        headers.put("sec-ch-ua-platform","macOS");
        headers.put("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
//        chatbot.setHeaders(headers);
        Map<String, Object> chatResponse = chatbot.getChatResponse("hello");
        log.info("message:{}", chatResponse);
    }

    @Test
    public void testChatGpt(){
        ChatGPT chatGpt = ChatGPT.newBuilder()
                .sessionToken(token) // required field: get from cookies
                .cfClearance(cfClearance) // required to bypass Cloudflare: get from cookies
                .userAgent(userAgent) // required to bypass Cloudflare: google 'what is my user agent'
                .addExceptionAttribute(new ParsedExceptionEntry("exception keyword", Exception.class)) // optional: adds an exception attribute
                .connectTimeout(60L) // optional: specify custom connection timeout limit
                .readTimeout(30L) // optional: specify custom read timeout limit
                .writeTimeout(30L) // optional: specify custom write timeout limit
                .build(); // builds the ChatGPT client

        chatGpt.getComplexAccessCache().refreshAccessToken() // refreshing cache and verifies session token
                .whenComplete((accessToken) -> { // called when the promise is completed, not required
                    log.info("Access token: " + accessToken);
                });

        Conversation conversation = chatGpt.createConversation();
        conversation.sendMessageAsync("Hello!")
                .whenComplete((response) -> { // called when the promise is completed with its response
                    log.info("Response: " + response.getMessage());
                });
    }
}