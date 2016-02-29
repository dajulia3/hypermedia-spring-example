package com.djulia.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.HalLinkDiscoverer;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AuthenticationController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity login(@RequestParam String username, @RequestParam String password){
        return null;
    }

    @RequestMapping("/")
    public HttpEntity<ApiRootResponse> root(){
        Link link = linkTo(methodOn(AuthenticationController.class).login("womp", "passwordwomp")).withRel("login");
        ApiRootResponse rootResponse = new ApiRootResponse("wassup");
        rootResponse.add(link);
        return new HttpEntity<ApiRootResponse>(rootResponse);
    }


    public static class ApiRootResponse extends ResourceSupport {
        private final String message;

        @JsonCreator
        public ApiRootResponse(@JsonProperty("message") String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class TemplatedLink extends Link {

    }
}
