package com.oms.serverapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.service.ServiceManService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@RunWith(SpringRunner.class)
@WebMvcTest(ServiceManController.class)
class ServiceManControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServiceManService service;

    private List<ServiceMan> allServiceMan;

    @BeforeEach
    public void init() {
        ServiceMan jannowak = new ServiceMan("Jan", "Nowak", "123456789", "jannowak", "jannowak", "loc", 2);
        jannowak.setId(1L);
        ServiceMan maciejkowalski = new ServiceMan("Maciej", "Kowalski", "987654321", "maciejkowalski", "maciejkowalski", "loc", 6);
        maciejkowalski.setId(2L);
        ServiceMan pawelwozniak = new ServiceMan("Pawel", "Wozniak", "111222333", "pawelwozniak", "pawelwozniak", "loc", 7);
        pawelwozniak.setId(3L);
        allServiceMan = Arrays.asList(jannowak, maciejkowalski, pawelwozniak);
    }

    @Test
    public void getAllServiceMan() throws Exception {
        given(service.getAllServiceMan()).willReturn(allServiceMan);
        RequestBuilder request = get("/api/serviceman").contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(allServiceMan.get(0).getId().intValue()))) //?
                .andExpect(jsonPath("$[0].firstName", is(allServiceMan.get(0).getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(allServiceMan.get(0).getLastName())))
                .andExpect(jsonPath("$[0].phoneNumber", is(allServiceMan.get(0).getPhoneNumber())))
                .andExpect(jsonPath("$[0].username", is(allServiceMan.get(0).getUsername())))
                .andExpect(jsonPath("$[0].password", is(allServiceMan.get(0).getPassword())))
                .andExpect(jsonPath("$[0].startLocalization", is(allServiceMan.get(0).getStartLocalization())))
                .andExpect(jsonPath("$[0].experience", is(allServiceMan.get(0).getExperience())))
                .andExpect(jsonPath("$[1].id", is(2))) //?
                .andExpect(jsonPath("$[1].firstName", is(allServiceMan.get(1).getFirstName())))
                .andExpect(jsonPath("$[1].lastName", is(allServiceMan.get(1).getLastName())))
                .andExpect(jsonPath("$[1].phoneNumber", is(allServiceMan.get(1).getPhoneNumber())))
                .andExpect(jsonPath("$[1].username", is(allServiceMan.get(1).getUsername())))
                .andExpect(jsonPath("$[1].password", is(allServiceMan.get(1).getPassword())))
                .andExpect(jsonPath("$[1].startLocalization", is(allServiceMan.get(1).getStartLocalization())))
                .andExpect(jsonPath("$[1].experience", is(allServiceMan.get(1).getExperience())))
                .andExpect(jsonPath("$[2].id", is(3))) //?
                .andExpect(jsonPath("$[2].firstName", is(allServiceMan.get(2).getFirstName())))
                .andExpect(jsonPath("$[2].lastName", is(allServiceMan.get(2).getLastName())))
                .andExpect(jsonPath("$[2].phoneNumber", is(allServiceMan.get(2).getPhoneNumber())))
                .andExpect(jsonPath("$[2].username", is(allServiceMan.get(2).getUsername())))
                .andExpect(jsonPath("$[2].password", is(allServiceMan.get(2).getPassword())))
                .andExpect(jsonPath("$[2].startLocalization", is(allServiceMan.get(2).getStartLocalization())))
                .andExpect(jsonPath("$[2].experience", is(allServiceMan.get(2).getExperience())));
        verify(service, VerificationModeFactory.times(1)).getAllServiceMan();
        reset(service);
    }

    @Test
    public void getServiceManById() throws Exception {
        given(service.getServiceManById(Mockito.anyLong())).willReturn(allServiceMan.get(0));

        RequestBuilder request = get("/api/serviceman/{id}", 1L).contentType(MediaType.APPLICATION_JSON);
        mvc.perform(request)
                .andExpect(status().isOk())
                //.andDo(print())
                .andExpect(jsonPath("$.id", is(1))) //?
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.lastName", is("Nowak")))
                .andExpect(jsonPath("$.phoneNumber", is("123456789")))
                .andExpect(jsonPath("$.username", is("jannowak")))
                .andExpect(jsonPath("$.password", is("jannowak")))
                .andExpect(jsonPath("$.startLocalization", is("loc")))
                .andExpect(jsonPath("$.experience", is(2))); //?
        verify(service, VerificationModeFactory.times(1)).getServiceManById(1L);
        reset(service);
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addServiceMan() throws Exception {
        ServiceMan newServiceMan =  new ServiceMan("Jan", "Nowak", "123456789", "jannowak", "jannowak", "loc", 2);
        newServiceMan.setId(1L);
        given(service.addServiceMan(Mockito.any(ServiceMan.class))).willReturn(allServiceMan.get(0));

        System.out.println("contenet " + asJsonString(newServiceMan));
        RequestBuilder request = post("/api/serviceman/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(newServiceMan));
        mvc.perform(request)
                .andExpect(status().isOk())
                //.andDo(print())
                .andExpect(jsonPath("$.id", is(1))) //?
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.lastName", is("Nowak")))
                .andExpect(jsonPath("$.phoneNumber", is("123456789")))
                .andExpect(jsonPath("$.username", is("jannowak")))
                .andExpect(jsonPath("$.password", is("jannowak")))
                .andExpect(jsonPath("$.startLocalization", is("loc")))
                .andExpect(jsonPath("$.experience", is(2)));
        verify(service, VerificationModeFactory.times(1)).addServiceMan(refEq(newServiceMan));
        reset(service);

    }

    @Test
    void deleteServiceManById() throws Exception {
        RequestBuilder request = delete("/api/serviceman/{id}", 1L).contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void updateServiceManById() {
    }
}