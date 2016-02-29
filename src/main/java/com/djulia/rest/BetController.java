package com.djulia.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.escalon.hypermedia.affordance.Affordance;
import de.escalon.hypermedia.spring.AffordanceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
/*
This is just an example for creating templated links, and links with actions.
I treated it as a scratch pad. The links on these resources are totally wonky and not meant to be taken as an example
 of how to model links in a REST API! 
 */
@RestController
@ExposesResourceFor(BetController.Bet.class)
public class BetController {
    private final EntityLinks entityLinks;

    @Autowired
    public BetController(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @RequestMapping(value = "/bets")
    public BetIndex listBets() {
        Link collectionLink = this.entityLinks.linkToCollectionResource(Bet.class);
        Affordance affordance = AffordanceBuilder.linkTo(methodOn(BetController.class).createBet(new CreateBetRequest(null))).withRel("create-bet");
        Link createBetForm = new LinkWithMethod(affordance.getHref(), "create-member", HttpMethod.POST);
        return new BetIndex(asList(new Bet(210)), asList(collectionLink.withSelfRel(), createBetForm));
    }

    @RequestMapping(value = "/bet/{id}")
    public Resource<Bet> getBet(@RequestParam String someFilter, @PathVariable String id) {
        Affordance affordance = betLinkTemplate();
        return new Resource<Bet>(new Bet(892), affordance);
    }

    private Affordance betLinkTemplate() {
        return AffordanceBuilder.linkTo(methodOn(BetController.class).getBet(null, null)).withRel("affordance-rel-yeam");
    }


    @RequestMapping(value = "/bets", method = RequestMethod.POST)
    public CreateBetResponse createBet(@RequestBody CreateBetRequest createRequest){
        return new CreateBetResponse();
    }

    public static class CreateBetResponse extends ResourceSupport{
        private Bet bet;


        public CreateBetResponse(Bet bet) {
            this.bet = bet;
            this.add();
        }

    }


    public static class CreateBetRequest {
        private Integer amount;

        @JsonCreator
        public CreateBetRequest(@JsonProperty("amount") Integer amount) {
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }
    }

    public static class LinkWithMethod extends Link {
        private HttpMethod method;

        public LinkWithMethod(String href, HttpMethod method) {
            super(href);
            this.method = method;
        }

        public LinkWithMethod(String href, String rel, HttpMethod method) {
            super(href, rel);
            this.method = method;
        }

        public LinkWithMethod(UriTemplate template, String rel, HttpMethod method) {
            super(template, rel);
            this.method = method;
        }

        public HttpMethod getMethod() {
            return method;
        }

    }

    public static class BetIndex extends ResourceSupport{
        private List<Bet> embeds;

        public BetIndex(List<Bet> embeds, List<Link> links) {
            super();
            this.embeds = embeds;
            this.add(links);
        }

        @JsonProperty("_embedded")
        public List<Bet> getEmbeds() {
            return embeds;
        }
    }

    public static class Bet {
        private int amount;

        @JsonCreator
        public Bet(@JsonProperty("amount") int amount) {
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }

    }
}
