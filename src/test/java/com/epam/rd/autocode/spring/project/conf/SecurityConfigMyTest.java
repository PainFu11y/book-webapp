package com.epam.rd.autocode.spring.project.conf;

import com.epam.rd.autocode.spring.project.controller.BookController;
import com.epam.rd.autocode.spring.project.mapper.BookMapper;
import com.epam.rd.autocode.spring.project.security.JwtFilter;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.impl.auth.CustomOAuth2UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(controllers = BookController.class)
class SecurityConfigMyTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private BookMapper bookMapper;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;


    @Test
    void protectedEndpointsShouldRedirectToOauth2WithoutAuth() throws Exception {
        mockMvc.perform(get("/books/some-book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://localhost/oauth2/authorization/google"));

        mockMvc.perform(get("/employees"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://localhost/oauth2/authorization/google"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeEndpointsShouldBeAccessibleForEmployee() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void clientEndpointsShouldBeAccessibleForClient() throws Exception {
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void userCanAccessBookByName() throws Exception {
        mockMvc.perform(get("/books/some-book"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void publicEndpointsShouldBeAccessibleWithMockUser() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }


}
