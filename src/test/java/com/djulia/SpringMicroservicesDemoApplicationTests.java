package com.djulia;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMicroservicesDemoApplication.class)
@WebAppConfiguration
public class SpringMicroservicesDemoApplicationTests {

	@Autowired
	WebApplicationContext context;
	private MockMvc mockMvc;

	@Before
	public void setup(){
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	@Test
	public void contextLoads() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/bet/1234?someFilter=hello")).andExpect(status().isOk())
				.andExpect(content().string("{\"amount\":892,\"_links\":{\"affordance-rel-yeam\":{\"href\":\"http://localhost/bet/{id}{?somw®eFilter}\",\"title\":null,\"baseUriTemplated\":true,\"rev\":null,\"templated\":true}}}"));
	}

}
