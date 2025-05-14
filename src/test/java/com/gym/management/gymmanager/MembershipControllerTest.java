package com.gym.management.gymmanager;

import com.gym.management.gymmanager.controller.MembershipController;
import com.gym.management.gymmanager.model.Membership;
import com.gym.management.gymmanager.model.Person;
import com.gym.management.gymmanager.service.MembershipService;
import com.gym.management.gymmanager.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest {

    @InjectMocks
    private MembershipController membershipController;

    @Mock
    private MembershipService membershipService;

    @Mock
    private PersonService personService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(membershipController).build();
    }

    @Test
    void testAssignMembershipToPerson() throws Exception {
        Person person = new Person();
        person.setId(1L);
        Membership membership = new Membership();
        membership.setId(1L);
        membership.setPerson(person);

        when(personService.getPersonById(1L)).thenReturn(person);
        when(membershipService.saveMembership(any(Membership.class))).thenReturn(membership);

        mockMvc.perform(post("/api/memberships/person/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(membershipService, times(1)).saveMembership(any(Membership.class));
    }

    @Test
    void testGetMembershipById() throws Exception {
        Membership membership = new Membership();
        membership.setId(1L);

        when(membershipService.getMembershipById(1L)).thenReturn(membership);

        mockMvc.perform(get("/api/memberships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(membershipService, times(1)).getMembershipById(1L);
    }

    @Test
    void testAssignMembershipToNonExistingPerson() throws Exception {
        when(personService.getPersonById(999L)).thenReturn(null);
        Membership membership = new Membership();
        membership.setId(1L);
        mockMvc.perform(post("/api/memberships/person/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person with ID 999 not found."));
        verify(membershipService, times(0)).saveMembership(any(Membership.class));
    }


    @Test
    void testDeleteMembership() throws Exception {
        when(membershipService.deleteMembership(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/memberships/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person delete"));

        verify(membershipService, times(1)).deleteMembership(1L);
    }
}
